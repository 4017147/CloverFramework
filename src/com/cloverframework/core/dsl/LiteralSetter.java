package com.cloverframework.core.dsl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public interface LiteralSetter{
	
	class EntityMethodInterceptor implements MethodInterceptor{
		protected EntityMethodInterceptor() {}
		
		@Override
		public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
			Thread t = Thread.currentThread();
			//产生全限定名,不应发生原生返回值修改
			String literalName = arg0.getClass().getSuperclass().getName()+"."+arg1.getName();
			//调整该方法的位置需要修改length的值，每多一个上级方法，length-1
			Processor.setLiteral(literalName,t.getStackTrace().length);
			arg3.invokeSuper(arg0,arg2);
			//TODO 返回null还是原来方法的结果
			return null;
		}
	}
	
	class Processor{
		private static Enhancer enhancer = new Enhancer();
		private static EntityMethodInterceptor interceptor = new EntityMethodInterceptor();
		static ThreadLocal<int[]> lengthMap = new ThreadLocal<int[]>();
		private static ConcurrentHashMap<String, Object> staple = new ConcurrentHashMap<String, Object>(100);
		
		/**方法字面值列表*/
		static ThreadLocal<List<String>> literal = new ThreadLocal<List<String>>();
		
		/**三元方法字面值列表*/
		static ThreadLocal<List<String>> literal_te = new ThreadLocal<List<String>>();
		
		static{
			enhancer.setUseCache(true);
			enhancer.setCallback(interceptor);
			lengthMap.set(new int[2]);
			literal.set(new ArrayList<>());
			literal_te.set(new ArrayList<>());
		}
		
		protected Processor() {}
		
		static void setLiteral(String literalName,int length) throws Exception {
			System.out.println(length+literalName);
			int courseLength = lengthMap.get()[1];
			//执行代理类的get方法时线程的方法栈长跟course方法栈长的差值，不同的虚拟机平台可能得到不同的值，则根据实际情况调整
			if(length==courseLength) {
				literal.get().add(literalName);
			}		
		}
		
		static <E> E getStaple(Class<E> E) {
			//TODO 重写动态代理类的toString方法,创建的对象要进行归零处理
			String className = E.getName();
			if(staple.get(className)==null) {
				enhancer.setSuperclass(E);
				E entity = (E) enhancer.create();
				staple.put(className, entity);
				return entity;		
			}else 
				return (E) staple.get(className);
			
		}
		
		public static int putCourse(AbstractCourse course, int length) {
			lengthMap.get()[0] = course.hashCode();
			lengthMap.get()[1] = length;
			return lengthMap.get()[1];
		}

		public static void removeCourse(int hashCode) {
			if(lengthMap.get()[0]==hashCode) {
				lengthMap.get()[0] = 0;
				lengthMap.get()[1] = 0;				
			}
		}
		
		public static int getStaplesSize() {
			return staple.size();
		}
		
		
	}
		
	default <E> E getStaple(Class<E> E) {
		return Processor.getStaple(E);
	}
	
	default int putCourse(AbstractCourse course, int length) {
		return Processor.putCourse(course, length);
	}
	
	default void removeCourse(int hashCode) {
		Processor.removeCourse(hashCode);
	}
	
	default List<String> getLiteral() {
		return Processor.literal.get();
	}
	
	default List<String> getLiteral_te() {
		return Processor.literal_te.get();
	}
	
	
}
