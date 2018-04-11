package com.cloverframework.core.data;

import java.util.List;
import java.util.Map;

import com.cloverframework.core.data.interfaces.CourseResult;

public class Result<T> implements CourseResult<T>{
	private final List<T> list;
	private final List<Object> objectList;
	private final Map<String, Object> map;
	private final int count;
	private final Object value;
	
	public Result(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
		super();
		this.list = list;
		this.objectList = objectList;
		this.map = map;
		this.count = 1;
		this.value = value;
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
	public int getInt() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getLong() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public double getDouble() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public boolean getBoolean() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

}
