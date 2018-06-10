package com.cloverframework.core.data.interfaces;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * 将与数据接口层交互的course包装为特定的操作对象
 * @author yl
 *
 */
public interface Wrapper extends Iterator<Wrapper>{
	String id();
	String type();
	String opType();
	Wrapper previous();
	Wrapper parent();
	List<Wrapper> sons();
	List<String> fields();
	Set<String> types();
	List<Object> entities();
	Values value();
	String json();
	Result<?> getResult();
	void setResult(Result<?> result);
}
