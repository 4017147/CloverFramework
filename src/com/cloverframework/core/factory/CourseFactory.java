package com.cloverframework.core.factory;

public class CourseFactory {
	public static <C> C create(Class<C> c) throws InstantiationException, IllegalAccessException {
		//TODO 对象池
		return c.newInstance();
	}
}
