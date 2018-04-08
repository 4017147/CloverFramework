package com.cloverframework.core.dsl;

import com.cloverframework.core.repository.CourseRepository;

public interface ICourseProxy<T> {
	Course initCourse(String id,Course course,CourseProxy<T> proxy,byte status);
	public void setRepository(CourseRepository<T> repository);
	public T executeOne(Course course);
	public int commit(Course course);
}
