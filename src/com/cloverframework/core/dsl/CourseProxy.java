package com.cloverframework.core.dsl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.interfaces.CourseOperation;
import com.cloverframework.core.dsl.interfaces.CourseProxyInterface;
import com.cloverframework.core.exceptions.CourseIdException;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.factory.CourseFactory;
import com.cloverframework.core.repository.CourseRepository;
import com.cloverframework.core.util.interfaces.CourseType;
import com.cloverframework.core.util.lambda.DSLFunction;
/**
 * 泛型C extends AbstractCourse，内部统称course，而对于测试和用户可称为DSL
 * course代理提供了面向用户的course操作和管理方法，通常使用该类创建业务过程普适的course，
 * 该类大部分方法是线程不安全的。如果该对象处于线程池环境中，在丢弃的时候请使用reset或destory进行释放。
 * @author yl
 *
 */
public class CourseProxy<T,C extends AbstractCourse<C>> implements CourseOperation<C>,CourseProxyInterface<T,C>{
	
	/**最后产生的course对象*/
	C newest;
	
	/**course缓存，在缓存中每一个course和对应的id都应当是唯一的，不存在任何副本*/
	Map<String,C> shareSpace = new ConcurrentHashMap<String,C>();
	
	protected DomainService domainService;

	CourseRepository<T,C> repository;
	
	/**
	 * 异步执行course等待超时秒
	 */
	private static int getResultTimeout = 3;
	
	/**
	 * 创建对应的泛型course头部节点
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected C createCourse() {
		C newc = null;
		try {
			//获取父类泛型参数，默认第二个泛型参数为泛型course，没有参数或者没提供，使用默认的course
			Type type = this.getClass().getGenericSuperclass();
			if(type==Object.class) {
				newc = (C) new Course();
			}else {
				Type[] types = ((ParameterizedType)type).getActualTypeArguments();
				if(types.length<2)
					newc = (C) new Course();
				else
					newc = CourseFactory.create((Class<C>) types[1]);				
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return newc;
	}

	/**
	 * 该方法根据已有的course和新建course的状态决定是否缓存这些course。
	 * 如果shareSpace查找对应id的course状态为LOCKED，则返查找的course，
	 * 如果查找的course为null，则返回新建的course，如果新建的course为fork则不缓存
	 * @return
	 */
	protected C addCourse(String key,boolean isFork,int option) {
		//reSetLevel(0);
		C old = getCourse(key);
		if(old!=null&& old.getStatus()>LOCKED) {
			return old;
		}
		C newc = createCourse();
		newc = initCourse(key,newc,this,option);//必需先经过初始化再入缓存
		beginLiteral(newc,1);
		if(old!=null && old.getStatus()<=LOCKED) {
			setCurrCourse(newc);
			//newc.next = old.next;
			return newc;
		} 
		if(old==null){
			setCurrCourse(newc);
			if(isFork==true||key.equals("")) {
				newc.isFork = true;
				return newc;
			}
			setCourse(newc.id, newc);	
			return newc;
		}
		return null;
	}
	

	/**
	 * 创建异步执行对象并设置course异步的result
	 * @param <K>
	 * @param course
	 * @param t
	 * @return
	 */
	private CompletableFuture<Result<?>> applyAsyncResult(C course){
		return CompletableFuture.supplyAsync(()->{
			return repository.result(course);
		});
	}

	/**
	 * 内部使用，如果master已存在，则返回新建fork，否则返回新建master
	 * @param key
	 * @param var
	 * @return
	 */
	private C Branch(String key,int var) {
		C ori = getCourse(key);
		if(ori!=null) {
			C fork = addCourse(key+"_F_"+System.currentTimeMillis(),true,UNLOCKED);
			cross(ori,fork);
			return fork;
		}else {
			C master = addCourse(key,true,UNLOCKED);
			return master;
		}		
	}

	private void toEnd(AbstractCourse<?> course,int type) {
		//对于非缓存的course只会是unlock，因为本地线程中lock或者end是无意义的
		course = getCourse(course.id);
		if(course!=null && course.type==CourseType.root) {
			do {
				if(course.getStatus()!=type) {
					course.setStatus(type);
				}
				@SuppressWarnings("rawtypes")
				Iterator<AbstractCourse> i;
				if(course.sons!=null) {
					i = course.sons.iterator();
					while(i.hasNext()) {
						i.next().setStatus(type);
					}
				}			
				course = course.next;
			}
			while (course.next!=null);			
		}				
	}

