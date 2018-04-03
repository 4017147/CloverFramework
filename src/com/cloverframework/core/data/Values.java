package com.cloverframework.core.data;

import java.util.Arrays;
import java.util.List;

public class Values implements CourseValues{
	private final List<Object> data;
	
	public Values(Object ...val) {
		data = Arrays.asList(val);
	}

	@Override
	public List<Object> getValues() {
		return data;
	}
	
	
	
}
