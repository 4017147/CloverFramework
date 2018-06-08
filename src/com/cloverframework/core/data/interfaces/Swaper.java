package com.cloverframework.core.data.interfaces;

import java.util.List;
import java.util.Map;

/**
 * 仓储层通过该接口与数据接口层实现交互
 * @author yl
 *
 * @param <T>
 */
public interface Swaper<T> extends Iterable<Wrapper>{
	Wrapper open();
	void setResult(DataSet<T> data);
	void setResult(List<T> list, List<Object> objectList, Map<String, Object> map, Object value);
	void setResult(Result<T> result);
	void close();
}
