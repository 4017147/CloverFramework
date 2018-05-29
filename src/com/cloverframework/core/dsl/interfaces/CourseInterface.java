package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.data.interfaces.CourseResult;
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
	
	Object execute();
	
	int commit();
	
	CourseValues getValues();
	
	AbstractCourse setValues(Object ...values);
	
	String getType();
	
	CourseResult<?> getResult();
	
	void setResult(CourseResult<?> result);
	
	
}
