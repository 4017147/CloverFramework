package com.cloverframework.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.exception.ArgsCountNotMatch;

public final class Values implements CourseValues{
	private  boolean[] v_boolean;
	private  byte[] v_byte;
	private  short[] v_short;
	private  int[] v_int;
	private  long[] v_long;
	private  float[] v_float;
	private  double[] v_double;
	private  String[] v_String;
	/**参数集合*/
	private  List<Object> objects;
	
	/**
	 * 该方法用于tojson或者其他组件toString，输出优先级规则必需跟实际setValue取值一致
	 */
	@Override
	public String toString() {
		String vbo = Arrays.toString(v_boolean);
		String vby = Arrays.toString(v_byte);
		String vin = Arrays.toString(v_int);
		String vlo = Arrays.toString(v_long);
		String vfl = Arrays.toString(v_float);
		String vdo = Arrays.toString(v_double);
		String vst = Arrays.toString(v_String);
		String obj = objects==null?"null":objects.toString();
		
		String[] values = {vbo,vby,vin,vlo,vfl,vdo,vst,obj};
		for(String value:values) {
			if(!value.equals("null"))
				return value;	
		}
		return null;
	}




	/**
	 * 
	 * 检查当前节点字段参数跟值参数个数，值参数只能为1或者与之相等，否则抛出异常
	 * @param types
	 * @param fields
	 * @param val
	 * @throws ArgsCountNotMatch
	 */
	public Values(int fieldsSize,Object... val) throws ArgsCountNotMatch {
		if(val.length!=1 && val.length!= fieldsSize)
			throw new ArgsCountNotMatch(fieldsSize, val.length);
		List<Object> list = Arrays.asList(val);
		byte n = 0;
		for(Object o:list) {
			if(o instanceof AbstractCourse) {
				n++;
				int size = ((AbstractCourse)o).getElements().length;
				System.out.println(size);
				if(size!=1 && size+val.length-n!=fieldsSize) {
					//参数值个数只能为1个或者和字段个数相同
					throw new ArgsCountNotMatch(fieldsSize, size+val.length-n);
				}
			}
		}
		objects = new ArrayList<>();
		objects.addAll(list);			
	}


	
	
	public  Values(boolean... val) {
		super();	
		v_boolean = new boolean[val.length];
		System.arraycopy(val, 0, v_boolean, 0, val.length);
	}
	
	public  Values(byte... val) {
		super();	
		v_byte = new byte[val.length];
		System.arraycopy(val, 0, v_byte, 0, val.length);
	}
	
	public  Values(short... val) {
		super();	
		v_short = new short[val.length];
		System.arraycopy(val, 0, v_short, 0, val.length);
	}
	

	public  Values(int... val) {
		super();	
		v_int = new int[val.length];
		System.arraycopy(val, 0, v_int, 0, val.length);
	}
	
	public  Values(long... val) {
		super();	
		v_long = new long[val.length];
		System.arraycopy(val, 0, v_long, 0, val.length);
	}
	
	public  Values(float... val) {
		super();	
		v_float = new float[val.length];
		System.arraycopy(val, 0, v_float, 0, val.length);
	}
	
	public  Values(double... val) {
		super();	
		v_double = new double[val.length];
		System.arraycopy(val, 0, v_double, 0, val.length);
	}
	
	public  Values(String... val) {
		super();	
		v_String = new String[val.length];
		System.arraycopy(val, 0, v_String, 0, val.length);
	}
	
	@Override
	public List<Object> objectList() {
		return objects;
	}

	@Override
	public boolean[] getBoolean() {
		return this.v_boolean;
	}




	@Override
	public byte[] getByte() {
		return this.v_byte;
	}




	@Override
	public short[] getShort() {
		return this.v_short;
	}




	@Override
	public int[] getInt() {
		return this.v_int;
	}




	@Override
	public long[] getLong() {
		return this.v_long;
	}




	@Override
	public float[] getFloat() {
		return this.v_float;
	}




	@Override
	public double[] getDouble() {
		return this.v_double;
	}




	@Override
	public String[] getString() {
		return this.v_String;
	}
	
}
