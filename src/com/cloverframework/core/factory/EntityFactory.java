package com.cloverframework.core.factory;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.domain.annotation.Domain;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.interfaces.Constant;

public final class EntityFactory implements Constant{
	
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

}
