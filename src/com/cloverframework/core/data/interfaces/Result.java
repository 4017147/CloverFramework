package com.cloverframework.core.data.interfaces;

import java.util.List;
import java.util.Map;
/**
 * 数据访问层返回的内容通过实现该接口的类来封装，
 * service可以在course执行完成后通过该接口获取数据
 * @author yl
 *
 * @param <T>
 */
public interface Result<T> {
	List<T> getList();
	List<Object> getObjectList();
	Map<String, Object> getMap();
	byte getByte();
	short getShort();
	int getInt();
	float getFloat();
	long getLong();
	double getDouble();
	boolean getBoolean();
	String getString();
}
