package com.cloverframework.core.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cloverframework.core.data.Result;
import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.data.interfaces.CourseWrapper;
import com.cloverframework.core.data.interfaces.DataSet;
import com.cloverframework.core.data.interfaces.DataSwap;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Wrapper;

@SuppressWarnings("unchecked")
public class DataSwaper<T> implements DataSwap<T>,Iterable<CourseWrapper>{
	private Wrapper wrapper;
	
	
	
	public DataSwaper(AbstractCourse course) {
		super();
		this.wrapper = new Wrapper(course);
	}

	@Override
	public void setResult(DataSet<T> data) {
		wrapper.result(new Result<T>(data.toList(),data.toObjectList(),data.toMap(),data.value()));
		
	}

	@Override
	public void setResult(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
		wrapper.result(new Result<T>(list, objectList, map, value));
	}

	@Override
	public void setResult(CourseResult<T> result) {
		wrapper.result(result);
	}

	@Override
	public Iterator<CourseWrapper> iterator() {
		return wrapper;
	}

	@Override
	public CourseWrapper open() {
		return wrapper;
	}

	@Override
	public void close() {
		this.wrapper = null;
	}
	
	
}
