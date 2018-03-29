package com.cloverframework.core.util;

/**
 * course的节点类型，继承该接口可扩展更多的类型
 * @author yl
 *
 */
public interface CourseType {
	String get = "get";
	String add = "add";
	String put = "put";
	String remove = "remove";
	String con = "con";
	String by = "by";
	String and = "and";
	String or = "or";
	String not = "not";
	String groupBy = "groupBy";
	String orderBy = "orderBy";
}
