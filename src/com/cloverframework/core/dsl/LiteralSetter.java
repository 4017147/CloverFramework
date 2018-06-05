package com.cloverframework.core.dsl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.cloverframework.core.dsl.interfaces.Constant;
import com.cloverframework.core.util.lambda.Literal;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public interface LiteralSetter extends Constant{
	
	
	/**
	 * 调整该方法的位置需要修改length的值，每多一个上级方法调用，var+1
	 */
	default void beginLiteral(AbstractCourse course,int var) {
		Thread t = Thread.currentThread();
		System.out.println(t.getStackTrace().length+"-"+var+" "+course.type);
		setLevel(course==null?0:course.hashCode(),t.getStackTrace().length-var);
	}
	
	default int getThread() {
		return Thread.currentThread().getStackTrace().length;
	}
	
	default void setThread(int var) {
		setLevel(0,Thread.currentThread().getStackTrace().length-var);
	}
	
	default int setLevel(int hashCode, int length) {
		return Processor.setLevel(hashCode, length);
	}
	
	default int addLevel(int hashCode, int length) {
		return Processor.addLevel(hashCode, length);
	}
	
	default void reSetLevel(int hashCode) {
		Processor.reSetLevel(hashCode);
	}
	
	default void setLiteralModel(int mo,int var) {
		Processor.setModel(mo, var);
	}
	
	
	default List<String> getLiteral() {
		return Processor.literal.get();
	}
	
	default List<String> getLiteral_te() {
		return Processor.literal_te.get();
	}

	/**
	 * 一系列的字面值参数的开头，例如：如果在GET($(),user.getName(),user.getId())之前，
	 * 在course方法以外的地方使用了User.getName()等方法，
	 * 则$()方法是必需出现在参数的第一位，以抹除之前无关的字面值。
	 * @return null
	 */
	default public Object $() {
		if(getLiteral()!=null && getLiteral().size()>0)
			getLiteral().clear();
		return null;
	}
	
	/**
	 * 方法引用，以lambda的方式提供方法字面参数，
	 * 如GET($(user::getName))相当于GET(user.getName()),当有多个方法字面值需要获取，最好用该方式。<p>
	 * 例如：GET($(user::getName,user::getId,user,user::getCode),首项为lambda的情况下不需要$()。
	 * 如果字面值中还有user.getName()这样的方法引用，那么lambda的任意一个表达式必需写在字面值参数的第一位。
	 * 
	 * @param lt 实体类的lambda表达式，如user::getName，建议采用此种语法而非()->user.getName(),
	 * 因为这种写法会带来隐患，比如可在lambda中执行多个句柄，这样会令字面值参数跟期望的不一致，
	 * 尽管Course已经尽力避免这种情况，但是无法完全阻止通过这种手段输入，这是由于java语言机制限制了，因此在实际使用中
	 * 需要多加留意。并且，在其他层面（如domainMatch、EntityFactory）会对该问题进行原则上的处理。
	 * @return null
	 */
	default public Object $(Literal ...lt) {
		setLiteralModel(LAMBDA, 1);
		for(Literal li:lt) {
			li.literal();
		}
		setLiteralModel(METHOD, -1);
		return null;
	}
	
	/**
	 * 可获取三元运算结果的字面值
	 * @param obj @see {@link CourseProxy#$(Literal...)}
	 * @return
	 */
	default public Object te(Literal lt) {
		setLiteralModel(LAMBDA_TE, 1);
		lt.literal();
		setLiteralModel(METHOD, -1);
		return null;
	}
	
	default public Object te(Object obj) {
		int size = getLiteral().size();
		getLiteral_te().add(getLiteral().get(size-1));
		getLiteral().remove(size-1);
		getLiteral().remove(size-2);
		return AbstractCourse.Te.te;
	}
	
	
	default <E> E getStaple(Class<E> E) {
		return Processor.getStaple(E);
	}

	class EntityMethodInterceptor implements MethodInterceptor{
		protected EntityMethodInterceptor() {}
		
		@Override
		public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
			Thread t = Thread.currentThread();
			//产生全限定名,不应发生原生返回值修改
			String literalName = arg0.getClass().getSuperclass().getName()+"."+arg1.getName();
			//调整该方法的位置需要修改length的值，通过基准测试来判断该值是否与DSL节点构造方法栈长对应
			//不同的虚拟机平台可能得到不同的值，则根据实际情况调整
			Processor.setLiteral(literalName,t.getStackTrace().length);
			arg3.invokeSuper(arg0,arg2);
			//TODO 返回null还是原来方法的结果
			return null;
		}
	}

	class Processor{
		private static ThreadLocal<Integer> model = new ThreadLocal<Integer>();
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
		}
		
		private Processor() {}
		
		static void setLiteral(String literalName,int length) throws Exception {
			System.out.println(length+literalName);
			if(Processor.literal.get()==null)
				Processor.literal.set(new ArrayList<>());
			if(Processor.literal_te.get()==null)
				Processor.literal_te.set(new ArrayList<>());
			if(model.get()==null)
				model.set(METHOD);
			if(lengthMap.get()==null) 
				lengthMap.set(new int[2]);
			int courseLength = lengthMap.get()[1];
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
		
		static int addLevel(int hashCode, int length) {
			lengthMap.get()[0] = hashCode;
			lengthMap.get()[1] = lengthMap.get()[1]+length;
			return lengthMap.get()[1];
		}
		
		static int setLevel(int hashCode, int length) {
			lengthMap.get()[0] = hashCode;//hashCode用于在course中判断是否为当前course
			lengthMap.get()[1] = length;
			return lengthMap.get()[1];
		}
	
		static void reSetLevel(int hashCode) {
			if(lengthMap.get()[0]==hashCode) {
				lengthMap.get()[0] = 0;
				lengthMap.get()[1] = 0;				
			}
		}
		
		static int getStaplesSize() {
			return staple.size();
		}
		
		
		static void setModel(int mo,int var) {
			model.remove();
			model.set(mo);
			addLevel(lengthMap.get()[0],var);
		}
		
	}
	
	
}
