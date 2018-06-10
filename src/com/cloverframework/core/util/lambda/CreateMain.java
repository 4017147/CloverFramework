package com.cloverframework.core.util.lambda;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 创建主线节点方法签名
 * @author yl
 *
 * @param <R>
 */
@FunctionalInterface
public interface CreateMain<R> {
	R apply(AbstractCourse<?> previous,Object ...obj);
}
