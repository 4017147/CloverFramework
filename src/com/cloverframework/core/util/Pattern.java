package com.cloverframework.core.util;

/**
 * 节点获取的参数匹配规则，通过实现该接口可自定义匹配规则
 * @author yl
 *
 */
public interface Pattern {
	boolean isMatch(Object obj);
	default boolean isMatch() {
		return false;	
	}
}
