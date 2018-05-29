package com.cloverframework.core.data;

import com.cloverframework.core.data.interfaces.ValueSet;
import com.cloverframework.core.dsl.AbstractCourse;


public final class VSet implements ValueSet{

	AbstractCourse course;
	
	
	public VSet(AbstractCourse course) {
		super();
		this.course = course;
	}


	@Override
	public AbstractCourse setBoolean(boolean... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setByte(byte... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setShort(short... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setInt(int... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setLong(long... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setFloat(float... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setDouble(double... val) {
		course.setValues(new Values(val));
		return course;
	}


	@Override
	public AbstractCourse setString(String... val) {
		course.setValues(new Values(val));
		return course;
	}

}