	/**
	 * 将branch和master关联
	 * @param id
	 * @param course
	 * @return
	 */
	private C cross(C ori,C fork) {
		fork.origin = ori.next;
		return fork;
	}

	@Override
	public C getCurrCourse() {
		return newest;
	}
	
	@Override
	public C removeCurrCourse() {
		C old = getCurrCourse();
		newest = null;
		return old;
	}
	
	@Override
	public void setCurrCourse(C course) {
		newest = course;
	}
	
	@Override
	public C getCourse(String key) {
		return shareSpace.get(key);
	}
	
	@Override
	public void setCourse(String key,C course) {
		//需要根据不同的缓存存入key
		//TODO 容错处理
		if(getCourse(key)==null) 
			shareSpace.put(key,course);
	}
	
	@Override
	public C removeCourse(String key) {
		return shareSpace.remove(key);
	}
	
	/**
	 * 初始化course
	 */
	@Override
	public C initCourse(String id,C course,CourseProxyInterface<T,C> proxy,int status) {
		course.setId(id);
		course.setHead();
		course.proxy = proxy;
		course.setStatus(status);
		course.init(course);
		return course;
	}
	
	public CourseProxy() {}

	public CourseProxy(DomainService domainService) {
		this.domainService = domainService;
	}
	
	public Map<String, C> getShareSpace() {
		return shareSpace;
	}
	
	@Override
	public void setRepository(CourseRepository<T,C> repository) {
		this.repository = repository;
	}

	@Override
	public DomainService getDomainService() {
		return domainService;
	}

