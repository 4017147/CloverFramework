package com.cloverframework.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cloverframework.core.data.interfaces.Values;
import com.cloverframework.core.exceptions.ArgsCountNotMatch;

public final class CourseValues implements Values{
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
	 * 该方法用于tojson或者其他组件toString，输出优先级规必需跟实际setValue取值一致
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
	 * @param val
	 * @throws ArgsCountNotMatch
	 */
	public CourseValues(Object... val) throws ArgsCountNotMatch {
		objects = new ArrayList<>();
		objects.addAll(Arrays.asList(val));			
	}


	
	
	public  CourseValues(boolean... val) {
		super();	
		v_boolean = new boolean[val.length];
		System.arraycopy(val, 0, v_boolean, 0, val.length);
	}
	
	public  CourseValues(byte... val) {
		super();	
		v_byte = new byte[val.length];
		System.arraycopy(val, 0, v_byte, 0, val.length);
	}
	
	public  CourseValues(short... val) {
		super();	
		v_short = new short[val.length];
		System.arraycopy(val, 0, v_short, 0, val.length);
	}
	

	public  CourseValues(int... val) {
		super();	
		v_int = new int[val.length];
		System.arraycopy(val, 0, v_int, 0, val.length);
	}
	
	public  CourseValues(long... val) {
		super();	
		v_long = new long[val.length];
		System.arraycopy(val, 0, v_long, 0, val.length);
	}
	
	public  CourseValues(float... val) {
		super();	
		v_float = new float[val.length];
		System.arraycopy(val, 0, v_float, 0, val.length);
	}
	
	public  CourseValues(double... val) {
		super();	
		v_double = new double[val.length];
		System.arraycopy(val, 0, v_double, 0, val.length);
	}
	
	public  CourseValues(String... val) {
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
