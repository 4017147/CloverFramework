package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 可以返回自身对象接口
 * @author yl
 *
 */
public interface Accessable {
	AbstractCourse<?> getThis();
}
