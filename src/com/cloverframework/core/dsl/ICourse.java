package com.cloverframework.core.dsl;

import java.util.List;

public interface ICourse<T> {
	void destroy();
	public Object execute();
	List<Object> getValues();
	AbstractCourse<T> setValues(Object ...values);
	String getType();
}
