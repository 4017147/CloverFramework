package com.cloverframework.core.dsl;

import com.cloverframework.core.util.interfaces.CourseOpt;
import com.cloverframework.core.util.interfaces.CourseType;

/**
 * Course是基于AbstractCourse实现的基础上，提供了面向用户业务过程语言定义的方法，
 * 通过方法和标识的使用能够组装一个业务过程。
 * @author yl  
 */
public final class Course extends AbstractCourse{
	 /*
	  * 为了保证闭包性，此类和内部类的构造方法修饰为protected,类则是static final
	  */
	
	private Get 	get;
	private Add 	add;
	private Put 	put;
	private Remove 	remove;
	
	/**
	 * 反射创建，public
	 */
	public Course() {this.type = CourseType.root;}
	
	/**
	 * 开启一个GET描述的Course，代表从数据提供方获取相应的内容
	 * @param obj
	 * @return
	 */
	public Get get(Object... obj){return get = create(get,Get::new,this,obj);}
	
	/**
	 * 开启一个ADD描述的Course，代表在数据提供方添加相应的内容
	 * @param obj
	 * @return
	 */
	public Add add(Object... obj){return add = create(add, Add::new,this,obj);}
	
	/**
	 * 开启一个PUT描述的Course，代表在数据提供方更新/替换相应的内容
	 * @param obj
	 * @return
	 */
	public Put put(Object... obj){return put = create(put, Put::new,this,obj);}
	
	/**
	 * 开启一个REMOVE描述的Course，代表在数据提供方删除相应的内容
	 * @param obj
	 * @return
	 */
	public Remove remove(Object... obj){return remove = create(remove, Remove::new,this,obj);}

	/**
	 * Get
	 * @author yl
	 *
	 */
	public static final class Get extends AbstractCourse{
		
		public Get(AbstractCourse previous, Object... obj) {
			super(previous,CourseType.get, obj);
		}
		
		//-------------------------------------------------------
		private By 		by;
		private AND 	and;
		private GroupBy groupBy;
		private OrderBy orderBy;
		@SuppressWarnings("unused")
		private Limit 	limit;
		
		public By 		by(Object... obj)		{return by = create(by,By::new,this,obj);}
		public AND 		and(Object... obj)		{return and = create(and,AND::new,this,obj);}
		public OrderBy 	orderBy(Object... obj)	{return orderBy = create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	groupBy(Object... obj)	{return groupBy = create(groupBy, GroupBy::new,this,obj);}
		public Limit 	limit(int start,int end){return limit = new Limit(this,start,end);}
	}
		
		
	/**
		 * 聚合函数
		 * @author yl
		 *
		 */
	public static class Aggregate extends AbstractCourse{
	
		public Aggregate(AbstractCourse previous, String courseType, Object... obj) {
			super(previous, CourseType.agg, obj);
		}
		protected Aggregate(AbstractCourse parent,String courseType,boolean isSon, Object... obj) {
			super(parent, courseType,isSon, obj);
		}
		//--------------------------------------------------
		private By 		by;
		private AND 	and;
		private GroupBy groupBy;
		private OrderBy orderBy;
		@SuppressWarnings("unused")
		private Limit 	limit;

		public By 		by(Object... obj)		{return by = create(by,By::new,this,obj);}
		public AND 		and(Object... obj)		{return and = create(and,AND::new,this,obj);}
		public OrderBy 	orderBy(Object... obj)	{return orderBy = create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	groupBy(Object... obj)	{return groupBy = create(groupBy, GroupBy::new,this,obj);}
		public Limit 	limit(int start,int end){return limit = new Limit(this,start,end);}
	}
	
	public static final class Count extends Aggregate{
		public Count(AbstractCourse parent, boolean isSon, Object... obj) {
			super(parent, CourseType.count, isSon, obj);
		}		
	}


	/**
	 * Add
	 * @author yl
	 *
	 */
	public static final class Add extends AbstractCourse{

		protected Add(AbstractCourse previous,Object... obj){
			super(previous,CourseType.add, obj);
		}
		
		public Add setValues(Object... values) {
			// TODO Auto-generated method stub
			return (Add)super.setValues(values);
		}


		//-------------------------------------------------------
		private By by;
		public By By(Object... obj){return by = create(by,By::new,this,obj);}
		}
		
	/**
	 * Put
	 * @author yl
	 *
	 */
	public static final class Put extends AbstractCourse{

