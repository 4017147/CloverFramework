package com.clover.core.course;
/**
 * ��д�ӿڵķ����������courseProxy��ͬeden��newest�Ĳ���ʵ�֣�
 * ��ʵ���У�������Ҫ��ʹ�ú��ʵļ��ϺͶ�Ӧ�Ĳ������粢�������߶��У�
 * ����ͨ��������д��Щ��������ʵ�֣�������������Խ��иĶ���
 */
public interface CourseOperation {
	
	public Course getCurrCourse();
	
	public void setCurrCourse(Course course);
	
	public Course removeCurrCourse();
	
	public Course getCourse(String id);
		
	public void addCourse(String id,Course course);
	
	public Course removeCourse(String id);
}
