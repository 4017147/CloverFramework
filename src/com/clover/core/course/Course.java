package com.clover.core.course;

import java.util.ArrayList;

/**
 * Course是基于AbstractCourse实现的基础上，提供了面向用户业务过程语言定义的方法，
 * 通过方法和标识的使用能够组装一个业务过程。
 * @author yl  
 */
public final class Course extends AbstractCourse<Course>{
	 /*
	  * 规则说明：
	  * 1、类名全部大写,两个以上单词的用驼峰命名
	  * 2、公开的sql方法名全部大写
	  * 3、为了保证闭包性，此类和内部类的构造方法修饰为protected,类则是static final
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

	private GET 	get;
	private ADD 	add;
	private PUT 	put;
	private REMOVE 	remove;
	
	/**
	 * 开启一个GET描述的Course，代表从数据提供方获取相应的内容
	 * @param obj
	 * @return
	 */
	public GET GET(Object ...obj){type = CourseType.GET;get = new GET(this,obj);return get;}
	
	/**
	 * 开启一个ADD描述的Course，代表在数据提供方添加相应的内容
	 * @param obj
	 * @return
	 */
	public ADD ADD(Object ...obj){type = CourseType.ADD;add = new ADD(this,obj);return add;}
	
	/**
	 * 开启一个PUT描述的Course，代表在数据提供方更新/替换相应的内容
	 * @param obj
	 * @return
	 */
	public PUT PUT(Object ...obj){type = CourseType.PUT;put = new PUT(this,obj);return put;}
	
	/**
	 * 开启一个REMOVE描述的Course，代表在数据提供方删除相应的内容
	 * @param obj
	 * @return
	 */
	public REMOVE REMOVE(Object ...obj){type = CourseType.REMOVE;remove = new REMOVE(this,obj);return remove;}

	/**
	 * Get
	 * @author yl
	 *
	 */
	public static final class GET extends AbstractCourse<GET>{

		protected GET(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}

		//-------------------------------------------------------
		private By 		by;
		private GroupBy groupBy;
		private OrderBy orderBy;
		private LIMIT 	limit;
			
		public By 		By(Object obj){by = new By(this,obj);return by;}
		public GroupBy 	GroupBy(Object obj){groupBy = new GroupBy(this,obj);return groupBy;}
		public OrderBy 	OrderBy(Object obj){orderBy = new OrderBy(this,obj);return orderBy;}
		public LIMIT 	LIMIT(int start,int end){limit = new LIMIT(this,start,end);return limit;}
	}
		
		
	/**
	 * Add
	 * @author yl
	 *
	 */
	public static final class ADD extends AbstractCourse<ADD>{
		protected ADD(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object ...obj){by = new By(this,obj);return by;}
		}
		
	/**
	 * Put
	 * @author yl
	 *
	 */
	public static final class PUT extends AbstractCourse<PUT>{
		protected PUT(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}
		//-------------------------------------------------------
		//预留By
		private By by;
		public By By(Object ...obj){by = new By(this,obj);return by;}
	}
		
	/**
	* Remove
	* @author yl
	*
	*/
	public static final class REMOVE extends AbstractCourse<REMOVE>{
		protected REMOVE(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}
		//-------------------------------------------------------
		private By by;
		public By By(Object ...obj){by = new By(this,obj);return by;}
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
		protected By(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
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
		private LIMIT 	limit;
			
		public OrderBy 	OrderBy(Object ...obj){orderBy = new OrderBy(this,obj);return orderBy;}
		public GroupBy 	GroupBy(Object ...obj){groupBy = new GroupBy(this,obj);return groupBy;}
		public LIMIT 	LIMIT(int start,int end){limit = new LIMIT(this,start,end);return limit;}
		}
		
		/**
		 * GroupBy
		 * @author yl
		 *
		 */
	public static final class GroupBy extends AbstractCourse<GroupBy>{
		protected GroupBy(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}
			//-------------------------------------------------------
		private LIMIT limit;
		public LIMIT LIMIT(int start,int end){limit = new LIMIT(this,start,end);return limit;}
	}
		
	/**
	 * OrderBy
	* @author yl
	*
	*/
	public static final class OrderBy extends AbstractCourse<OrderBy>{
		public OrderBy(AbstractCourse<?> parent,Object ...obj){
			this.parent = parent;
			setElements(obj);
		}
		//-------------------------------------------------------
		private LIMIT limit;
		public LIMIT LIMIT(int start,int end){limit = new LIMIT(this,start,end);return limit;}
	}
	
	/**
	* Limit
	* @author yl
	*
	*/
	public static final class LIMIT extends AbstractCourse<LIMIT>{
		private Object[] element;
		protected LIMIT(AbstractCourse<?> parent,int start,int end){
			String obj = String.valueOf(start)+","+String.valueOf(end);
			this.parent = parent;
			setElements(obj);
		}

		@Override
		protected void setElements(Object... element) {
			this.element = element;
		}
		
			//-------------------------------------------------------
		}
}
