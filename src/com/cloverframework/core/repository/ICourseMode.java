package com.clover.core.repository;

import com.clover.core.course.Course;
import com.clover.core.course.CourseProxy;

/**
* @author yl  
* 
*    
*/
public interface ICourseMode {
	<E> E get(Course course);
	int add(Course course);
	int put(Course course);
	int remove(Course course);
}
