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
	public A setBoolean(boolean... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A setByte(byte... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_short(short... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_int(int... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_long(long... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_float(float... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_double(double... val) {
		course.setValues(new Values(val));
		return null;
	}


	@Override
	public A v_String(String... val) {
		course.setValues(new Values(val));
		return null;
	}

}
