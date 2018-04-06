package com.cloverframework.core.dsl;

import java.util.List;

public interface ICourse<T> {
	void destroy();
	void setElements(Object... elements);
	public Object execute();
	Object getElements();
	List<Object> getValues();
	AbstractCourse<T> setValues(Object ...values);
}
