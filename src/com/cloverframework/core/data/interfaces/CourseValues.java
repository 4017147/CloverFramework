package com.cloverframework.core.data.interfaces;

import java.util.List;

/**
 * 要求对值参数分装实现值的获取方法
 * @author yl
 *
 */
public interface CourseValues {
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
