package com.cloverframework.core.data;

import com.cloverframework.core.data.interfaces.ValueSet;
import com.cloverframework.core.dsl.AbstractCourse;


public final class VSet<A> implements ValueSet<A>{

	AbstractCourse<A> course;
	
	
	public VSet(AbstractCourse<A> course) {
		super();
		this.course = course;
	}


	@Override
	public AbstractCourse<A> setBoolean(boolean... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setByte(byte... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setShort(short... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setInt(int... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setLong(long... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setFloat(float... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setDouble(double... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse<A> setString(String... val) {
		course.setValues(new Values(val));
		return course;
	}

}
