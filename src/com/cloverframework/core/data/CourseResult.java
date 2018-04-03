package com.cloverframework.core.data;

import java.util.List;
import java.util.Map;

public interface CourseResult<T> {
	List<T> getList();
	List<Object> getObjectList();
	Map<String, Object> getMap();
	int getCount();
}
