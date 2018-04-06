package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.function.BiFunction;

import com.cloverframework.core.data.CourseResult;

/**
 * Course是基于AbstractCourse实现的基础上，提供了面向用户业务过程语言定义的方法，
 * 通过方法和标识的使用能够组装一个业务过程。
 * @author yl  
 */
public final class Course extends AbstractCourse<Course>{
	 /*
	  * 为了保证闭包性，此类和内部类的构造方法修饰为protected,类则是static final
	  */
	
	/** course标识*/
	protected String id;

	private CourseResult<?> result;
	
	public CourseResult<?> getResult(){
		return this.result;
	}
	public void setResult(CourseResult<?> result) {
		if(this.result==null)
			this.result = result;
	}
	
	
	/**
	 * 
	 * @param id 这个course的标识，给定的字符串不能包含空格
	 */
	protected Course(String id){
		literal = new ArrayList<String>(50);
		literal_te = new ArrayList<String>(50);
		//String reg = "^\\s*$";
		String reg = "^[\\S]*$";
		if(id!=null && id.matches(reg))
			this.id = id;
	}
	
	public String getId() {
		return id;
	}

	
	
	//用于测试
	@SuppressWarnings("unused")
	private void eachlist() {
		for(String s:literal) {
			System.out.println(s);
		}
	}

		
	@Override
	public String getJsonString() {
		return next.getJsonString();
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
		id = null;
		result = null;
	}
	/**
	 * 通过输入的节点创建函数表达式执行节点创建，如果节点已存在，则不会重复创建
	 */
	@SuppressWarnings("rawtypes")
	private static Object create(AbstractCourse old,BiFunction<AbstractCourse, Object[], Object> constructor,AbstractCourse a,Object b[]) {
		if(a.getStatus()<WAIT)return old;
		old = (AbstractCourse) constructor.apply(a, b);
		return old;
	}
	
	
	private Get 	get;
	private Add 	add;
	private Put 	put;
	private Remove 	remove;
	
	/**
	 * 开启一个GET描述的Course，代表从数据提供方获取相应的内容
	 * @param obj
	 * @return
	 */
	public Get get(Object ...obj){return get = (Get) create(get,Get::new,this,obj);
		//return get!=null?get:(get = new GET(this,obj));
	
	}
	
	/**
	 * 开启一个ADD描述的Course，代表在数据提供方添加相应的内容
	 * @param obj
	 * @return
	 */
	public Add add(Object ...obj){return add = (Add) create(add, Add::new,this,obj);}
	
	/**
	 * 开启一个PUT描述的Course，代表在数据提供方更新/替换相应的内容
	 * @param obj
	 * @return
	 */
	public Put put(Object ...obj){return put = (Put) create(put, Put::new,this,obj);}
	
	/**
	 * 开启一个REMOVE描述的Course，代表在数据提供方删除相应的内容
	 * @param obj
	 * @return
	 */
	public Remove remove(Object ...obj){return remove = (Remove) create(remove, Remove::new,this,obj);}

