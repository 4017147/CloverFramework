package com.clover.core.course;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.domain.DomainService;

/**
 * Action是一个支持多线程的course代理，在服务程序中建议使用该类来创建业务过程而非使用CourseProxy，
 * 该类还提供了批量提交并执行业务过程的特性。
 * @author yl
 *
 * @param <T>
 */
public class Action<T> extends CourseProxy implements CourseOperation{
	{
		/** 用于计算产生字面值的方法栈长是否合法，
		 * 如果别的方法中调用该类中的START()或START(args)方法（仅开发过程中可设置，对外隐藏），需要相应的+1*/
		level +=1;		
	}
	
	/** 每个线程操作的course对象是相互独立的，对course操作前会先将course设置到local中，确保线程安全。*/
	private static ThreadLocal<Course> newest = new ThreadLocal<Course>();
	
	/** eden区，用于缓存每个线程产生的couse*/
	private ConcurrentHashMap<String,Course> eden = new ConcurrentHashMap<String,Course>();
	
	/** work区，每个线程持有的一个相互独立的work集合，并且会被批量的提交至仓储执行*/
	private static ThreadLocal<List<Course>> work = new ThreadLocal<List<Course>>();
	
	/** 每个work集合初始大小*/
	private static byte workSize = 10;
	
	/** 只有当workable为1的时候，course才会被填入work区*/
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
	 * 获取当前线程的work区
	 * @return
	 */
	public static List<Course> getWork() {
		return work.get();
	}

	/**
	 * 获取work区大小值
	 * @return
	 */
	public static byte getWorkSize() {
		return workSize;
	}

	/**
	 * 设置work区大小
	 * @param workSize
	 */
	public static void setWorkSize(byte workSize) {
		Action.workSize = workSize;
	}

	/**
	 * 在此方法之后定义或获取的course填入work区的操作是有效的
	 */
	public void startWork() {
		work.remove();
		work.set(new ArrayList<Course>(workSize));
		workable.remove();
		workable.set((byte)1);
	}
	
	/**
	 * 此方法会立刻提交work区，填入work区是无效的
	 * @return
	 */
	public int endWork() {
		int result = repository.fromAction(this);
		workable.remove();
		workable.set((byte)0);
		return result;
	}
	
	/**
	 * 此方法会立刻提交work区，并清空，work填入仍然是有效状态,但是并不能通过该方法开启允许状态
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
	 * 当 {@link Action#startWork()}开启，所产生的course会填入work区
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
	 * 当 {@link Action#startWork()}开启，所产生的course会填入work区
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
