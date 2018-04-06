package com.cloverframework.core.data;

import com.cloverframework.core.dsl.AbstractCourse;

public interface CourseData<T> {
	AbstractCourse<T> setValues(Object ...val);
	CourseValues getValues();
	void setResult(CourseResult<?> result);
	CourseResult<?> getResult();
}
