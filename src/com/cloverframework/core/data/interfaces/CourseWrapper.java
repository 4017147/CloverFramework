package com.cloverframework.core.data.interfaces;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * 将与数据接口层交互的course包装为特定的操作对象
 * @author yl
 *
 */

import com.cloverframework.core.data.Result;
public interface CourseWrapper extends Iterator<CourseWrapper>{
	String id();
	String type();
	String opType();
	CourseWrapper previous();
	CourseWrapper parent();
	List<CourseWrapper> sons();
	List<String> fields();
	Set<String> types();
	List<Object> entities();
	CourseValues value();
	String json();
	CourseResult result(CourseResult result);
}
