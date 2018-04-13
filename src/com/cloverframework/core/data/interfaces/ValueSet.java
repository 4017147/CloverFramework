package com.cloverframework.core.data.interfaces;

/**
 * 该接口用于设置Course的基本数据类型values
 * @author yl
 *
 * @param <A>
 */
public interface ValueSet<A> {
	A setBoolean(boolean...val);
	  A setByte(byte...val);
	  A v_short(short...val);
	  A v_int(int...val);
	  A v_long(long...val);
	  A v_float(float...val);
	  A v_double(double...val);
	  A v_String(String...val);
}
