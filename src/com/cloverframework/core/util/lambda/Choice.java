package com.cloverframework.core.util.lambda;

/**
 * 创建动态DSL时提供分支语句的执行函数，其输入类型与调用者一致
 * @author yl
 *
 * @param <A>
 */
@FunctionalInterface
public interface Choice<A> {
	void apply(A a);
}
