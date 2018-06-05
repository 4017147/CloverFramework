package com.cloverframework.core.dsl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.interfaces.CourseOperation;
import com.cloverframework.core.dsl.interfaces.CourseProxyInterface;
import com.cloverframework.core.exceptions.CourseIdException;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.factory.CourseFactory;
import com.cloverframework.core.repository.CourseRepository;
import com.cloverframework.core.util.interfaces.CourseType;
import com.cloverframework.core.util.interfaces.ELType;
import com.cloverframework.core.util.lambda.DSLFunction;
/**
 * 泛型C extends AbstractCourse，内部统称course，而对于测试和用户可称为DSL
 * course代理提供了面向用户的course操作和管理方法，通常使用该类创建业务过程普适的course，
 * 该类大部分方法是线程不安全的。
 * @author yl
 *
 */
public class CourseProxy<T,C extends AbstractCourse> implements CourseOperation<C>,CourseProxyInterface<T,C>,LiteralSetter,SonCreator,ELType{
	
	/**最后产生的course对象，无论什么方法，要求每次产生新的course都必须移除旧的course*/
	C newest;
	
	/**share区，用于缓存course对象*/
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
	protected C addCourse(String id,boolean isFork,int option) {
		setThread(1);
		C old = getCourse(id);
		C newc = createCourse();
		newc = initCourse(id,newc,this,option);//必需先经过初始化再入缓存
		if(old==null){
			setCurrCourse(newc);
			if(isFork==true||id.equals("")) {
				newc.isFork = true;
				return newc;
			}
			setCourse(newc.id, newc);	
			return newc;
		}
		if(old!=null && old.getStatus()<=LOCKED) {
			setCurrCourse(newc);
			//newc.next = old.next;
			return newc;
		} 
		return old;
	}
	

	/**
	 * 创建异步执行对象并设置course异步的result
	 * @param <K>
	 * @param course
	 * @param t
	 * @return
	 */
	private <K> CompletableFuture<CourseResult> applyFutureResult(C course,K k){
		return CompletableFuture.supplyAsync(()->{
			setObject(k,executeGeneral(course));
			return course.getResult();
		});
	}

	/**
	 * 闭包变量final赋值
	 * @param <K>
	 * @param obj
	 * @param t
	 */
	private <K> void setObject(K k,Object obj) {
		k = (K) obj;
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
	 * 创建一个DSL头部，通过该方法可以链式执行GET ADD PUT REMVE等方法
	 * @return 返回一个根节点
	 */
	public C Master() {
		return addCourse("",false,UNLOCKED);
	}
	
	/**
	 * 如果缓存的course不存在则执行DSL，否则返回已缓存的course
	 * @param id
	 * @param f
	 * @return
	 */
	public C Master(String id,DSLFunction f) {
		if(getCourse(id)==null)
			f.build(id);
		return getCourse(id);
	}
	
	
	/**
	 * 创建一个DSL头部，通过该方法可以链式执行GET ADD PUT REMVE等方法。
	 * 
	 * @param id 这个course的标识，不能包含空格
	 * @return
	 */
	public C Master(String id) {
		String reg = "^[\\S]*$";
		if(id!=null && id.matches(reg))
			return addCourse(id,false,UNLOCKED);
		else 
			throw ExceptionFactory.wrapException("Course id should not be empty or contains space", new CourseIdException(id));
	}
	
	/**
	 * 带缓存的FORK，
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照Master(id)模式进行
	 * @param id
	 * @return
	 */
	public C BranchM(String id) {
		C ori = getCourse(id);
		if(ori!=null) {
			C fork = addCourse(id+"_FM_"+System.currentTimeMillis(),false,UNLOCKED);
			fork.isForkm = true;
			cross(ori,fork);
			return fork;
		}else {
			return addCourse(id+"_NFM_"+System.currentTimeMillis(),false,UNLOCKED);
		}
	}
	
	/**
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照Master()模式进行
	 * @param id
	 * @return
	 */
	public C Branch(String id) {
		C ori = getCourse(id);
		if(ori!=null) {
			C fork = addCourse(id+"_F_"+System.currentTimeMillis(),true,UNLOCKED);
			cross(ori,fork);
			return fork;
		}else {
			return addCourse(id+"_NF_"+System.currentTimeMillis(),true,UNLOCKED);
		}		
	}
	
	/**
	 * 内部使用，如果master已存在，则返回新建fork，否则返回新建master
	 * @param id
	 * @param var
	 * @return
	 */
	private C Branch(String id,int var) {
		C ori = getCourse(id);
		if(ori!=null) {
			C fork = addCourse(id+"_F_"+System.currentTimeMillis(),true,UNLOCKED);
			cross(ori,fork);
			return fork;
		}else {
			C master = addCourse(id,true,UNLOCKED);
			return master;
		}		
	}
	/**
	 * 将branch和master关联
	 * @param id
	 * @param course
	 * @return
	 */
	public C cross(C ori,C fork) {
		fork.origin = ori.next;
		return fork;
	}
	
	/**
	 * 接收course的回调参数，根据course的status执行对应的操作
	 */
	@Override
	public Object receive(C c,int option) {
		switch(option) {
		case rebase:return Branch(c.getId(),1);
		case UNLOCKED:unLock(c);break;
		case LOCKED:lock(c);break;
		case END:END(c);break;
		}
		return option;
	}

	
	/**
	 * 执行当前的一条course语句
	 * @return
	 */
	public T execute() {
		return execute(getCurrCourse());
	}
	
	@Override
	public T execute(C course) {
		return repository.query(course);
	}
	
	@Override
	public Object execute(String id) {
		C course = getCourse(id);
		return executeGeneral(course);
	}

	/**
	 * 通用执行方法
	 * @param course
	 * @return
	 */
	public Object executeGeneral(C course) {
		if(course!=null && course.next.type==CourseType.get) 
			return execute(course);
		else
			return commit(course);
	}
	
	/**
	 * 提交当前的一条course语句
	 * @return
	 */
	public int commit() {
		return commit(getCurrCourse());
	}

	@Override
	public int commit(C course) {
		return repository.commit(course);
	}

	
	@Override
	public T executeFuture() {
		T t = null;
		C course = getCurrCourse();
		if(course.getFutureResult()==null) {
			course.setResult(applyFutureResult(course,t));
		}
		return t;
	}

	@Override
	public int commitFuture() {
		int t = 0;
		C course = getCurrCourse();
		if(course.getFutureResult()==null) {
			course.setResult(applyFutureResult(course,t));
		}
		return t;
	}

	/**
	 * 将当前proxy对象移交仓储，仓储根据不用的proxy实例和泛化执行对应的操作
	 * @return
	 */
	public int push() {
		return repository.fromProxy(this);
	}
	
	public void lock(C course) {
		synchronized (course) {
			toEnd(course,LOCKED);
		}
	}
	
	public void unLock(C course) {
		synchronized (course) {
			toEnd(course,UNLOCKED);
		}
	}
	
	public void END(C course) {
		synchronized (course) {
			toEnd(course,END);
		}
	}
	
	private void toEnd(AbstractCourse course,int type) {
		course = getCourse(course.id);
		if(course!=null && course.type==CourseType.root) {
			do {
				if(course.getStatus()!=type) {
					course.setStatus(type);
				}
				if(course.sons!=null)
					for(AbstractCourse c:course.sons) {
						c.setStatus(type);
					}				
				course = course.next;
			}
			while (course.next!=null);			
		}				
	}
	
	
	
}
