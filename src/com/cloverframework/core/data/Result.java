package com.cloverframework.core.data;

import java.util.List;
import java.util.Map;

import com.cloverframework.core.data.interfaces.CourseResult;

public class Result<T> implements CourseResult<T>{
	private final List<T> list;
	private final List<Object> objectList;
	private final Map<String, Object> map;
	private final Object value;
	
	public Result(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
		super();
		this.list = list;
		this.objectList = objectList;
		this.map = map;
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
		return (int)value;
	}



	@Override
	public long getLong() {
		return (long)value;
	}



	@Override
	public double getDouble() {
		// TODO Auto-generated method stub
		return (double)value;
	}



	@Override
	public boolean getBoolean() {
		return (boolean)value;
	}

	@Override
	public String getString() {
		return (String)value;
	}



	@Override
	public byte getByte() {
		// TODO Auto-generated method stub
		return (byte)value;
	}



	@Override
	public short getShort() {
		return (short)value;
	}



	@Override
	public float getFloat() {
		return (float)value;
	}

}
