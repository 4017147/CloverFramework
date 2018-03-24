package com.cloverframework.core.factory;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.cloverframework.core.course.Course;
import com.cloverframework.core.course.CourseMethod;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.domain.annotation.Domain;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class EntityFactory {
	/**  
	 * 保存线程id和course的map，这里使用的是ConcurrentHashMap，因此修改操作是线程安全的，但是get和某些操作可能会交迭，
	 * 在确保线程和course是一对一的情况下(正常应用情况下)，不会存在线程安全问题，在多对一的情况下就必需进行同步。<p>
	 * 另外在使用不同的web服务容器时，需要考虑容器的并发规则，通常一次业务过程的创建对应的某个（java）线程应该是确定的，否则不适用。
	 * <p>该map的初始大小为1000，实际应根据web服务容器的并发线程数量（如线程池的数量）来设置，比它少稍大一点并且是2的倍数
	 **/
	//TODO 这里将要考虑分散为多个map的方案
	private static ConcurrentHashMap<Long, Object[]> courses = new ConcurrentHashMap<Long, Object[]>(1000);
	private static ConcurrentHashMap<String, Object> staple = new ConcurrentHashMap<String, Object>(100);
	private static EntityFactory factory;
	private static Enhancer enhancer;
	private static EntityMethodInterceptor interceptor;
	{
		if(interceptor==null) 
			interceptor = new EntityMethodInterceptor();
		if(enhancer==null) {
			enhancer = new Enhancer();
			enhancer.setUseCache(true);
			enhancer.setCallback(interceptor);
		}
	}
	private EntityFactory() {}
	
	/**
	 * 
	 * cglib动态代理方法拦截器内部类
	 *
	 */
	
	public static final class EntityMethodInterceptor implements MethodInterceptor{
		protected EntityMethodInterceptor() {}
		
		@Override
		public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
			Thread t = Thread.currentThread();
			//产生全限定名
			String literalName = arg0.getClass().getSuperclass().getName()+"."+arg1.getName();
			//调整该方法的位置需要修改length的值，每多一个上级方法，length-1
			factory.setLiteral(literalName,t,t.getStackTrace().length);
			arg3.invokeSuper(arg0,arg2);
			//TODO 返回null还是原来方法的结果
			return null;
		}
	}
	
	/**
	 * 将截获的方法字面值写入当前course中
	 * @param literalName 调用的实体类的类名+getter方法名
	 * @param t 正在执行的线程
	 * @param length 该线程的方法栈长
	 * @throws Exception 
	 */
	private void setLiteral(String literalName,Thread t,int length) throws Exception {
		Object[] courseInfo = null;
		Course course = null;
		//System.out.println(length);
		if((courseInfo = courses.get(t.getId()))!=null && ((Thread)courseInfo[1])==t) {
			byte le = 1;//执行代理类的get方法时线程的方法栈长跟course方法栈长的差值，不同的虚拟机平台可能得到不同的值，则根据实际情况调整
			if((course = (Course) courseInfo[0])!=null) {
				if(course.getStatus()==Course.LAMBDA) 
					le = 2;
				else if(course.getStatus()==Course.METHOD)
					le = 1;
				if(length-le==(Integer)courseInfo[2]) {
					if(course.getStatus()<Course.WAIT) 
						//TODO 抛出异常存在什么影响
						throw new Exception("Course status error:"+course.getStatus());
//					if(course.getStatus()>=Course.LAMBDA_TE) {
//						CourseMethod.addLiteral_te(literalName,course,interceptor);
//						CourseMethod.addLiteral(literalName,course,interceptor);
//						System.out.println(literalName);
//					}else {
					CourseMethod.addLiteral(literalName,course,interceptor);
					//System.out.println(literalName);
					
				}
			}		
		}
	}

	public static EntityFactory getInstance() {
		if (factory==null) {
			factory = new EntityFactory();
			return factory;
		}else 
			return factory;
		
	}

	/**
	 * 
	 * @param threadId 创建或调用course的线程id
	 * @param proxy 创建的course
	 * @param length 该线程方法的栈长
	 * @throws Exception 
	 */
	public static Course putCourse(Course course,Thread t,int length) {
		Object[] CourseInfo = new Object[3];
		CourseInfo[0] = course;
		CourseInfo[1] = t;
		CourseInfo[2] = length;
		courses.put(t.getId(), CourseInfo);
		return (Course) courses.get(t.getId())[0];
	}

	public static void removeCourse(long threadId) {
		for(Entry<Long, Object[]> entry:courses.entrySet()) {
			//System.out.println("remove:"+entry.getKey()+":"+entry.getValue());
		}
		
		courses.remove(threadId);
		for(Entry<Long, Object[]> entry:courses.entrySet()) {
			System.out.println("还有"+entry.getKey()+":"+entry.getValue());
		}
	}
	
	/**
	 * 获取基本的实体类，这些实体类和普通的一样，都是不可变的bean，并且都是空白的，仅作为模板元素提供字面意思。
	 * 通过调用这些实体类的getter，动态代理会根据当前的Course调用，来将getter方法填入Course中。
	 * 这些模板元素都只会缓存一份
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getStaple(Class<E> E) {
		//TODO 重写动态代理类的toString方法
		String className = E.getName();
		if(staple.get(className)==null) {
			enhancer.setSuperclass(E);
			E e = (E) enhancer.create();
			staple.put(className, e);
			return e;		
		}else 
			return (E) staple.get(className);
		
	}
	/**
	 * 提供应用层使用，应用层根据表示层传递过来的对象，不必知道对应的实体，只需提供请求的service给工厂创建对应的实体并传给service
	 * @param entityClass
	 * @param service
	 * @return
	 */
	public <E> E createEntity(Class<E> entityClass,DomainService service) {
		if (isMatchDomain(entityClass, service.getClass())) {
			try {
				return entityClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	

	/**
	 * 解析Course中使用的实体类型是否符合领域服务操作范围，如不符合返回null
	 */
	public Course resolve(Course process) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 将仓储返回的结果重新装配到Course
	 */
	public Course assemble(Course process) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 判断给定的一个实体类和一个领域服务所属的领域是否符合或兼容
	 */
	public static boolean isMatchDomain(Class<?> entityClass, Class<?> serviceClass) {
		if(entityClass.isAnnotationPresent(Domain.class)&&serviceClass.isAnnotationPresent(Domain.class)) {
			if(serviceClass.getAnnotation(Domain.class).toString().equals(entityClass.getAnnotation(Domain.class).toString())) {
				//System.out.println(entityClass+" "+serviceClass);
				return true;			
			}			
		}
		return false;
	}

	/**
	 * 感知调用者，该方法用于获取调用改方法的领域服务类名;
	 * 由于Thread.currentThread().getStackTrace()开销较大而且是非常规的方法，而且受到native方法影响较大，不同平台虚拟机可能返回不同的栈顺序
	 * 因此如不确定虚拟机等平台特性情况下，不建议使用该方法。
	 * @param depth 方法调用的栈深度，该方法最低为2，每增加一层方法，深度+1
	 * @return
	 */
	public String getCallerServiceName(int depth) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for(int i = 0;i<elements.length;i++) {
			System.out.println(elements[depth].toString());
		}
		return elements[depth].getClassName();
	}

	/**
	 * 
	 * @return 返回当前所有在生成的Course的数量估计值
	 */
	public static int getCoursesSize() {
		return courses.size();
	}
	
	/**
	 * 
	 * @return 返回当前所有缓存的实体样板实例的数量估计值
	 */
	public static int getStaplesSize() {
		return staple.size();
	}

}
