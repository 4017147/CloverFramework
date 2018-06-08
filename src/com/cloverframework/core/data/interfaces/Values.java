package com.cloverframework.core.data.interfaces;

import java.util.List;

/**
 * 对Course的节点值参数封装实现值的获取方法
 * @author yl
 *
 */
public interface Values {
	boolean[] getBoolean();
	byte[] getByte();
	short[] getShort();
	int[] getInt();
	long[] getLong();
	float[] getFloat();
	double[] getDouble();
	String[] getString();
	List<Object> objectList();
	
}
