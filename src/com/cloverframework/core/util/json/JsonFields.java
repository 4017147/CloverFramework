package com.cloverframework.core.util.json;

import java.util.List;
import java.util.Set;

public class JsonFields extends Jsonable{

	/**
	 * 对course进行json的字段限制提取
	 */
	private static final long serialVersionUID = 1L;

	public JsonFields(String type, String optype, List<String> fields, Set<String> types, String values,
			List<? extends Jsonable> son, Jsonable next) {
		super(type, optype, fields, types, values, son, next);
	}

	
}
