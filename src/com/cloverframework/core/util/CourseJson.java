package com.cloverframework.core.util;

import java.util.List;
import java.util.Set;

public class CourseJson extends Jsonable{

	/**
	 * 对course进行json的字段限制提取
	 */
	private static final long serialVersionUID = 1L;

	public CourseJson(String type, String optype, List<String> fields, Set<String> types, List<Object> values,
			Jsonable son, Jsonable next) {
		super(type, optype, fields, types, values, son, next);
		// TODO Auto-generated constructor stub
	}

	
}
