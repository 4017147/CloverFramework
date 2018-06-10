package com.cloverframework.core.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cloverframework.core.data.CourseResult;
import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.data.interfaces.Wrapper;
import com.cloverframework.core.data.interfaces.DataSet;
import com.cloverframework.core.data.interfaces.Swaper;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.CourseWrapper;


public class DataSwaper<T> implements Swaper<T>,Iterable<Wrapper>{
	private Wrapper wrapper;
	
	
	
	public DataSwaper(AbstractCourse<?> course) {
		super();
		this.wrapper = new CourseWrapper(course);
	}
	
	public DataSwaper(Wrapper wrapper) {
		super();
		this.wrapper = wrapper;
	}

	@Override
	public void setResult(DataSet<T> data) {
		wrapper.setResult(new CourseResult<T>(data.toList(),data.toObjectList(),data.toMap(),data.value()));
		
	}

	@Override
	public void setResult(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
		wrapper.setResult(new CourseResult<T>(list, objectList, map, value));
	}

	@Override
	public void setResult(Result<T> result) {
		wrapper.setResult(result);
	}

	@Override
	public Iterator<Wrapper> iterator() {
		return wrapper;
	}

	@Override
	public Wrapper open() {
		return wrapper;
	}

	@Override
	public void close() {
		this.wrapper = null;
	}
	
	
}
