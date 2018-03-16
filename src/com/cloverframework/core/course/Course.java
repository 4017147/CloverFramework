package com.clover.core.course;

import java.util.ArrayList;

/**
 * Course�ǻ���AbstractCourseʵ�ֵĻ����ϣ��ṩ�������û�ҵ��������Զ���ķ�����
 * ͨ�������ͱ�ʶ��ʹ���ܹ���װһ��ҵ����̡�
 * @author yl  
 */
public final class Course extends AbstractCourse<Course>{
	 /*
	  * ����˵����
	  * 1������ȫ����д,�������ϵ��ʵ����շ�����
	  * 2��������sql������ȫ����д
	  * 3��Ϊ�˱�֤�հ��ԣ�������ڲ���Ĺ��췽������Ϊprotected,������static final
	  */
	
	/** course��ʶ*/
	protected String id;
	/** ����ʱ��*/
	protected long createTime = System.currentTimeMillis();
	/** ƽ��ִ��ʱ��*/
	protected long avg_exe;
	/** ���ִ��ʱ��*/
	protected long max_exe;
	/** ���ִ��ʱ��*/
	protected long min_exe;
	
	/**
	 * 
	 * @param id ���course�ı�ʶ���������ַ������ܰ����ո�
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

	//���ڲ���
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
	 * ����һ��GET������Course������������ṩ����ȡ��Ӧ������
	 * @param obj
	 * @return
	 */
	public GET GET(Object ...obj){type = CourseType.GET;get = new GET(this,obj);return get;}
	
	/**
	 * ����һ��ADD������Course�������������ṩ�������Ӧ������
	 * @param obj
	 * @return
	 */
	public ADD ADD(Object ...obj){type = CourseType.ADD;add = new ADD(this,obj);return add;}
	
	/**
	 * ����һ��PUT������Course�������������ṩ������/�滻��Ӧ������
	 * @param obj
	 * @return
	 */
	public PUT PUT(Object ...obj){type = CourseType.PUT;put = new PUT(this,obj);return put;}
	
	/**
	 * ����һ��REMOVE������Course�������������ṩ��ɾ����Ӧ������
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
		//Ԥ��By
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
		//Ԥ��By
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
