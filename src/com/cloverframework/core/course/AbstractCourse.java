package com.cloverframework.core.course;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.cloverframework.core.factory.EntityFactory;
import com.domain.DomainService;

/**
 * 这是course的抽象的父类，定义了一种链表结构属性，并实现了大部分基础特性
 * @author yl
 * 
 * 
 */
public abstract class AbstractCourse<T> {
	DomainService domainService;//上级传递
	
	/**course代理*/
	CourseProxy proxy;//上级传递
	
	/**节点元素*/
	private Object[] elements;
	
	/**父节点*/
	AbstractCourse<?> parent;
	
	/**字面列表*/
	List<String> literalList;//上级传递
	
	/**是否输出simpleName */
	public volatile boolean condition1;//根传递，处于性能考虑没有使用引用类型，如果是引用类型则应该上级传递
	
	/**是否输出颜色 */
	public volatile boolean condition2;//根传递
	
	/**待填充*/
	public static final byte WAIT 		= 0;
	/**添加字面值(从lambda)*/
	public static final byte LAMBDA 	= 1;
	/**添加字面值(从方法)*/
	public static final byte METHOD 	= 2;
	/**添加字面值(从lambda三元)*/
	public static final byte LAMBDA_TE 	= 3;
	/**正在填充*/
	public static final byte FILL 		=-1;
	/**关闭*/
	public static final byte END 		=-2;
	/**异常*/
	public static final byte ERROR 		=-3;
	
	/**
	 * 表示该course当前状态
	 * 在一个线程中，proxy创建course和动态代理获取字面值方法是按顺序执行的，也就是当前的status为正常，
	 * 如果在这些方法获取到的status异常，则意味着其他线程发生了不可预料的错误而被终止，
	 * 但是集合中的course可能并未移除，当前线程如果复用了相同的ID，有可能发生误读，
	 * 这一般发生在一个course中断后，另一个course开启之前，尽管这个概率是很低的。
	 * 
	 */
	volatile byte status = WAIT;//上级传递
	
	
	/*----------------------private method-------------------- */
	
	
	
	/**
	 * 设置节点的元素，该方法是父类委托子类的构造方法调用的。
	 * 如果根节点status异常，则不会执行，否则正常执行并刷新根节点的status。
	 * 执行后都会将字面列表清空
	 * @param elements
	 */
	protected void setElements(Object... elements) {
		try {
			status = parent==null?null:parent.status;
			//TODO 该异常情况下如何处理
			if(status>=WAIT) {
				status = FILL;
				literalList = parent==null?literalList:parent.literalList;
				domainService = parent==null?domainService:parent.domainService;
				proxy = parent==null?proxy:parent.proxy;
				this.elements = fill(elements,literalList,domainService);
				parent.status = status = WAIT;
			}
		}finally {
			if(literalList!=null) 
				literalList.clear();
		}
	}

	@SuppressWarnings("rawtypes")
	private void setCondition(AbstractCourse parent) {		
		AbstractCourse p = parent;
		if(parent==null)
			p = this;
		for(;p!=null;) {
			if(p.condition1==true&&p.condition2==true) {
				condition1 = p.condition1;
				condition2 = p.condition2;
				break;
			}
			p = (AbstractCourse) p.parent;
		}
	}
	
	/**
	 * 销毁该Course
	 */
	protected void destroy() {}


	/**
	 * 将领域实体的getter方法字面值填充到element数组中
	 * 1、如果数组元素为null则填充（如果字面值列表元素的next存在）
	 * 2、如果数组元素为领域实体，如果为合法的领域实体则添加，如果不合法则移除。
	 * 3、如果该元素不是实体，则进行字面值填充。
	 * 
	 * @param elements
	 * @param literalList
	 * @param domainService
	 */
	private Object[] fill(Object[] elements,List<String> literalList,DomainService domainService) {
		if(elements.length>0 && literalList.size()>0) {
			Object[] temps = new Object[elements.length];
			if(elements.length<literalList.size()) {
				temps = new Object[literalList.size()];
			}
			byte k = 0,i = 0,t = 0;
			for(;i<elements.length;i++) {
				if(elements[i]!=null && !EntityFactory.isMatchDomain(elements[i].getClass(), domainService.getClass())) {
					if(k<literalList.size()) {
						temps[t] = literalList.get(k);
						k++;
						t++;
					}
				}else if(elements[i]!=null){
					temps[t] = elements[i];
					t++;
				} 
			}
			for(;t<temps.length;t++) {//将剩余的字面值填充（如果还有剩余）
				if(k>=literalList.size()) 
					break;
				temps[t] = literalList.get(k);
				k++;
			}
			return Arrays.copyOf(temps, t);//去除为null的无效下标
		}
		return null;
	}


