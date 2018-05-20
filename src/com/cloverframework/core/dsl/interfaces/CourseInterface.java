package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.exceptions.ArgsCountNotMatch;

/**
 * 接口限定Course必需提供的方法以组合功能
 * @author yl
 *
 */
public interface CourseInterface {
	void destroy();
	public Object execute();
	CourseValues getValues();
	@SuppressWarnings("rawtypes")
	AbstractCourse setValues(Object ...values) throws ArgsCountNotMatch;
	String getType();
}
