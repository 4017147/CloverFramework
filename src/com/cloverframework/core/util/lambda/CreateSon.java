package com.cloverframework.core.util.lambda;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 创建子节点方法签名
 * @author yl
 *
 * @param <R>
 */
@FunctionalInterface
public interface CreateSon<R> {
	R apply(AbstractCourse parent,boolean isSon, Object... obj);
}
