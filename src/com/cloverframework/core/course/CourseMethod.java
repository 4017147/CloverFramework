package com.cloverframework.core.course;

import com.cloverframework.core.factory.EntityFactory.EntityMethodInterceptor;

public final class CourseMethod{
	/**
	 * 为避免course的addLiteral方法公开，同时对factory组件可见，通过该类进行桥接
	 * @param methodName
	 * @param course
	 * @param emi
	 */
	public static void addLiteral(String methodName,Course course,EntityMethodInterceptor emi) {
		course.addLiteral(methodName);
	}
}
