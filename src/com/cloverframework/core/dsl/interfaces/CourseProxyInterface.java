package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.repository.CourseRepository;

public interface CourseProxyInterface<T> {
	Course initCourse(String id,Course course,CourseProxy<T> proxy,byte status);
	public void setRepository(CourseRepository<T> repository);
	public T executeOne(Course course);
	public int commit(Course course);
}
