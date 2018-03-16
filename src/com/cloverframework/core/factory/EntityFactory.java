package com.clover.core.factory;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.clover.core.course.Course;
import com.clover.core.course.CourseMethod;
import com.domain.DomainService;
import com.domain.annotation.Domain;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class EntityFactory {
	/**  
	 * �����߳�id��course��map������ʹ�õ���ConcurrentHashMap������޸Ĳ������̰߳�ȫ�ģ�����get��ĳЩ�������ܻύ����
	 * ��ȷ���̺߳�course��һ��һ�������(����Ӧ�������)����������̰߳�ȫ���⣬�ڶ��һ������¾ͱ������ͬ����<p>
	 * ������ʹ�ò�ͬ��web��������ʱ����Ҫ���������Ĳ�������ͨ��һ��ҵ����̵Ĵ�����Ӧ��ĳ����java���߳�Ӧ����ȷ���ģ��������á�
	 * <p>��map�ĳ�ʼ��СΪ1000��ʵ��Ӧ����web���������Ĳ����߳����������̳߳ص������������ã��������Դ�һ�㲢����2�ı���
	 **/
	//TODO ���ｫҪ���Ƿ�ɢΪ���map�ķ���
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
	 * cglib��̬�������������ڲ���
	 *
	 */
	public static final class EntityMethodInterceptor implements MethodInterceptor{
		protected EntityMethodInterceptor() {}
		
		@Override
		public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
			Thread t = Thread.currentThread();
			String literalName = arg0.getClass().getSuperclass().getName()+"."+arg1.getName();
			//�����÷�����λ����Ҫ�޸�length��ֵ��ÿ��һ���ϼ�������length-1
			factory.setLiteral(literalName,t,t.getStackTrace().length);
			arg3.invokeSuper(arg0,arg2);
			//TODO ����null����ԭ�������Ľ��
			return null;
		}
	}
	
	/**
	 * ���ػ�ķ�������ֵд�뵱ǰcourse��
	 * @param literalName ���õ�ʵ���������+getter������
	 * @param t ����ִ�е��߳�
	 * @param length ���̵߳ķ���ջ��
	 * @throws Exception 
	 */
	private void setLiteral(String literalName,Thread t,int length) throws Exception {
		Object[] courseInfo = null;
		Course course = null;
		if((courseInfo = courses.get(t.getId()))!=null && ((Thread)courseInfo[1])==t) {
			byte le = 1;//ִ�д������get����ʱ�̵߳ķ���ջ����course����ջ���Ĳ�ֵ����ͬ�������ƽ̨���ܵõ���ͬ��ֵ�������ʵ���������
			if((course = (Course) courseInfo[0])!=null) {
				if(course.getStatus()==Course.LAMBDA || course.getStatus()==Course.LAMBDA_TE) 
					le = 2;
				else if(course.getStatus()==Course.METHOD)
					le = 1;
				if(length-le==(Integer)courseInfo[2]) {
					if(course.getStatus()<Course.WAIT) 
						//TODO �׳��쳣����ʲôӰ��
						throw new Exception("Course status error:"+course.getStatus());
					CourseMethod.addLiteral(literalName,course,interceptor);
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
	 * @param threadId ���������course���߳�id
	 * @param proxy ������course
	 * @param length ���̷߳�����ջ��
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
			System.out.println("remove:"+entry.getKey()+":"+entry.getValue());
		}
		
		courses.remove(threadId);
		for(Entry<Long, Object[]> entry:courses.entrySet()) {
			System.out.println("����"+entry.getKey()+":"+entry.getValue());
		}
	}
	
	/**
	 * ��ȡ������ʵ���࣬��Щʵ�������ͨ��һ�������ǲ��ɱ��bean�����Ҷ��ǿհ׵ģ�����Ϊģ��Ԫ���ṩ������˼��
	 * ͨ��������Щʵ�����getter����̬�������ݵ�ǰ��Course���ã�����getter��������Course�С�
	 * ��Щģ��Ԫ�ض�ֻ�Ỻ��һ��
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getStaple(Class<E> E) {
		//TODO ��д��̬�������toString����
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
	 * �ṩӦ�ò�ʹ�ã�Ӧ�ò���ݱ�ʾ�㴫�ݹ����Ķ��󣬲���֪����Ӧ��ʵ�壬ֻ���ṩ�����service������������Ӧ��ʵ�岢����service
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
	 * ����Course��ʹ�õ�ʵ�������Ƿ����������������Χ���粻���Ϸ���null
	 */
	public Course resolve(Course process) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ���ִ����صĽ������װ�䵽Course
	 */
	public Course assemble(Course process) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * �жϸ�����һ��ʵ�����һ��������������������Ƿ���ϻ����
	 */
	public static boolean isMatchDomain(Class<?> entityClass, Class<?> serviceClass) {
		if(entityClass.isAnnotationPresent(Domain.class)&&serviceClass.isAnnotationPresent(Domain.class)) {
			if(serviceClass.getAnnotation(Domain.class).toString().equals(entityClass.getAnnotation(Domain.class).toString())) {
				System.out.println(entityClass+" "+serviceClass);
				return true;			
			}			
		}
		return false;
	}

	/**
	 * ��֪�����ߣ��÷������ڻ�ȡ���øķ����������������;
	 * ����Thread.currentThread().getStackTrace()�����ϴ�����Ƿǳ���ķ����������ܵ�native����Ӱ��ϴ󣬲�ͬƽ̨��������ܷ��ز�ͬ��ջ˳��
	 * ����粻ȷ���������ƽ̨��������£�������ʹ�ø÷�����
	 * @param depth �������õ�ջ��ȣ��÷������Ϊ2��ÿ����һ�㷽�������+1
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
	 * @return ���ص�ǰ���������ɵ�Course����������ֵ
	 */
	public static int getCoursesSize() {
		return courses.size();
	}
	
	/**
	 * 
	 * @return ���ص�ǰ���л����ʵ������ʵ������������ֵ
	 */
	public static int getStaplesSize() {
		return staple.size();
	}

}
