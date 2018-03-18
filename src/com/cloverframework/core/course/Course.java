package com.cloverframework.core.course;

import java.util.ArrayList;
import java.util.function.BiFunction;

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
	/** 创建时间*/
	protected long createTime = System.currentTimeMillis();
	/** 平均执行时间*/
	protected long avg_exe;
	/** 最大执行时间*/
	protected long max_exe;
	/** 最短执行时间*/
	protected long min_exe;
	
	/**
	 * 
	 * @param id 这个course的标识，给定的字符串不能包含空格
	 */
	protected Course(String id){
		literalList = new ArrayList<String>(50);
		//String reg = "^\\s*$";
		String reg = "^[\\S]*$";
		if(id!=null && id.matches(reg))
			this.id = id;
	}
	
	public enum CourseType {GET,ADD,PUT,REMOVE}	
	private Enum<CourseType> type;
	public Enum<CourseType> getType() {
		return type;
	}

	//用于测试
	@SuppressWarnings("unused")
	private void eachlist() {
		for(String s:literalList) {
			System.out.println(s);
		}
	}

	/**
	 * 如果节点已存在，则不会重复创建
	 */
	@SuppressWarnings("rawtypes")
	private static Object create(AbstractCourse old,BiFunction<AbstractCourse, Object[], Object> constructor,AbstractCourse a,Object b[]) {
		if(a.status<WAIT)return old;
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
	public Get get(Object ...obj){type = CourseType.GET;return get = (Get) create(get,Get::new,this,obj);
		//return get!=null?get:(get = new GET(this,obj));
	}
	
	/**
	 * 开启一个ADD描述的Course，代表在数据提供方添加相应的内容
	 * @param obj
	 * @return
	 */
	public Add add(Object ...obj){type = CourseType.ADD;return add = (Add) create(add, Add::new,this,obj);}
	
	/**
	 * 开启一个PUT描述的Course，代表在数据提供方更新/替换相应的内容
	 * @param obj
	 * @return
	 */
	public Put put(Object ...obj){type = CourseType.PUT;return put = (Put) create(put, Put::new,this,obj);}
	
	/**
	 * 开启一个REMOVE描述的Course，代表在数据提供方删除相应的内容
	 * @param obj
	 * @return
	 */
	public Remove remove(Object ...obj){type = CourseType.REMOVE;return remove = (Remove) create(remove, Remove::new,this,obj);}

	/**
	 * Get
	 * @author yl
	 *
	 */
	public static final class Get extends AbstractCourse<Get>{

		public Get(AbstractCourse<?> previous, Object[] obj) {
			super(previous, obj);
		}

		//-------------------------------------------------------
		private By 		by;
		private GroupBy groupBy;
		private OrderBy orderBy;
		private Limit 	limit;
			
		public By 		by(Object... obj){return by = (By) create(by,By::new,this,obj);}
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
		protected Add(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
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
		protected Put(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
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
		protected Remove(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
		}
		//-------------------------------------------------------
		private By by;
		public By By(Object ...obj){return by = (By) create(by,By::new,this,obj);}
		}
		
		
	/**
	* By
	* @author yl
	*
	*/
	public static final class By extends AbstractCourse<By>{
		private Object value;
		public enum CourseType {eq,ne,gt,lt,ge,le}	
		private Enum<CourseType> type;
		public Enum<CourseType> getType() {
			return type;
		}
		protected By(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
		}
		
		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		/**=*/
		public By eq(Object value) {type = CourseType.eq;setValue(value);return this;}
		/**!=*/
		public By ne(Object value) {type = CourseType.ne;setValue(value);return this;}
		/**>*/
		public By gt(Object value) {type = CourseType.gt;setValue(value);return this;}
		/**<*/
		public By lt(Object value) {type = CourseType.lt;setValue(value);return this;}
		/**>=*/
		public By ge(Object value) {type = CourseType.ge;setValue(value);return this;}
		/**<=*/
		public By le(Object value) {type = CourseType.le;setValue(value);return this;}
		
		//-------------------------------------------------------
		private OrderBy orderBy;
		private GroupBy groupBy;
		private Limit 	limit;
			
		public OrderBy 	orderBy(Object... obj){return orderBy = (OrderBy) create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	GroupBy(Object ...obj){return groupBy = (GroupBy) create(groupBy, GroupBy::new,this,obj);}
		public Limit 	LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
		}
		
		/**
		 * GroupBy
		 * @author yl
		 *
		 */
	public static final class GroupBy extends AbstractCourse<GroupBy>{
		protected GroupBy(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
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
		public OrderBy(AbstractCourse<?> previous,Object ...obj){
			super(previous, obj);
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
		protected void setElements(Object... element) {
			this.element = element;
		}
		
			//-------------------------------------------------------
		}
}
