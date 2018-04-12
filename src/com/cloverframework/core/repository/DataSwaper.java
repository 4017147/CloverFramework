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
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.Wrapper;

public class DataSwaper<T,C extends AbstractCourse> implements DataSwap<T>{
	private C course;
	

	protected C getCourse() {
		return course;
	}

	protected void setCourse(C course) {
		this.course = course;
	}
	
	public DataSwaper(C course) {
		super();
		this.course = course;
	}

	@Override
	public Iterator<CourseWrapper> getCourseIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResult(DataSet<T> data) {
		course.setResult(new Result<T>(data.toList(),data.toObjectList(),data.toMap(),data.value()));
		
	}

	@Override
	public void setResult(List<T> list, List<Object> objectList, Map<String, Object> map, Object value) {
		course.setResult(new Result<T>(list, objectList, map, value));
	}

	@Override
	public void setResult(CourseResult<T> result) {
		course.setResult(result);
	}

	@Override
	public CourseWrapper transfer() {
		return new Wrapper(course);
	}

}