	/**
	 * Get
	 * @author yl
	 *
	 */
	public static final class Get extends AbstractCourse<Get>{
		@SuppressWarnings("static-access")
		public Get(AbstractCourse<?> previous, Object[] obj) {
			super(previous,courseType.get, obj);
		}
		//-------------------------------------------------------
		private By 		by;
		private AND 	and;
		private GroupBy groupBy;
		private OrderBy orderBy;
		@SuppressWarnings("unused")
		private Limit 	limit;
		
		
		/**=*/
		public Get count(Object ...value) {optype = opt.count;setValues(value);return this;}
		public By 		by(Object... obj){return by = (By) create(by,By::new,this,obj);}
		public AND 		and(Object... obj){return and = (AND) create(and,AND::new,this,obj);}
		public OrderBy 	orderBy(Object... obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	groupBy(Object... obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	limit(int start,int end){return limit = new Limit(this,start,end);}
	}
		
		
	/**
	 * Add
	 * @author yl
	 *
	 */
	public static final class Add extends AbstractCourse<Add>{
		@SuppressWarnings("static-access")
		protected Add(AbstractCourse<?> previous,Object ...obj){
			super(previous,courseType.add, obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object ...obj){return by = (By) create(by,By::new,this,obj);}
		}
		
	/**
	 * Put
	 * @author yl
	 *
	 */
	public static final class Put extends AbstractCourse<Put>{
		@SuppressWarnings("static-access")
		protected Put(AbstractCourse<?> previous,Object ...obj){
			super(previous,courseType.put, obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object ...obj){return by = (By) create(by,By::new,this,obj);}
	}
		
	/**
	* Remove
	* @author yl
	*
	*/
	public static final class Remove extends AbstractCourse<Remove>{
		@SuppressWarnings("static-access")
		protected Remove(AbstractCourse<?> previous,Object ...obj){
			super(previous,courseType.remove, obj);
		}
		//-------------------------------------------------------
		private By by;
		public By By(Object ...obj){return by = (By) create(by,By::new,this,obj);}
		}
		
		
	@SuppressWarnings("static-access")
	public static class Condition extends AbstractCourse<Condition>{
		
		protected Condition(AbstractCourse<?> previous,String optype, Object ...obj){
			super(previous,optype, obj);
		}
		
		protected Condition(AbstractCourse<?> parent,String optype,boolean isSon, Object... obj) {
			super(parent, optype,isSon, obj);
		}
		
		/**=*/
		public Condition eq(Object ...value) {optype = opt.eq;setValues(value);return this;}
		/**!=*/
		public Condition ne(Object ...value) {optype = opt.ne;setValues(value);return this;}
		/**>*/
		public Condition gt(Object ...value) {optype = opt.gt;setValues(value);return this;}
		/**<*/
		public Condition lt(Object ...value) {optype = opt.lt;setValues(value);return this;}
		/**>=*/
		public Condition ge(Object ...value) {optype = opt.ge;setValues(value);return this;}
		/**<=*/
		public Condition le(Object ...value) {optype = opt.le;setValues(value);return this;}
		
		//-------------------------------------------------------
		private Limit 	limit;
		private By by;
		private AND and;
		private OR or;
		private NOT not;
		private OrderBy orderBy;
		private GroupBy groupBy;
		
		public By by(Object ...obj){return by = (By) create(by,By::new,this,obj);}
		public AND and(Object... obj) {return and = (AND) create(and, AND::new,this,obj);}
		public OR or(Object ...obj){return or = (OR) create(or,OR::new,this,obj);}
		public NOT not(Object ...obj){return not = (NOT) create(not,NOT::new,this,obj);}
		public OrderBy 	orderBy(Object... obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	GroupBy(Object ...obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
	}
	
	
	
	/**
	* By
	* @author yl
	*
	*/
	public static final class By extends Condition{

		@SuppressWarnings("static-access")
		protected By(AbstractCourse<?> previous, Object[] obj) {
			super(previous,courseType.by, obj);
			}	
		}
		
	public static final class AND extends Condition{

		@SuppressWarnings("static-access")
		protected AND(AbstractCourse<?> previous, Object[] obj) {
			super(previous,courseType.and, obj);
		}
		
	}
	
	public static final class OR extends Condition{

		@SuppressWarnings("static-access")
		protected OR(AbstractCourse<?> previous, Object[] obj) {
			super(previous,courseType.or, obj);
		}
		
	}
	
	public static final class NOT extends Condition{

		@SuppressWarnings("static-access")
		protected NOT(AbstractCourse<?> previous, Object[] obj) {
			super(previous,courseType.not,obj);
		}
		
	}
	
	
		/**
		 * GroupBy
		 * @author yl
		 *
		 */
	public static final class GroupBy extends AbstractCourse<GroupBy>{
		@SuppressWarnings("static-access")
		protected GroupBy(AbstractCourse<?> previous,Object ...obj){
			super(previous,courseType.groupBy, obj);
		}
			//-------------------------------------------------------
		private Limit limit;
		public Limit LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
	}
		
	/**
		 * OrderBy
		* @author yl
		*
		*/
		public static final class OrderBy extends AbstractCourse<OrderBy>{
			@SuppressWarnings("static-access")
			public OrderBy(AbstractCourse<?> previous,Object ...obj){
				super(previous,courseType.orderBy, obj);
			}
			//-------------------------------------------------------
			private Limit limit;
			public Limit LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
		}


	/**
	* Limit
	* @author yl
	*
	*/
	public static final class Limit extends AbstractCourse<Limit>{
		@SuppressWarnings("unused")
		private Object[] element;
		protected Limit(AbstractCourse<?> previous,int start,int end){
			String obj = String.valueOf(start)+","+String.valueOf(end);
			this.previous = previous;
			setElements(obj);
		}

		@Override
		public void setElements(Object... element) {
			this.element = element;
		}
		
			//-------------------------------------------------------
		}
}
