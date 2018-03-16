package com.cloverframework.core.factory;

import com.cloverframework.core.course.Course;

public abstract class AbstractEntityFactory {
	public abstract boolean isMatchDomain(Class<?> entityClass,Class<?> serviceClass);
	public abstract <E> E getStaple(Class<E> E);
	public abstract Course resolve(Course process);
	public abstract Course assemble(Course process);
}
