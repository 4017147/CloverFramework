package com.clover.core.course;

import com.clover.core.factory.EntityFactory.EntityMethodInterceptor;

public final class CourseMethod{
	/**
	 * Ϊ����course��addLiteral����������ͬʱ��factory����ɼ���ͨ����������Ž�
	 * @param methodName
	 * @param course
	 * @param emi
	 */
	public static void addLiteral(String methodName,Course course,EntityMethodInterceptor emi) {
		course.addLiteral(methodName);
	}
}
