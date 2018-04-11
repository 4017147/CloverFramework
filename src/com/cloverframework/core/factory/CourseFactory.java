package com.cloverframework.core.factory;

import com.cloverframework.core.dsl.AbstractCourse;

public class CourseFactory {
	<T> T createCourse(Class<? extends AbstractCourse<?>> c) throws InstantiationException, IllegalAccessException {
		return (T)c.newInstance();
	}
}
