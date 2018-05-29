package com.cloverframework.core.data.interfaces;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 设置Course的基本数据类型参数values的值
 * @author yl
 *
 * @param <A>
 */
public interface ValueSet {
	AbstractCourse setBoolean(boolean...val);
	AbstractCourse setByte(byte...val);
	AbstractCourse setShort(short...val);
	AbstractCourse setInt(int...val);
	AbstractCourse setLong(long...val);
	AbstractCourse setFloat(float...val);
	AbstractCourse setDouble(double...val);
	AbstractCourse setString(String...val);
}
