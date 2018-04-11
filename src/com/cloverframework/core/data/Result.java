package com.cloverframework.core.data;

import java.util.List;
import java.util.Map;

<<<<<<< HEAD
=======
import com.cloverframework.core.data.interfaces.CourseResult;

>>>>>>> treenode
public class Result<T> implements CourseResult<T>{
	private final List<T> list;
	private final List<Object> objectList;
	private final Map<String, Object> map;
	private final int count;
<<<<<<< HEAD
	
	public Result(List<T> list, List<Object> objectList, Map<String, Object> map, int count) {
=======
	private final Object value;
	
	public Result(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
>>>>>>> treenode
		super();
		this.list = list;
		this.objectList = objectList;
		this.map = map;
<<<<<<< HEAD
		this.count = count;
	}
	

	public Result(int count) {
		super();
		this.list = null;
		this.objectList = null;
		this.map = null;
		this.count = count;
	}

=======
		this.count = 1;
		this.value = value;
	}
	

>>>>>>> treenode

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

<<<<<<< HEAD
	@Override
	public int getCount() {
		return this.count;
=======



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
>>>>>>> treenode
	}

}
