package com.cloverframework.core.data.interfaces;

import java.util.List;
import java.util.Map;

/**
 * 数据接口层将返回的数据分装的对象必需实现该接口，
 * 通常情况下，返回的一个结果应当有相应的转换作为该接口每个方法的返回值
 * @author yl
 *
 * @param <T>
 */
public interface DataSet<T> {
	/**返回领域类型List*/
	List<T> toList();
	/**返回对象类型List*/
	List<Object> toObjectList();
	/**返回一个对象类型map*/
	Map<String, Object> toMap();
	/**返回一个值*/
	Object value();
}
