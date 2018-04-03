package com.cloverframework.core.data;

import java.util.List;
import java.util.Map;

public class Result<T> implements CourseResult<T>{
	private final List<T> list;
	private final List<Object> objectList;
	private final Map<String, Object> map;
	private final int count;
	
	public Result(List<T> list, List<Object> objectList, Map<String, Object> map, int count) {
		super();
		this.list = list;
		this.objectList = objectList;
		this.map = map;
		this.count = count;
	}
	

	public Result(int count) {
		super();
		this.list = null;
		this.objectList = null;
		this.map = null;
		this.count = count;
	}


	@Override
	public List<T> getList() {
		return this.list;
	}

	@Override
	public List<Object> getObjectList() {
		return this.objectList;
	}

	@Override
	public Map<String, Object> getMap() {
		return this.map;
	}

	@Override
	public int getCount() {
		return this.count;
	}

}
