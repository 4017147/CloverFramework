package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.repository.CourseRepository;

public interface CourseProxyInterface<T,C extends AbstractCourse> {
	C initCourse(String id,C course,CourseProxy<T,C> proxy,byte status);
	public void setRepository(CourseRepository<T,C> repository);
	public T executeOne(C course);
	public int commit(C course);
}
