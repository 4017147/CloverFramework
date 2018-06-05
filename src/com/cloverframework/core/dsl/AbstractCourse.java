package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.Course.Condition;
import com.cloverframework.core.dsl.interfaces.CourseInterface;
import com.cloverframework.core.dsl.interfaces.CourseProxyInterface;
import com.cloverframework.core.util.ArgsFilter;
import com.cloverframework.core.util.ArgsMatcher;
import com.cloverframework.core.util.ELOperation;
import com.cloverframework.core.util.interfaces.CourseOpt;
import com.cloverframework.core.util.interfaces.CourseType;
import com.cloverframework.core.util.interfaces.ELType;
import com.cloverframework.core.util.interfaces.IArgsMatcher;
import com.cloverframework.core.util.json.JsonFields;
import com.cloverframework.core.util.json.JsonUtil;

/**
 * 定义了一种双向链表结构属性，并实现了大部分基础特性,需要注意的是，
 * 当status属性级别低于或等于END，对Elements和status的任何操作都必需是无效的。
 * @author yl
 * 
 * 
 */

public abstract class AbstractCourse implements CourseInterface{
	
	/**course代理*/
	CourseProxyInterface proxy;//上级传递
	
	/**命名标识*/
	protected String id;

	/**
	 * 头部标识,它可能是易变的以及有多个管理实现，不能作为查找唯一条件,
	 * 然而判断两个course是否相同，使用id的方式并不能保证缓存的course的唯一性，
	 * 因此需要结合id和head来判断。
	 * */
	protected String head;
	
	/**节点元素*/
	private Object[] elements;	
	
	private Object[] args;
	
	/**节点类型*/
	protected String type;
	
	/**操作类型*/
	protected String optype;
	
	/**前级*/
	AbstractCourse previous;
	
	/**后级*/
	AbstractCourse next;
	
	/**父级*/
	AbstractCourse parent;
	
	/**子级*/
	List<AbstractCourse> sons;
	
	/**是否是一个子级*/
	protected boolean isSon;
	
	/**字段值*/
	List<String> fields = new ArrayList<>();
	
	/**查询类型*/
	Set<String> types = new HashSet<String>();//需要全限定定名
	
	/**对象值*/
	List<Object> entities;
	
	/**参数值,该值线程独立*/
	ThreadLocal<CourseValues> values = new ThreadLocal<CourseValues>();
	
	/**
	 * 返回数据接口
	 */
	ThreadLocal<CourseResult> result;
	
	ThreadLocal<CompletableFuture<CourseResult>> futureResult;

	/**json输出工具*/
	static JsonUtil jutil;
	
	/**json格式内容*/
	//String jsonString;
	
	/**包装用于json格式化的数据*/
	JsonFields courseData;
	
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
	protected AbstractCourse origin;

	IArgsMatcher argsMather = new ArgsMatcher();
	
	/**是否开启参数映射*/
	public static boolean ifCountValues = true;
	
	/**是否输出simpleName */
	public static boolean condition1 = true;//根传递
	
	/**是否输出颜色 */
	public static boolean condition2 = true;//根传递
	
	
	/**三元引用的返回标识*/
	public enum Te{te}
	
	/**
	 * 表示该course当前状态
	 * 在一个线程中，proxy创建course和动态代理获取字面值方法是按顺序执行的，也就是当前的status为正常，
	 * 如果在这些方法获取到的status异常，则意味着其他线程发生了不可预料的错误而被终止，
	 * 但是集合中的course可能并未移除，当前线程如果复用了相同的ID，有可能发生误读，
	 * 这一般发生在一个course中断后，另一个course开启之前，尽管这个概率是很低的。
	 * 
	 */
	private volatile int status = UNLOCKED;//上级传递
	

	/**当前线程方法栈帧基准累计数，用于计算产生字面值的方法栈长是否合法*/
	ThreadLocal<Integer> level = new ThreadLocal<Integer>();
	{
		level.set(4);
	}
	
