package com.clover.core.course;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.clover.core.factory.EntityFactory;
import com.domain.DomainService;

/**
 * ����course�ĳ���ĸ��࣬������һ������ṹ���ԣ���ʵ���˴󲿷ֻ�������
 * @author yl
 * 
 * 
 */
public abstract class AbstractCourse<T> {
	DomainService domainService;//�ϼ�����
	
	/**course����*/
	CourseProxy proxy;//�ϼ�����
	
	/**�ڵ�Ԫ��*/
	private Object[] elements;
	
	/**���ڵ�*/
	AbstractCourse<?> parent;
	
	/**�����б�*/
	List<String> literalList;//�ϼ�����
	
	/**�Ƿ����simpleName */
	public volatile boolean condition1;//�����ݣ��������ܿ���û��ʹ���������ͣ����������������Ӧ���ϼ�����
	
	/**�Ƿ������ɫ */
	public volatile boolean condition2;//������
	
	/**�����*/
	public static final byte WAIT 		= 0;
	/**�������ֵ(��lambda)*/
	public static final byte LAMBDA 	= 1;
	/**�������ֵ(�ӷ���)*/
	public static final byte METHOD 	= 2;
	/**�������ֵ(��lambda��Ԫ)*/
	public static final byte LAMBDA_TE 	= 3;
	/**�������*/
	public static final byte FILL 		=-1;
	/**�ر�*/
	public static final byte END 		=-2;
	/**�쳣*/
	public static final byte ERROR 		=-3;
	
	/**
	 * ��ʾ��course��ǰ״̬
	 * ��һ���߳��У�proxy����course�Ͷ�̬�����ȡ����ֵ�����ǰ�˳��ִ�еģ�Ҳ���ǵ�ǰ��statusΪ������
	 * �������Щ������ȡ����status�쳣������ζ�������̷߳����˲���Ԥ�ϵĴ��������ֹ��
	 * ���Ǽ����е�course���ܲ�δ�Ƴ�����ǰ�߳������������ͬ��ID���п��ܷ��������
	 * ��һ�㷢����һ��course�жϺ���һ��course����֮ǰ��������������Ǻܵ͵ġ�
	 * 
	 */
	volatile byte status = WAIT;//�ϼ�����
	
	
	/*----------------------private method-------------------- */
	
	
	
	/**
	 * ���ýڵ��Ԫ�أ��÷����Ǹ���ί������Ĺ��췽�����õġ�
	 * ������ڵ�status�쳣���򲻻�ִ�У���������ִ�в�ˢ�¸��ڵ��status��
	 * ִ�к󶼻Ὣ�����б����
	 * @param elements
	 */
	protected void setElements(Object... elements) {
		try {
			status = parent==null?null:parent.status;
			//TODO ���쳣�������δ���
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
	 * ���ٸ�Course
	 */
	protected void destroy() {}


	/**
	 * ������ʵ���getter��������ֵ��䵽element������
	 * 1���������Ԫ��Ϊnull����䣨�������ֵ�б�Ԫ�ص�next���ڣ�
	 * 2���������Ԫ��Ϊ����ʵ�壬���Ϊ�Ϸ�������ʵ������ӣ�������Ϸ����Ƴ���
	 * 3�������Ԫ�ز���ʵ�壬���������ֵ��䡣
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
			for(;t<temps.length;t++) {//��ʣ�������ֵ��䣨�������ʣ�ࣩ
				if(k>=literalList.size()) 
					break;
				temps[t] = literalList.get(k);
				k++;
			}
			return Arrays.copyOf(temps, t);//ȥ��Ϊnull����Ч�±�
		}
		return null;
	}


	/**
	 * ��ӡ�ڵ�Ԫ�أ�java.lang�����ͻ�ֱ������������String�������������.������������ʵ�����������������
	 * @param course
	 * @param elements
	 * @param condition1 �Ƿ����simpleName
	 * @param condition2 �Ƿ������ɫ,����ɫͨ��ANSIת�����ж���
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
					builder.append(blank);//�׸�Ԫ������
				else
					builder.append(comma);	
				if (elements[i].getClass().getPackage() == Package.getPackage("java.lang")) {
					if(elements[i].getClass()==String.class){
						if(condition1) {
							String fullName = elements[i].toString();
							//��ȡ�������ͺ���������simpleName
							//��ֹsubstring�ڴ�ռ��
							String simpleName = new String(fullName.substring(fullName.lastIndexOf(".",fullName.lastIndexOf(".")-1)+1, fullName.length()).replace(".get", "."));
							builder.append(simpleName).append(blank);	
						}else
							builder.append(elements[i]).append(blank);												
					}else
						builder.append(elements[i]).append(blank);	
				}else {
					String simpleName = elements[i].getClass().getSimpleName();
					int index = simpleName.indexOf("$$");//�������������ȡ
					builder.append(new String(simpleName.substring(0, index==-1?simpleName.length():index))).append(blank);					
				}
			}
		}
		return builder.toString();
	}


	/**
	 * ��ӡ�ӽڵ�
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
	 * ������ǰ��һ��course��䣬���course�����������䣬
	 * ����ִ��end�����ڴ������¶��Ǳ���ģ����û��������ִ��end��
	 * �ᵼ�µ�ǰ�����course����һ�β�������������������л���
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
	 * ֱ��END()��ִ�е�ǰ����course���
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
	 * �ṩһ����course�Ľṹ������������Ϊ�����ṩ���㣬ʵ�ʹ��̺����������Ĳ����ܻ��ϵȺš�
	 * �������ڲ����ʼ�������е���,��Ϊthis
	 */
	@Override
	public String toString() {
		setCondition(parent);
		return DataString(this,elements,condition1,condition2) + fieldString(this);
	}

	
}
