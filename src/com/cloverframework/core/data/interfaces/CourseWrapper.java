package com.cloverframework.core.data.interfaces;

import java.util.List;
import java.util.Set;
/**
 * 将与数据接口层交互的course包装为特定的操作对象
 * @author yl
 *
 */
public interface CourseWrapper{
	String id();
	String type();
	String opType();
	CourseWrapper previous();
	CourseWrapper next();
	CourseWrapper parent();
	List<CourseWrapper> sons();
	List<String> fields();
	Set<String> types();
	List<Object> entities();
	CourseValues value();
	String json();
}
