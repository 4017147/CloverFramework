package com.cloverframework.core.util.lambda;

import com.cloverframework.core.dsl.AbstractCourse;

@FunctionalInterface
public interface CreateMain<R> {
	R apply(AbstractCourse previous,Object ...obj);
}