	/*----------------------private method-------------------- */
	
	/**
	 * 初始化数据依赖于前置节点
	 * @param course
	 */
	protected void init(AbstractCourse course) {
		if(course!=null) {
			id = course.id;
			proxy = course.proxy;
			isFork = course.isFork;
			isForkm = course.isForkm;
			origin = course.origin;
			argsMather = course.argsMather;	
			level = course.level;
		}
	}
	
	/**
	 * 如果根节点status异常，则不会执行，否则正常执行并刷新根节点的status。
	 * 执行后都会将字面列表清空，通常情况下，值传入要先于方法返回值传入，
	 * 在传入节点参数的时候，只能包括并且按照枚举->实体->方法字面值->三元,
	 * 通常情况下这几类型必需具有不变性。
	 * @param elements
	 */
	protected void setElements(Object... elements) {
		try {
			args = elements;
			status = previous==null?null:previous.status;
			//TODO 该异常情况下如何处理
			if(status>LOCKED) {
				status = FILL;
				init(previous);
				if((isFork||isForkm) && elements.length>0) 
					setModel(elements[0]);
				this.elements = fill(elements,getLiteral(),getLiteral_te(),proxy.getDomainService());
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
//						if(this.elements==null)
//							this.elements = origin.elements;
						origin = origin.next;
					}
				status = previous.status;
					//previous.next = this;//
				if(entities==null) {
					entities = new ArrayList<>();
				}
				buildData(true);
			}
		}finally {
			getLiteral().clear();
			getLiteral_te().clear();
		}
	}

	void setValueElement(Object... elements){
		setElements(elements);
	}
	
	/**
	 * 设置fork模式值
	 * @param o
	 */
	protected void setModel(Object o) {
		if(o!=null && o.getClass()==String.class) {
			for(String s:ELType.Model) {
				if(s.equals(o)) {
					model = s;
					break;
				}
			}
		}
	}
	
	/**
	 * 设置son，son和同辈son是链表结构，并且清除son原来的previous之间的关系，
	 * 恢复当前节点跟previous的关系，因为子节点的创建语法上先于当前节点
	 * @param object
	 */
	protected void setSon(Object object) {
		AbstractCourse node =  (AbstractCourse)object;
		if(node.isSon==false) {
			while(node.previous!=null) {
				node = node.previous;
			}			
		}else 
			node.parent = this;	
		if(this.sons==null)
			this.sons = new ArrayList<AbstractCourse>();
		this.sons.add(node);			
		
	}

	
	/**
	 * 将领域实体字典或方法字面值填充到element数组中
	 * 1、如果数组元素遇到为null则填充
	 * 2、如果数组元素为领域实体或实体类型，合法则添加类型
	 * 3、剩余的字面值会填充
	 * <p>
	 * 通常情况下，值要先于方法字面值填充，按字典->实体类型->方法字面值->三元
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
				}else if(elements[e] instanceof AbstractCourse) {
					setSon(elements[e]);
					if(elements[e] instanceof Condition)//Condtion类型节点不允许子节点和其他参数共存
						return new Object[0];
				}else if(elements[e]!=null){
					Object o = null;
					if(elements[e]==Te.te) {
						int size = literal_te.size();
						if(size>0) {
							temps[t] = literal_te.get(b);
							b++;
							t++;
						}
					}else if((o = ArgsFilter.filter(elements[e], domainService, argsMather))!=null){
						temps[t] = o;
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
		return new Object[0];
	}
	
	/**
	 * 创建一个包含该节点所在的树的courseData
	 * @return
	 */
	protected JsonFields buildJsonNode() {
		List<JsonFields> sonList = null;
		JsonFields next = null;
		if(this.sons!=null) {
			if(sonList==null)
				sonList = new ArrayList<JsonFields>();
			
			for(AbstractCourse a:this.sons) {
				sonList.add(a.buildJsonNode());
			}
		}
		if(this.next!=null) 
			next = this.next.buildJsonNode();
			
		courseData = new JsonFields(type, optype, fields, types, 
				values==null?null:(values.get()==null?null:values.get().toString()), sonList, next);
		return courseData;
	}
	

	/**
	 * 将elements元素分类设置到数据结构
	 * @param lowerCase 方法名首字母是否转小写
	 */
	private void buildData(boolean lowerCase) {
		
		for(Object obj:elements) {
			if(obj==null)continue;
			if(obj.getClass().isEnum()) {
				types.add(obj.getClass().getFields()[0].getName());
				fields.add(obj.getClass().getFields()[0].getName()+'.'+obj.toString());
			}else if(obj.getClass()==String.class && !obj.getClass().isEnum()) {
					String fullName = obj.toString();
					String type$field = new String(fullName.substring(fullName.lastIndexOf('.',fullName.lastIndexOf('.')-1)+1, fullName.length()).replace(".get", "."));
					if(lowerCase) {
						char[] fc = type$field.toCharArray();
						int index = type$field.lastIndexOf('.')+1;
						char c = fc[index];
						if(c<='Z'&&c>='A'&&lowerCase)
							fc[index] = (char) (c+32);
						fields.add(new String(fc));					
					}else
						fields.add(type$field);
					String type = new String(fullName.substring((fullName.substring(0,fullName.lastIndexOf('.')).lastIndexOf('.')+1),fullName.lastIndexOf('.')));
					types.add(type);					
				}else 
					entities.add(obj);
				
		}
	}
	

	/**
	 * 打印节点元素
	 * @param course
	 * @param elements
	 * @param condition1 是否输出simpleName
	 * @param condition2 是否输出颜色,改颜色通过ANSI转义序列定义
	 * @return
	 */
	private String DataString(Object[] elements,boolean condition1,boolean condition2) {
		Optional<List<String>> fields = Optional.ofNullable(this.fields);
		Optional<List<AbstractCourse>> sons = Optional.ofNullable(this.sons);
		Optional<List<Object>> entities = Optional.ofNullable(this.entities);
		Optional<String> optype = Optional.ofNullable(this.optype);
		Optional<CourseValues> values = Optional.ofNullable(this.values.get());
		Optional<AbstractCourse> next = Optional.ofNullable(this.next);
		StringBuilder builder = new StringBuilder(56);
		String nextline = "\n";
		if(isSon)
			nextline = "";
		if(condition2)
			builder.append(nextline+"\u001b[94m").append(type).append("\u001b[0m ");
		else
			builder.append(nextline).append(type);
		if(id!=null && type==CourseType.root)builder.append("id:"+id);
			fields.ifPresent((field)->{
				field.forEach((f)->builder.append(f).append(','));
				if(!sons.isPresent()&&!entities.isPresent())
					builder.deleteCharAt(builder.length()-1);
			});
			sons.ifPresent((son)->{
				son.forEach((s)->builder.append(s).append(','));
				if(!entities.isPresent())
					builder.deleteCharAt(builder.length()-1);
			});
			entities.ifPresent((entity)->{
				entity.forEach((s)->builder.append(s).append(','));
				builder.deleteCharAt(builder.length()-1);
			});
			optype.ifPresent((s)->builder.append(' ').append(s).append(' '));
			values.ifPresent((s)->builder.append(" values:").append(s.toString()));
			next.ifPresent((s)->builder.append(s));
		return builder.toString();
	}

	/**
	 * Warning!If the status is less than END,you can not change status
	 * @param status
	 */
	void setStatus(int status) {
		if(this.status>END)
			this.status = status;
	}
	
	/**
	 * 
	 * @param id 这个course的标识，给定的字符串不能包含空格
	 */
	protected AbstractCourse(String id){
		String reg = "^[\\S]*$";
		if(id!=null && id.matches(reg))
			this.id = id;
	}

	/*----------------------public method-------------------- */
	@Override
	public AbstractCourse getThis() {
		return this;
	}
	
	public AbstractCourse() {}
	
	/**
	 * 创建一个主干节点类型的course
	 * @param previous
	 * @param courseType
	 * @param obj
	 */
	public AbstractCourse(AbstractCourse previous,String courseType,Object ...obj) {
		this.type = courseType;
		previous.next = this;
		this.previous = previous;
		if(this.previous.type!=CourseType.root) 
			beginLiteral(this, level.get()+1);
		else
			beginLiteral(this, level.get());
		setElements(obj);
	}
	
	/**
	 * 创建一个子节点类型的course
	 * @param parent
	 * @param courseType
	 * @param isSon
	 * @param obj
	 */
	public AbstractCourse(AbstractCourse parent,String courseType,boolean isSon,Object...obj) {
		this.type = courseType;
		if(isSon) {
			this.isSon = true;
			this.parent = parent;
		}
		this.previous = this.parent;//传递previous参数
		beginLiteral(this, level.get());
		setElements(obj);
		this.previous = null;
	}
	
	/**
	 * 销毁该Course
	 */
	@Override
	public void destroy() {
		//TODO
		if(next!=null) 
			next.destroy();
		
		proxy = null;
		elements = null;
		previous = null;
		next = null;
		parent = null;
		sons = null;
		fields = null;
		types = null;
		entities = null;
		values = null;
		courseData = null;
		origin = null;
		argsMather = null;
		args = null;
	}

	
	/**
	 * 提供一个该course的结构的字面描述，为调试提供方便，实际过程和所见描述的并不能画上等号。
	 * 不能在内部类初始化方法中调用,因为this
	 */
	@Override
	public String toString() {
		return DataString(elements,condition1,condition2);
	}

	/**
	 * 将生成的CourseData发送给json格式化单元，并返回格式化后的json字符串
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getJsonString() {
		buildJsonNode();
		if(jutil==null) 
			return JsonUtil.toJsonString(courseData);
		else	
			return jutil.toJsonString(courseData);
	}
	
	public List<Object> getEntities() {
		return entities;
	}

	
	void addEntity(Object entity) {
		entities.add(entity);
	}

	public Object[] getElements() {
		return elements;
	}

	public String getModel() {
		return model;
	}

	public int getStatus() {
		return status;
	}
	
	void createResult(){
		if(result==null && type == CourseType.root) 
			result = new ThreadLocal<CourseResult>();
	}
	
	void createFutureResult(){
		if(result==null && type == CourseType.root) 
			futureResult = new ThreadLocal<CompletableFuture<CourseResult>>();
	}
	

	@Override
	public String getType() {
		return type;
	}

	public String getNextType() {
		if(next!=null)
			return next.type;
		return "";
	}

	public String getOptype() {
		return optype;
	}

	/**
	 * 可以通过设置一个继承CourseOpt的接口扩充节点类型常量
	 * @param optype
	 */
	void setOptype(String optype) {
		this.optype = optype;
	}
	

	public static JsonUtil getJutil() {
		return jutil;
	}

	/**
	 * 可以通过设置一个继承Jutil的工具类来自定义json格式化操作和输出
	 * @see JsonUtil#toJsonString(com.cloverframework.core.util.Jsonable)
	 * @param jutil
	 */
	public static void setJutil(JsonUtil jutil) {
		AbstractCourse.jutil = jutil;
	}

	public static CourseOpt getOpt() {
		return opt;
	}

	/**
	 * 可以通过设置一个继承CourseType的接口扩充操作类型常量
	 * @param opt
	 */
	public static void setOpt(CourseOpt opt) {
		AbstractCourse.opt = opt;
	}

	public IArgsMatcher getPattern() {
		return argsMather;
	}

	/**
	 * 可以通过设置一个规则校验器来自定义节点参数校验规则
	 * @param pattern
	 */
	void setPattern(IArgsMatcher pattern) {
		this.argsMather = pattern;
	}

	
	void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<String> getFields() {
		return fields;
	}

	public Object[] getArgs() {
		return args;
	}


	
	
	
}
