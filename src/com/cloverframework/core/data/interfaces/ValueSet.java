package com.cloverframework.core.data.interfaces;

import com.cloverframework.core.dsl.AbstractCourse;

/**
 * 设置Course的基本数据类型参数values的值
 * @author yl
 *
 * @param <A>
 */
public interface ValueSet<A> {
	AbstractCourse<A> setBoolean(boolean...val);
	AbstractCourse<A> setByte(byte...val);
	AbstractCourse<A> setShort(short...val);
	AbstractCourse<A> setInt(int...val);
	AbstractCourse<A> setLong(long...val);
	AbstractCourse<A> setFloat(float...val);
	AbstractCourse<A> setDouble(double...val);
	AbstractCourse<A> setString(String...val);
}
