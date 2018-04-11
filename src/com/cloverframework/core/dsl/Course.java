package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.function.BiFunction;

import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.exception.CourseIsClosed;
import com.cloverframework.core.util.interfaces.CourseType;

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
	
	/**
	 * result不会跟随节点立刻创建，根据流程会推迟到仓储接收返回结果时创建
	 * @param result
	 */
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
	private static Object create(AbstractCourse old,BiFunction<AbstractCourse, Object[], AbstractCourse> constructor,AbstractCourse a,Object b[]) {
		if(a.getStatus()<WAIT)
			try {
				throw new CourseIsClosed(a.getType());
			} catch (CourseIsClosed e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		old = constructor.apply(a, b);
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
	public Get get(Object...obj){
		this.type = CourseType.get;
		return get = (Get) create(get,Get::new,this,obj);
		//return get!=null?get:(get = new GET(this,obj));
	
	}
	
	/**
	 * 开启一个ADD描述的Course，代表在数据提供方添加相应的内容
	 * @param obj
	 * @return
	 */
	public Add add(Object...obj){this.type = CourseType.add;return add = (Add) create(add, Add::new,this,obj);}
	
	/**
	 * 开启一个PUT描述的Course，代表在数据提供方更新/替换相应的内容
	 * @param obj
	 * @return
	 */
	public Put put(Object...obj){this.type = CourseType.put;return put = (Put) create(put, Put::new,this,obj);}
	
	/**
	 * 开启一个REMOVE描述的Course，代表在数据提供方删除相应的内容
	 * @param obj
	 * @return
	 */
	public Remove remove(Object...obj){this.type = CourseType.remove;return remove = (Remove) create(remove, Remove::new,this,obj);}

	/**
	 * Get
	 * @author yl
	 *
	 */
	public static final class Get extends AbstractCourse<Get>{
		
		@SuppressWarnings("static-access")
		public Get(AbstractCourse<?> previous, Object...obj) {
			super(previous,courseType.get, obj);
		}
		
		//-------------------------------------------------------
		private By 		by;
		private AND 	and;
		private GroupBy groupBy;
		private OrderBy orderBy;
		@SuppressWarnings("unused")
		private Limit 	limit;
		
		public By 		by(Object...obj){return by = (By) create(by,By::new,this,obj);}
		public AND 		and(Object...obj){return and = (AND) create(and,AND::new,this,obj);}
		public OrderBy 	orderBy(Object...obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	groupBy(Object...obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	limit(int start,int end){return limit = new Limit(this,start,end);}
	}
		
		
	/**
		 * 聚合函数
		 * @author yl
		 *
		 */
	public static class Aggregate extends AbstractCourse<Object>{
	
		public Aggregate(AbstractCourse<?> previous, String courseType, Object obj) {
			super(previous, CourseType.agg, obj);
		}
		protected Aggregate(AbstractCourse<?> parent,String optype,boolean isSon, Object...obj) {
			super(parent, optype,isSon, obj);
		}
		//--------------------------------------------------
		private By 		by;
		private AND 	and;
		private GroupBy groupBy;
		private OrderBy orderBy;
		@SuppressWarnings("unused")
		private Limit 	limit;

		public By 		by(Object...obj){return by = (By) create(by,By::new,this,obj);}
		public AND 		and(Object...obj){return and = (AND) create(and,AND::new,this,obj);}
		public OrderBy 	orderBy(Object...obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	groupBy(Object...obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	limit(int start,int end){return limit = new Limit(this,start,end);}
	}
	
	public static final class Count extends Aggregate{
		public Count(AbstractCourse<?> parent, String optype, boolean isSon, Object obj) {
			super(parent, optype, isSon, obj);
		}		
	}


	/**
	 * Add
	 * @author yl
	 *
	 */
	public static final class Add extends AbstractCourse<Add>{
		@SuppressWarnings("static-access")
		protected Add(AbstractCourse<?> previous,Object...obj){
			super(previous,courseType.add, obj);
		}
		
		
		@Override
		public Add setValues(Object... values) {
			// TODO Auto-generated method stub
			return (Add)super.setValues(values);
		}


		//-------------------------------------------------------
		private By by;
		public By By(Object...obj){return by = (By) create(by,By::new,this,obj);}
		}
		
	/**
	 * Put
	 * @author yl
	 *
	 */
	public static final class Put extends AbstractCourse<Put>{
		@SuppressWarnings("static-access")
		protected Put(AbstractCourse<?> previous,Object...obj){
			super(previous,courseType.put, obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object...obj){return by = (By) create(by,By::new,this,obj);}
	}
		
	/**
	* Remove
	* @author yl
	*
	*/
	public static final class Remove extends AbstractCourse<Remove>{
		@SuppressWarnings("static-access")
		protected Remove(AbstractCourse<?> previous,Object...obj){
			super(previous,courseType.remove, obj);
		}
		//-------------------------------------------------------
		private By by;
		public By By(Object...obj){return by = (By) create(by,By::new,this,obj);}
		}
		
		
	/**
	 * where条件
	 * @author yl
	 *
	 */
	@SuppressWarnings("static-access")
	public static class Condition extends AbstractCourse<Condition>{
		
		protected Condition(AbstractCourse<?> previous,String optype, Object...obj){
			super(previous,optype, obj);
		}
		
		protected Condition(AbstractCourse<?> parent,String optype,boolean isSon, Object...obj) {
			super(parent, optype,isSon, obj);
		}
		
		/**=*/
		public Condition eq(Object...value) {optype = opt.eq;setValues(value);return this;}
		/**!=*/
		public Condition ne(Object...value) {optype = opt.ne;setValues(value);return this;}
		/**>*/
		public Condition gt(Object...value) {optype = opt.gt;setValues(value);return this;}
		/**<*/
		public Condition lt(Object...value) {optype = opt.lt;setValues(value);return this;}
		/**>=*/
		public Condition ge(Object...value) {optype = opt.ge;setValues(value);return this;}
		/**<=*/
		public Condition le(Object...value) {optype = opt.le;setValues(value);return this;}
		
		//-------------------------------------------------------
		private Limit 	limit;
		private AND and;
		private OR or;
		private NOT not;
		private OrderBy orderBy;
		private GroupBy groupBy;
		
		public AND 		and(Object...obj) {return and = (AND) create(and, AND::new,this,obj);}
		public OR 		or(Object...obj){return or = (OR) create(or,OR::new,this,obj);}
		public NOT 		not(Object...obj){return not = (NOT) create(not,NOT::new,this,obj);}
		public OrderBy 	orderBy(Object...obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	GroupBy(Object...obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
	}
	
	
	
	/**
	* By
	* @author yl
	*
	*/
	public static final class By extends Condition{

		@SuppressWarnings("static-access")
		protected By(AbstractCourse<?> previous, Object...obj) {
			super(previous,courseType.by, obj);
			}	
		}
		
	public static final class AND extends Condition{

		@SuppressWarnings("static-access")
		protected AND(AbstractCourse<?> previous, Object...obj) {
			//注意如果非可变入参，此处会形成二维数组，需要转换后入参,如(Object[])obj
			super(previous,courseType.and, obj);
		}
		
	}
	
	public static final class OR extends Condition{

		@SuppressWarnings("static-access")
		protected OR(AbstractCourse<?> previous, Object...obj) {
			super(previous,courseType.or, obj);
		}
		
	}
	
	public static final class NOT extends Condition{

		@SuppressWarnings("static-access")
		protected NOT(AbstractCourse<?> previous, Object...obj) {
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
		protected GroupBy(AbstractCourse<?> previous,Object...obj){
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
			public OrderBy(AbstractCourse<?> previous,Object...obj){
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
		public void setElements(Object...element) {
			this.element = element;
		}
		
			//-------------------------------------------------------
		}
}
