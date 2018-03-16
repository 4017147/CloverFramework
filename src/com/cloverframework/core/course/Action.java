package com.clover.core.course;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.domain.DomainService;

/**
 * Action��һ��֧�ֶ��̵߳�course�����ڷ�������н���ʹ�ø���������ҵ����̶���ʹ��CourseProxy��
 * ���໹�ṩ�������ύ��ִ��ҵ����̵����ԡ�
 * @author yl
 *
 * @param <T>
 */
public class Action<T> extends CourseProxy implements CourseOperation{
	{
		/** ���ڼ����������ֵ�ķ���ջ���Ƿ�Ϸ���
		 * �����ķ����е��ø����е�START()��START(args)�����������������п����ã��������أ�����Ҫ��Ӧ��+1*/
		level +=1;		
	}
	
	/** ÿ���̲߳�����course�������໥�����ģ���course����ǰ���Ƚ�course���õ�local�У�ȷ���̰߳�ȫ��*/
	private static ThreadLocal<Course> newest = new ThreadLocal<Course>();
	
	/** eden�������ڻ���ÿ���̲߳�����couse*/
	private ConcurrentHashMap<String,Course> eden = new ConcurrentHashMap<String,Course>();
	
	/** work����ÿ���̳߳��е�һ���໥������work���ϣ����һᱻ�������ύ���ִ�ִ��*/
	private static ThreadLocal<List<Course>> work = new ThreadLocal<List<Course>>();
	
	/** ÿ��work���ϳ�ʼ��С*/
	private static byte workSize = 10;
	
	/** ֻ�е�workableΪ1��ʱ��course�Żᱻ����work��*/
	private static ThreadLocal<Byte> workable = new  ThreadLocal<Byte>();
	

	
	public Action() {}
	
	public Action(DomainService service) {
		super(service);
	}
	
	
	@Override
	public Course getCurrCourse() {
		return newest.get();
	}
	
	@Override
	public Course removeCurrCourse() {
		Course old = getCurrCourse();
		newest.remove();
		return old;
	}
	
	@Override
	public void setCurrCourse(Course course) {
		newest.set(course);
	}
	
	@Override
	public Course getCourse(String id) {
		return eden.get(id);
	}
	
	@Override
	public void addCourse(String id,Course course) {
		eden.put(id, course);
	}
	
	@Override
	public Course removeCourse(String id) {
		return eden.remove(id);
	}
	

	/**
	 * ��ȡ��ǰ�̵߳�work��
	 * @return
	 */
	public static List<Course> getWork() {
		return work.get();
	}

	/**
	 * ��ȡwork����Сֵ
	 * @return
	 */
	public static byte getWorkSize() {
		return workSize;
	}

	/**
	 * ����work����С
	 * @param workSize
	 */
	public static void setWorkSize(byte workSize) {
		Action.workSize = workSize;
	}

	/**
	 * �ڴ˷���֮������ȡ��course����work���Ĳ�������Ч��
	 */
	public void startWork() {
		work.remove();
		work.set(new ArrayList<Course>(workSize));
		workable.remove();
		workable.set((byte)1);
	}
	
	/**
	 * �˷����������ύwork��������work������Ч��
	 * @return
	 */
	public int endWork() {
		int result = repository.fromAction(this);
		workable.remove();
		workable.set((byte)0);
		return result;
	}
	
	/**
	 * �˷����������ύwork��������գ�work������Ȼ����Ч״̬,���ǲ�����ͨ���÷�����������״̬
	 * @return
	 */
	public int execute() {
		int result =  repository.fromAction(this);
		work.get().clear();
		return result;
	}

	@Override
	public Object executeOne() {
		return repository.query(newest.get());
	}
	
	
	/**
	 * {@inheritDoc}
	 * �� {@link Action#startWork()}��������������course������work��
	 */
	@Override
	public Course START() {
		Course course = super.START();
		if(workable.get()!=null && workable.get()==1)
			work.get().add(course);
		return course;
	}

	/**
	 * {@inheritDoc}
	 * �� {@link Action#startWork()}��������������course������work��
	 */
	@Override
	public Course START(String id) {
		Course course = super.START(id);
		if(workable.get()!=null && workable.get()==1)
			work.get().add(course);
		return course;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		for(String key:eden.keySet()) {
			Course course = eden.get(key);
			course.condition1 = true;
			course.condition2 = true;
			sb.append(course.toString()+"\n");
		}
		return sb.toString();			
	}
	
	
	
}