	@Override
	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}
	
	public static int getGetResultTimeout() {
		return getResultTimeout;
	}

	public static void setGetResultTimeout(int getResultTimeout) {
		CourseProxy.getResultTimeout = getResultTimeout;
	}

	/**
	 * 获取course list的友好信息
	 * @see CourseProxy#getInfo()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		for(String key:shareSpace.keySet()) {
			C course = shareSpace.get(key);
			sb.append(course.toString()+"\n");
		}
		return sb.toString();			
	}

	/**
	 * 打印course list的详细，如没必要，使用toString()
	 * @return
	 */
	public String getInfo() {
		return getCurrCourse().toString()+"\n";
	}

	
	
	
	/*------如果希望调用者只能以匿名类的形式使用，这些方法或重写应当为protected------*/
	
	
	/**
	 * 创建一个DSL头部，通过该方法可以链式执行GET ADD PUT REMVE等方法。
	 * 该方法创建的course并不会缓存，通常创建的course可能是多变的，即动态DSL。
	 * 每次创建或修改DSL都是一个全新的course。
	 * @return 返回一个根节点
	 */
	public C Master() {
		return addCourse("",false,UNLOCKED);
	}
	
	/**
	 * 如果缓存的course不存在则执行DSL，否则返回已缓存的course,
	 * 而不是重复执行DSL的创建，如：{@link #Master(String)}
	 * @param key
	 * @param f
	 * @return
	 */
	public C Master(String key,DSLFunction f) {
		if(getCourse(key)==null)
			f.build(key);
		return getCourse(key);
	}
	
	
	/**
	 * 创建一个DSL头部，通过该方法可以链式执行GET ADD PUT REMVE等方法。
	 * 通过该方法创建的course是无锁的，如果你确定多线程环境下仅仅是重复执行一条相同
	 * 的DSL创建代码，那么此course是线程安全的，因为器值和结果存于本地线程中；
	 * 如果不能保证这一点，那么你的course应当使用动态DSL来对待，不应该使用该方法创建
	 * @param key 这个course的标识，不能包含空格
	 * @return
	 */
	public C Master(String key) {
		String reg = "^[\\S]*$";
		if(key!=null && key.matches(reg))
			return addCourse(key,false,UNLOCKED);
		else 
			throw ExceptionFactory.wrapException("Course id should not be empty or contains space", new CourseIdException(key));
	}
	
	/**
	 * 带缓存的FORK，
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照Master(id)模式进行
	 * @param key
	 * @return
	 */
	public C BranchM(String key) {
		C ori = getCourse(key);
		if(ori!=null) {
			C fork = addCourse(key+"_FM_"+System.currentTimeMillis(),false,UNLOCKED);
			fork.isForkm = true;
			cross(ori,fork);
			return fork;
		}else {
			return addCourse(key+"_NFM_"+System.currentTimeMillis(),false,UNLOCKED);
		}
	}
	
	/**
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照Master()模式进行
	 * @param key
	 * @return
	 */
	public C Branch(String key) {
		C ori = getCourse(key);
		if(ori!=null) {
			C fork = addCourse(key+"_F_"+System.currentTimeMillis(),true,UNLOCKED);
			cross(ori,fork);
			return fork;
		}else {
			return addCourse(key+"_NF_"+System.currentTimeMillis(),true,UNLOCKED);
		}		
	}
	
	@Override
	public T execute(C course) {
		return repository.query(course);
	}
	
	public Object execute(String id) {
		C course = getCourse(id);
		return executeOrCommit(course);
	}

	/**
	 * 执行最后操作的course语句
	 * @return
	 */
	public T execute() {
		return execute(getCurrCourse());
	}

	@Override
	public int commit(C course) {
		return repository.commit(course);
	}

	/**
	 * 提交最后操作的course语句
	 * @return
	 */
	public int commit() {
		return commit(getCurrCourse());
	}

	public int commit(String key) {
		return commit(getCourse(key));
	}
	
	/**
	 * 通用执行方法
	 * @param course
	 * @return
	 */
	public Object executeOrCommit(C course) {
		if(course!=null && course.getNextType()==CourseType.get) 
			return execute(course);
		else
			return commit(course);
	}

	/**
	 * 异步执行最后操作的course语句
	 */
	@Override
	public void resultAsync(C course) {
		if(course.getAsyncResult()==null) {
			course.setResult(applyAsyncResult(course));
		}
	}
	
	public void resultAsync() {
		resultAsync(getCurrCourse());
	}
	
	public void resultAsync(String key) {
		resultAsync(getCourse(key));
	}
	
	/**
	 * 接收course的回调参数，根据course的status执行对应的操作
	 */
	@Override
	public Object receive(AbstractCourse<?> c,int option) {
		switch(option) {
		case ready		:ready(c);		break;
		case async		:resultAsync();	break;
		case UNLOCKED	:unLock(c);		break;
		case LOCKED		:lock(c);		break;
		case END		:end(c);		break;
		case commit		:return commit((C) c);
		case execute	:return execute((C) c);
		case rebase		:return Branch(c.getId(),1);
		}
		return null;
	}

	/**
	 * 对一个course进行上锁，上锁后该course不可追加节点和重新创建，
	 * 任何重新执行DSL的构造都将被创建为一个新的course并且不会缓存，
	 * 在共享模式下，建议上锁保确保线程安全。
	 * @param c
	 */
	public void lock(AbstractCourse<?> c) {
		synchronized (c) {
			toEnd(c,LOCKED);
		}
	}
	
	/**
	 * 解锁一个course，那么该course将回到共享状态，任何线程都可能修改其状态。
	 * @param c
	 */
	public void unLock(AbstractCourse<?> c) {
		synchronized (c) {
			toEnd(c,UNLOCKED);
		}
	}
	
	/**
	 * 将一个course标记为end状态，那么具有锁的作用并且包括value和result均无法被修改
	 * @param c
	 */
	public void end(AbstractCourse<?> c) {
		synchronized (c) {
			toEnd(c,END);
		}
	}
	
	public void ready(AbstractCourse<?> c) {
		//do nothing if extends by other
	}
	
	/**
	 * 获取缓存中的一个course
	 * @param key
	 * @return
	 */
	public C $(String key) {
		return getCourse(key);
	}
	
	/**
	 * 获取缓存中的一个course并返回其尾部节点
	 * @param key
	 * @return
	 */
	public C $E(String key) {
		C c = $(key);
		if(c.next!=null)
			c = (C) c.next;
		return c;
	}

	@Override
	public void reset() {
		newest = null;
		shareSpace.clear();	
	}

	@Override
	public void destroy() {
		reset();
		shareSpace = null;
		repository = null;
		domainService = null;
	}
	
	
}
