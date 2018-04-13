package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 重写接口的方法用于针对courseProxy的sharespace和newest的操作实现，
 * 在实际中，根据需要，使用合适的集合和对应的操作，如并发、或者队列，
 * 子类和父类都必需实现该接口，避免courseProxy子类继承发生的委托影响。
 */
public interface CourseOperation<C extends AbstractCourse> {
	
	public C getCurrCourse();
	
	public void setCurrCourse(C course);
	
	public C removeCurrCourse();
	
	public C getCourse(String id);
		
	public void setCourse(String id,C course);
	
	public C removeCourse(String id);
}
