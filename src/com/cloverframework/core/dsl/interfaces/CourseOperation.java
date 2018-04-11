package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.dsl.Course;

/**
 * 重写接口的方法用于针对courseProxy的sharespace和newest的操作实现，
 * 在实际中，根据需要，使用合适的集合和对应的操作，如并发、或者队列，
 * 子类和父类都必需实现该接口，避免courseProxy子类继承发生的委托影响。
 */
public interface CourseOperation {
	
	public Course getCurrCourse();
	
	public void setCurrCourse(Course course);
	
	public Course removeCurrCourse();
	
	public Course getCourse(String id);
		
	public void setCourse(String id,Course course);
	
	public Course removeCourse(String id);
}