		protected Put(AbstractCourse previous,Object... obj){
			super(previous,CourseType.put, obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object... obj){return by = create(by,By::new,this,obj);}
	}
		
	/**
	* Remove
	* @author yl
	*
	*/
	public static final class Remove extends AbstractCourse{

		protected Remove(AbstractCourse previous,Object... obj){
			super(previous,CourseType.remove, obj);
		}
		//-------------------------------------------------------
		private By by;
		public By By(Object... obj){return by = create(by,By::new,this,obj);}
		}
		
		
	/**
	 * where条件
	 * @author yl
	 *
	 */
	public static class Condition extends AbstractCourse{
		
		/**
		 * 用于直接创建
		 * @param previous
		 * @param obj
		 */
		protected Condition(AbstractCourse previous,Object... obj){
			super(previous,CourseType.con, obj);
		}
		
		/**
		 * 用于泛化节点创建
		 * @param previous
		 * @param courseType
		 * @param obj
		 */
		protected Condition(AbstractCourse previous,String courseType,Object... obj){
			super(previous,courseType, obj);
		}
		
		/**
		 * 用于子节点创建
		 * @param parent
		 * @param courseType
		 * @param isSon
		 * @param obj
		 */
		protected Condition(AbstractCourse parent,String courseType,boolean isSon, Object... obj) {
			super(parent, courseType,isSon, obj);
		}
		
		/**=*/
		public Condition eq(Object... value) {optype = CourseOpt.eq;setValues(value);return this;}
		/**!=*/
		public Condition ne(Object... value) {optype = CourseOpt.ne;setValues(value);return this;}
		/**>*/
		public Condition gt(Object... value) {optype = CourseOpt.gt;setValues(value);return this;}
		/**<*/
		public Condition lt(Object... value) {optype = CourseOpt.lt;setValues(value);return this;}
		/**>=*/
		public Condition ge(Object... value) {optype = CourseOpt.ge;setValues(value);return this;}
		/**<=*/
		public Condition le(Object... value) {optype = CourseOpt.le;setValues(value);return this;}
		
		//-------------------------------------------------------
		private Limit 	limit;
		private AND 	and;
		private OR 		or;
		private NOT 	not;
		private OrderBy orderBy;
		private GroupBy groupBy;
		
		public AND 		and(Object... obj) 		{return and = create(and, AND::new,this,obj);}
		public OR 		or(Object... obj)		{return or = create(or,OR::new,this,obj);}
		public NOT 		not(Object... obj)		{return not = create(not,NOT::new,this,obj);}
		public OrderBy 	orderBy(Object... obj)	{return orderBy = create(orderBy, OrderBy::new,this,obj);}
		public GroupBy 	GroupBy(Object... obj)	{return groupBy = create(groupBy, GroupBy::new,this,obj);}
		public Limit 	LIMIT(int start,int end){limit = new Limit(this,start,end);return limit;}
	}
	
	
	
	/**
	* By
	* @author yl
	*
	*/
	public static final class By extends Condition{

		protected By(AbstractCourse previous, Object... obj) {
			super(previous,CourseType.by, obj);
		}	
	}
		
	public static final class AND extends Condition{

		protected AND(AbstractCourse previous, Object... obj) {
			//注意如果非可变入参，此处会形成二维数组，需要转换后入参,如(Object[])obj
			super(previous,CourseType.and, obj);
		}
	}
	
	public static final class OR extends Condition{


		protected OR(AbstractCourse previous, Object... obj) {
			super(previous,CourseType.or, obj);
		}
		
	}
	
	public static final class NOT extends Condition{

		protected NOT(AbstractCourse previous, Object... obj) {
			super(previous,CourseType.not,obj);
		}
		
	}
	
	
		/**
		 * GroupBy
		 * @author yl
		 *
		 */
	public static final class GroupBy extends AbstractCourse{

		protected GroupBy(AbstractCourse previous,Object... obj){
			super(previous,CourseType.groupBy, obj);
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
		public static final class OrderBy extends AbstractCourse{

			public OrderBy(AbstractCourse previous,Object... obj){
				super(previous,CourseType.orderBy, obj);
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
	public static final class Limit extends AbstractCourse{
		@SuppressWarnings("unused")
		private Object[] element;
		protected Limit(AbstractCourse previous,int start,int end){
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
