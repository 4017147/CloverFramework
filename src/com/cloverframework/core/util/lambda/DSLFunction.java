package com.cloverframework.core.util.lambda;

/**
 * 创建DSL语句签名
 * @author yl
 *
 */
@FunctionalInterface
public interface DSLFunction {
	void build(String id);
}
