package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.interfaces.CourseOperation;

/**
 * Action是一个支持多线程的course代理，在服务程序中建议使用该类来创建业务过程而非使用CourseProxy，
 * 该类还提供了批量提交并执行业务过程的特性。原则上，不建议方法中创建action对象，因为那样会消耗大量内存，
 * 而是让业务组件继承action。例如：UserService extends Action
 * @author yl
 * @param <T>
 *
 * @param <T>
 * @param <C>
 */
public class Action<T,C extends AbstractCourse> extends CourseProxy<T,C> implements CourseOperation<C>{
//	{
//		/** 用于计算产生字面值的方法栈长是否合法，
//		 * 如果别的方法中调用该类中的START()或START(args)方法（仅开发过程中可设置，对外隐藏），需要相应的+1*/
//		level +=1;		
//	}
	
	/** 每个线程操作的course对象是相互独立的，对course操作前会先将course设置到local中，确保线程安全。*/
	private ThreadLocal<C> newest = new ThreadLocal<C>();
	
	/** share区，用于缓存每个线程产生的course*/
	private ConcurrentHashMap<String,C> shareSpace = new ConcurrentHashMap<String,C>();
	
//	/** work区，每个线程持有的一个相互独立的work集合，并且会被批量的提交至仓储执行*/
//	private ThreadLocal<List<C>> workSpace = new ThreadLocal<List<C>>();
	/** work区，每个线程持有的一个相互独立的work集合，并且会被批量的提交至仓储执行*/
	private ThreadLocal<LinkedHashMap<String, C>> workSpace = new ThreadLocal<LinkedHashMap<String, C>>();
	
	/** 每个work集合初始大小*/
	private static byte workSize = 20;
	
	/** 只有当workable为1的时候，course才会被填入work区*/
	private static ThreadLocal<Byte> workable = new  ThreadLocal<Byte>();
	

	
	
	public Action() {}
	
	public Action(DomainService domainService) {
		super(domainService);
	}
	
	
	@Override
	public C getCurrCourse() {
		return newest.get();
	}
	
	@Override
	public C removeCurrCourse() {
		C old = getCurrCourse();
		newest.remove();
		return old;
	}
	
	@Override
	public void setCurrCourse(C course) {
		newest.set(course);
	}
	
	@Override
	public C getCourse(String id) {
		return shareSpace.get(id);
	}
	
	@Override
	public void setCourse(String id,C course) {
		shareSpace.put(id, course);
	}
	
	@Override
	public C removeCourse(String id) {
		return shareSpace.remove(id);
	}
	

	/**
	 * 获取当前线程的work区
	 * @return
	 */
	public LinkedHashMap<String, C> getWorkSpace() {
		return workSpace.get();
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
	 * 开启工作区
	 */
	public void startWork() {
		workSpace.remove();
		workSpace.set(new LinkedHashMap<String, C>(workSize));
		workable.remove();
		workable.set((byte)1);
	}
	
	/**
	 * 关闭工作区同时销毁其中的course
	 * @return
	 */
	public int endWork() {
		if(workSpace.get()!=null) {
			for(C course:workSpace.get("")) {
				course.destroy();
			}
			workSpace.get().clear();
		}
		workable.remove();
		workable.set((byte)0);
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int push() {
		return repository.fromAction(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public C Master() {
		//必需
		return super.Master();
	}

	/**
	 * {@inheritDoc}，
	 * 当 {@link Action#startWork()}开启，匹配到的course会填入workspace
	 */
	@Override
	public C Master(String id) {
		//必需
		C course = super.Master(id);
		return course;
	}

	/**
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，分支的course将存入workspace
	 */
	public C Branch(String id) {
		//必需
		C course = super.Branch(id);
		return course;
	}
	
	/**
	 * {@inheritDoc}，
	 * 当 {@link Action#startWork()}开启，将fork或无给定id的course加入工作区
	 */
	public void END() {
		//必需
		super.END();
		C course = getCurrCourse();
		if(workable.get()!=null && workable.get()==1 && course.getStatus()==Course.END)
			workSpace.get().put(course.id,course);
	}	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		for(String key:shareSpace.keySet()) {
			C course = shareSpace.get(key);
			sb.append(course.toString()+"\n");
		}
		return sb.toString();			
	}
}
