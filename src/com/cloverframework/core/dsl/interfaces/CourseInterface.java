package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.dsl.Callback;
import com.cloverframework.core.dsl.MainCreator;
import com.cloverframework.core.dsl.ResultSetter;
import com.cloverframework.core.dsl.ValueSetter;

/**
 * 接口限定Course必需提供的方法以组合功能
 * @author yl
 *
 */
public interface CourseInterface extends Callback,MainCreator,ValueSetter,ResultSetter{
	void destroy();
	
	String getType();
	
}
