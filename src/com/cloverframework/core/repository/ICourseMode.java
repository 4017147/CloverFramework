package com.cloverframework.core.repository;

import com.cloverframework.core.course.Course;

/**
* @author yl  
* 
*    
*/
public interface ICourseMode<T> {
	T get(Course course);
	int add(Course course);
	int put(Course course);
	int remove(Course course);
}
