package com.cloverframework.core.course;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.factory.EntityFactory;
import com.cloverframework.core.util.CourseData;
import com.cloverframework.core.util.CourseOpt;
import com.cloverframework.core.util.CourseType;
import com.cloverframework.core.util.ELOperation;
import com.cloverframework.core.util.Jutil;

/**
 * 定义了一种双向链表结构属性，并实现了大部分基础特性,需要注意的是，
 * 当status属性级别低于或等于END，对Elements和status的任何操作都是无效的。
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
	
	/**方法字面值列表*/
	List<String> literal;//上级传递
	
	/**三元方法字面值列表*/
	List<String> literal_te;
	
	/**节点类型*/
	protected String type;
	
	/**操作类型*/
	protected String optype;
	
	/**前级*/
	AbstractCourse<?> previous;
	
	/**后级*/
	AbstractCourse<?> next;
	
	/**父级*/
	AbstractCourse<?> parent;
	
	/**子级*/
	AbstractCourse<?> son;
	
	/**是否是一个子级*/
	protected boolean isSon;
	
	/**查询字段*/
	List<String> fields;
	
	/**查询类型*/
	Set<String> types;
	
	/**查询对象*/
	List<Object> entities;
	
	/**查询值*/
	List<Object> values;
	
	/**json输出工具*/
	Jutil jutil;
	
	/**json格式内容*/
	String jsonString;
	
	/**包装用于json格式化的数据*/
	CourseData courseData;
	
	/**节点类型接口*/
	static CourseType courseType;
	
	/**操作类型接口*/
	static CourseOpt opt;
	
	/** 是否是一个fork*/
	protected boolean isFork;
	
	/** 是否是一个forkm*/
	protected boolean isForkm;
	
	/**fork模式*/
	private String model;
	
	/** 基线course*/
	protected AbstractCourse<?> origin;

	/**是否输出simpleName */
	public volatile boolean condition1;//根传递
	
	/**是否输出颜色 */
	public volatile boolean condition2;//根传递
	
	/**正在填充*/
	public static final byte FILL 		=-1;
	/**关闭*/
	public static final byte END 		=-2;
	/**异常*/
	public static final byte ERROR 		=-3;
	/**待填充*/
	public static final byte WAIT 		= 0;
	/**添加字面值(从lambda)*/
	public static final byte LAMBDA 	= 1;
	/**添加字面值(从方法)*/
	public static final byte METHOD 	= 2;
	/**添加字面值(从lambda三元)*/
	public static final byte LAMBDA_TE 	= 3;
	/**添加字面值(三元)*/
	public static final byte TE 		= 4;
	
	
	/**三元引用的返回标识*/
	enum Te{te}
	
	/**
	 * 表示该course当前状态
	 * 在一个线程中，proxy创建course和动态代理获取字面值方法是按顺序执行的，也就是当前的status为正常，
	 * 如果在这些方法获取到的status异常，则意味着其他线程发生了不可预料的错误而被终止，
	 * 但是集合中的course可能并未移除，当前线程如果复用了相同的ID，有可能发生误读，
	 * 这一般发生在一个course中断后，另一个course开启之前，尽管这个概率是很低的。
	 * 
	 */
	private volatile byte status = WAIT;//上级传递
	


	/*----------------------private method-------------------- */

	private void init(AbstractCourse<?> course) {
		literal = course==null?literal:course.literal;
		literal_te = course==null?literal_te:course.literal_te;
		domainService = course==null?domainService:course.domainService;
		proxy = course==null?proxy:course.proxy;
		isFork = course==null?isFork:course.isFork;
		isForkm = course==null?isForkm:course.isForkm;
		origin = course==null?origin:course.origin;
	}
	
	/**
	 * 设置节点的元素，该方法是父类委托子类的构造方法调用的。
	 * 如果根节点status异常，则不会执行，否则正常执行并刷新根节点的status。
	 * 执行后都会将字面列表清空
	 * 通常情况下，值传入要先于方法返回值传入，
	 * 在传入节点参数的时候，按照直接值->实体->方法字面值->三元
	 * @param elements
	 */
	protected void setElements(Object... elements) {
		try {
			status = previous==null?null:previous.status;
			//TODO 该异常情况下如何处理
			if(status>=WAIT) {
				status = FILL;
				init(previous);
				if(isFork||isForkm && elements.length>0) 
					setModel(elements[0]);
				this.elements = fill(elements,literal,literal_te,domainService);
				if((isFork||isForkm) && origin!=null) 
					if(model!=null) {
						if(origin.type==this.type) {
							this.elements = ELOperation.mergeElements((Object[])origin.getElements(), this.elements,model);	
							origin = origin.next;
						}else {
							isFork = false;
							isForkm = false;
						}
					}else {
						if(this.elements==null)
							this.elements = origin.elements;
						origin = origin.next;
					}
				
				previous.status = status = WAIT;
					previous.next = this;//
				if(entities==null) {
					entities = new ArrayList<>();
				}
				buildData();
				//buildNodeString();
			}
		}finally {
			if(literal!=null) 
				literal.clear();
			if(literal_te!=null) 
				literal_te.clear();
		}
	}

	/**
	 * 设置fork模式值
	 * @param o
	 */
	protected void setModel(Object o) {
		if(o.getClass()==String.class) {
			for(String s:CourseProxy.Model) {
				if(s.equals(o)) {
					model = s;
					break;
				}
			}
		}
	}
	
	/**
	 * 设置son，一个节点只有一个son，son的next不作为当前节点的son，
	 * 并且会清除son原来的previous之间的关系，恢复当前节点跟previous的关系，
	 * 因为子节点的创建要先于当前节点
	 * @param object
	 */
	private void setSon(Object object) {
		AbstractCourse<?> son =  (AbstractCourse<?>)object;
		while(son.previous!=null) {
			if(son.isSon)
				break;
			son = son.previous;
		}
		this.son = son;
		son.previous.next = this;
		son.previous = null;
		son.parent = this;
		this.son = son;
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
			p = (AbstractCourse) p.previous;
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
	 * <p>
	 * 通常情况下，值要先于方法字面值填充，按照值->实体->方法字面值->三元
	 * 
	 * @param elements
	 * @param literal
	 * @param domainService
	 */
	private Object[] fill(Object[] elements,List<String> literal,List<String> literal_te,DomainService domainService) {
		Object[] temps = new Object[elements.length+literal.size()+literal_te.size()];
		if(elements.length>0) {
			byte a = 0;//跟踪literal
			byte b = 0;//跟踪literal_te
			byte e = 0;//跟踪elements
			byte t = 0;//跟踪temp
			for(;e<elements.length;e++) {			
				if(elements[e]==null) {
					if(a<literal.size()) {
						temps[t] = literal.get(a);
						a++;
						t++;
					}
				}else if(elements[e] instanceof AbstractCourse<?>) {
					setSon(elements[e]);
				}
				
				else if(elements[e]!=null){
					if(elements[e]==Te.te) {
						int size = literal_te.size();
						if(size>0) {
							temps[t] = literal_te.get(b);
							b++;
							t++;
						}
					}else if(EntityFactory.isMatchDomain(elements[e].getClass(), domainService.getClass())||
							elements[e].getClass().isEnum()||proxy.getPattern().isMatch(elements[e])){
						temps[t] = elements[e];
						t++;				
					}else{
						if(a<literal.size()) {
							temps[t] = literal.get(a);
							a++;
							t++;
							} 
						}
				}
			}
			for(;t<temps.length;t++) {//将剩余的字面值填充（如果还有剩余）
				if(a>=literal.size()) 
					break;
				temps[t] = literal.get(a);
				a++;
			}
			return Arrays.copyOf(temps, t);//去除为null的无效下标
		}
		return null;
	}

	/**
	 * 创建一个包含该节点所在的树的CourseData
	 * @return
	 */
	protected CourseData buildJsonNode() {
		CourseData son = null;
		CourseData next = null;
		if(this.son!=null) {
			son = this.son.buildJsonNode();
		}
		if(this.next!=null) {
			next = this.next.buildJsonNode();
		}
		this.courseData = new CourseData(type, optype, fields, types, values, son, next);
		return courseData;
	}
	
	/**
	 * 将元素分类设置到数据结构
	 */
	private void buildData() {
		fields = new ArrayList<String>();
		types = new HashSet<String>();
		
		for(Object obj:elements) {
			if(obj==null)continue;
			if(obj.getClass()==String.class) {
				String name = obj.toString();
				fields.add(name.substring((name.substring(0,name.lastIndexOf(".")).lastIndexOf(".")+1),name.length()));
				types.add(name.substring((name.substring(0,name.lastIndexOf(".")).lastIndexOf(".")+1),name.lastIndexOf(".")));					
			}else if(obj.getClass().isEnum()) {
				
				types.add(obj.getClass().getFields()[0].getName());
				
				fields.add(obj.getClass().getFields()[0].getName()+"."+obj.toString());
			}else {
				entities.add(obj);
			}
		}
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
				
				//处理枚举,字典的第一个元素必需是对应的实体类名
				if(elements[i].getClass().isEnum()) {
					builder.append(elements[i].getClass().getFields()[0].getName()).append(".").append(elements[i].toString()).append(blank);
				}else
				//处理方法字面值
				if (elements[i].getClass().getPackage() == Package.getPackage("java.lang")) {
					if(elements[i].getClass()==String.class){
						if(condition1) {
							String fullName = elements[i].toString();
							//获取包括类型和属性名的simpleName
							//防止substring内存占用
							//产生类名.属性字符串
							String simpleName = new String(fullName.substring(fullName.lastIndexOf(".",fullName.lastIndexOf(".")-1)+1, fullName.length()).replace(".get", "."));
							builder.append(simpleName).append(blank);	
						}else
							builder.append(elements[i]).append(blank);												
					}else
						builder.append(elements[i]).append(blank);	
				}else
				//处理类型
				{
					String simpleName = elements[i].getClass().getSimpleName();
					int index = simpleName.indexOf("$$");//代理类类名截取
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
		
		return builder.toString();
	}
	
	protected void addLiteral(String methodName) {
		if(literal.size()>49)
			System.out.println(literal.size());
		else
			literal.add(methodName);			
	}

	protected void addLiteral_te(String methodName) {
		if(literal_te.size()>49)
			System.out.println(literal_te.size());
		else
			literal_te.add(methodName);			
	}

	/**
	 * Warning!If the status is less than END,you can not change status
	 * @param status
	 */
	protected void setStatus(byte status) {
		if(this.status>END)
			this.status = status;
	}

	/*----------------------public method-------------------- */
	
	public AbstractCourse() {}
	
	public AbstractCourse(AbstractCourse<?> previous,String courseType,Object ...obj) {
		this.type = courseType;
		this.previous = previous;
		setElements(obj);
	}
	
	public AbstractCourse(AbstractCourse<?> parent,String courseType,boolean isSon,Object ...obj) {
		this.type = courseType;
		if(isSon) {
			this.isSon = true;
			this.parent = previous;
		}
		this.previous = parent;
		setElements(obj);
	}
	
	/**
	 * 结束当前的一条course语句，则该course不可再添加语句，
	 * 并且执行end方法在大多情况下都是必须的，如果没有正常的执行end，
	 * 会导致当前定义的course被下一次操作快速抛弃而不会进行缓存
	 */
	public void END() {
		try {
			if(status!=END) {
				status = END;
				if (previous!=null) 
					previous.END(); 
				else {
					proxy.END();
					EntityFactory.removeCourse(Thread.currentThread().getId());				
				}				
			}
		}
		finally {
			if(status!=END) {
				proxy = null;
				literal = null;
				EntityFactory.removeCourse(Thread.currentThread().getId());
			}
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
	
	
	
	/**
	 * 提供一个该course的结构的字面描述，为调试提供方便，实际过程和所见描述的并不能画上等号。
	 * 不能在内部类初始化方法中调用,因为this
	 */
	@Override
	public String toString() {
		setCondition(previous);
		return DataString(this,elements,condition1,condition2) + fieldString(this);
	}

	/**
	 * 将生成的CourseData发送给json格式化单元，并返回格式化后的json字符串
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getJsonString() {
		buildJsonNode();
		if(jutil==null) 
			return Jutil.toJsonString(courseData);
		else	
			return jutil.toJsonString(courseData);
	}
	
	public List<Object> getEntities() {
		return entities;
	}

	
	public void addEntity(Object entity) {
		entities.add(entity);
	}

	public Object getElements() {
		return elements;
	}

	public String getModel() {
		return model;
	}

	public byte getStatus() {
		return status;
	}

	
	

	public List<Object> getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = Arrays.asList(values);
	}

	

	public String getType() {
		return type;
	}


	public String getOptype() {
		return optype;
	}

	/**
	 * 您可以通过继承CourseOpt接口扩充节点类型常量
	 * @param optype
	 */
	public void setOptype(String optype) {
		this.optype = optype;
	}

	public Jutil getJutil() {
		return jutil;
	}

	/**
	 * 您可以通过继承Jutil并实现其方法来自定义json格式化操作和输出
	 * @see Jutil#toJsonString(com.cloverframework.core.util.Jsonable)
	 * @param jutil
	 */
	public void setJutil(Jutil jutil) {
		this.jutil = jutil;
	}

	public static CourseOpt getOpt() {
		return opt;
	}

	/**
	 * 您可以通过继承CourseType接口扩充操作类型常量
	 * @param opt
	 */
	public static void setOpt(CourseOpt opt) {
		AbstractCourse.opt = opt;
	}

	

	
}
