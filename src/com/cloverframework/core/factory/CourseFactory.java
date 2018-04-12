package com.cloverframework.core.factory;

public class CourseFactory {
	public static <C> C create(Class<C> c) throws InstantiationException, IllegalAccessException {
		return c.newInstance();
	}
}
