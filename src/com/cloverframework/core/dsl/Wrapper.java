package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.data.interfaces.CourseWrapper;

public class Wrapper implements CourseWrapper{
	
	private AbstractCourse course;
	private Course head;

	public Wrapper(Course course) {
		super();
		this.course = course;
		this.head = course;
	}
	
	public Wrapper(AbstractCourse course) {
		super();
		this.course = course;
	}

	@Override
	public String id() {
		return head.getId();
	}

	@Override
	public String type() {
		return course.getType();
	}

	@Override
	public String opType() {
		return course.getOptype();
	}

	@Override
	public CourseWrapper previous() {
		return new Wrapper(course.previous);
	}

	@Override
	public CourseWrapper next() {
		return new Wrapper(course.next);
	}

	@Override
	public CourseWrapper parent() {
		return new Wrapper(course.parent);
	}

	@Override
	public List<CourseWrapper> sons() {
		List<CourseWrapper> list = new ArrayList<CourseWrapper>();
		for(AbstractCourse c:(List<AbstractCourse>)course.son) {
			list.add(new Wrapper(c));
		}
		return list;
	}

	@Override
	public List<String> fields() {
		return course.fields;
	}

	@Override
	public Set<String> types() {
		return course.types;
	}

	@Override
	public List<Object> entities() {
		return course.entities;
	}

	@Override
	public CourseValues value() {
		return course.getValues();
	}

	@Override
	public String json() {
		return course.getJsonString();
	}

}
