package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.List;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.interfaces.CourseOperation;

/**
 * Action是一个支持多线程的course代理，在服务程序中建议使用该类来创建业务过程而非使用CourseProxy，
 * 该类还提供了批量提交并执行业务过程的特性。原则上，不建议方法中创建action对象，因为那样会消耗大量内存，
 * 而是让业务组件继承action。例如：UserService extends Action，还有一点，通过继承Action实现的业务组件，
 * 必需通过其对应的一个application组件来调用，以避免暴露其内部逻辑和方法。
 * @author yl
 * @param <T>
 *
 * @param <T>
 * @param <C>
 */
public class Action<T,C extends AbstractCourse> extends CourseProxy<T,C> implements CourseOperation<C>{
	
	/** 每个线程操作的course对象是相互独立的，对course操作前会先将course设置到local中，确保线程安全。*/
	private ThreadLocal<C> newest = new ThreadLocal<C>();
	
	/** work区，每个线程持有的一个相互独立的work集合，并且会被批量的提交至仓储执行*/
	private ThreadLocal<List<C>> workSpace = new ThreadLocal<List<C>>();
	
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
	

	/**
	 * 获取当前线程的work区
	 * @return
	 */
	public List<C> getWorkSpace() {
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
		workSpace.set(new ArrayList<C>(workSize));
		workable.remove();
		workable.set((byte)1);
	}
	
	/**
	 * 关闭工作区同时销毁其中的course
	 * @return
	 */
	public int endWork() {
		if(workSpace.get()!=null) {
			for(C course:workSpace.get()) {
				course.destroy();//销毁或重置
			}
			workSpace.get().clear();
		}
		workable.remove();
		workable.set((byte)0);
		return 0;
	}
	
	/**
	 * 将当前proxy对象移交仓储，仓储根据不用的proxy实例和泛化执行对应的操作
	 * @return
	 */
	public int push() {
		return repository.fromProxy(this);
	}
	
	
	@Override
	public void ready(C course) {
		if(workable.get()!=null && workable.get()==1 && course.getStatus()>Course.END)
			workSpace.get().add(course);
	}

	
	/**
	 * 从缓存中获取key对应的course，优先从共享区中获取
	 */
	@Override
	public C $(String key) {
		C course = super.$(key);
		if(course!=null) {
			return course;
		}else {
			$(key,null);
		}
		return null;
	}

	/**
	 * 根据给定的范围从范围内获取key对应的course
	 * @param key
	 * @param scope
	 * @return
	 */
	public C $(String key,int scope) {
		if(scope==domain)
			return super.$(key);
		else if(scope==local)
			return $(key,null);
		return null;
	}
	
	/**
	 * 从工作区中获取key对应的course,如果没有给定head则返回第一个key对应的course
	 * @param key
	 * @param head
	 * @return
	 */
	public C $(String key,String head) {
		for(C c:workSpace.get()) {
			if(c.id.equals(key) && head==null)
				return c;
			if(c.id.equals(key) && c.head.equals(head))
				return c;		
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		return super.toString();		
	}
}