	/**
	 * 打印节点元素，java.lang包类型会直接输出，如果是String类型则输出类型.方法名，其他实体类型则输出类型名
	 * @param course
	 * @param elements
	 * @param condition1 是否输出simpleName
	 * @param condition2 是否输出颜色,改颜色通过ANSI转义序列定义
	 * @return
	 */
	private String DataString(AbstractCourse<T> course,Object[] elements,boolean condition1,boolean condition2) {
		StringBuilder builder = new StringBuilder(56);
		if(condition2)
			builder.append("\n\u001b[94m").append(course.getClass().getSimpleName()).append("\u001b[0m ");
		else
			builder.append("\n").append(course.getClass().getSimpleName());
		if (elements!=null ) {
			String comma = ", ";
			String blank = " ";
			if(elements.length>8)
				comma = ",\n";
			for (int i = 0; i < elements.length; i++) {
				if(elements[i]==null) {
					builder.append(comma);
					continue;					
				}
				if(i==0)
					builder.append(blank);//首个元素缩进
				else
					builder.append(comma);	
				if (elements[i].getClass().getPackage() == Package.getPackage("java.lang")) {
					if(elements[i].getClass()==String.class){
						if(condition1) {
							String fullName = elements[i].toString();
							//获取包括类型和属性名的simpleName
							//防止substring内存占用
							String simpleName = new String(fullName.substring(fullName.lastIndexOf(".",fullName.lastIndexOf(".")-1)+1, fullName.length()).replace(".get", "."));
							builder.append(simpleName).append(blank);	
						}else
							builder.append(elements[i]).append(blank);												
					}else
						builder.append(elements[i]).append(blank);	
				}else {
					String simpleName = elements[i].getClass().getSimpleName();
					int index = simpleName.indexOf("$$");//代理类类名需截取
					builder.append(new String(simpleName.substring(0, index==-1?simpleName.length():index))).append(blank);					
				}
			}
		}
		return builder.toString();
	}


	/**
	 * 打印子节点
	 * @param course
	 * @return
	 */
	private String fieldString(AbstractCourse<T> course) {
		StringBuilder builder = new StringBuilder();
		if (this != null) {
			Field[] fields = course.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					fields[i].setAccessible(true);
					Object value = fields[i].get(course);
					if (value != null) 
						builder.append(fields[i].getName()).append(':').append(value.toString()).append(' ');
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}


	
	/*----------------------public method-------------------- */
	
	
	
	public AbstractCourse() {}
	
	protected void addLiteral(String methodName) {
		if(literalList.size()>49)
			System.out.println(this.literalList.size());
		else
			this.literalList.add(methodName);			
	}
	
	public Object getElements() {
		return elements;
	}

	
	/**
	 * 结束当前的一条course语句，则该course不可再添加语句，
	 * 并且执行end方法在大多情况下都是必须的，如果没有正常的执行end，
	 * 会导致当前定义的course被下一次操作快速抛弃而不会进行缓存
	 */
	public void END() {
		try {
			if(status!=END) {
				if (parent!=null) 
					parent.END(); 
				else {
					status = END;
					proxy.END();
				}				
			}
		}
		finally {
			proxy = null;
			literalList = null;
			EntityFactory.removeCourse(Thread.currentThread().getId());				
		}
	}

	/**
	 * 直接END()并执行当前对象course语句
	 * @return
	 */
	public Object execute() {
		CourseProxy cp = proxy;
		END();
		return cp.executeOne();
	}
	
	public byte getStatus() {
		return status;
	}
	
	/**
	 * 提供一个该course的结构的字面描述，为调试提供方便，实际过程和所见描述的并不能画上等号。
	 * 不能在内部类初始化方法中调用,因为this
	 */
	@Override
	public String toString() {
		setCondition(parent);
		return DataString(this,elements,condition1,condition2) + fieldString(this);
	}

	
}
