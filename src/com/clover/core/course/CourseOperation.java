package com.clover.core.course;
/**
 * 重写接口的方法用于针对courseProxy不同eden和newest的操作实现，
 * 在实际中，根据需要，使用合适的集合和对应的操作，如并发、或者队列，
 * 可以通过子类重写这些方法即可实现，无须对其他特性进行改动。
 */
public interface CourseOperation {
	
	public Course getCurrCourse();
	
	public void setCurrCourse(Course course);
	
	public Course removeCurrCourse();
	
	public Course getCourse(String id);
		
	public void addCourse(String id,Course course);
	
	public Course removeCourse(String id);
}
