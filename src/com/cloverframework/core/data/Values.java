package com.cloverframework.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.cloverframework.core.data.interfaces.CourseValues;
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
	/**领域实体集合*/
	private  List<Object> entities;
	
	/**
	 * 如果参数值全部和字段对应关系，则不会有领域实体集合，
	 * 否则只要有一个实体跟字段所属实体对应，则放弃前者取后者.
	 * @param types
	 * @param fields
	 * @param val
	 * @throws ArgsCountNotMatch
	 */
	public Values(Set<String> types,List<String> fields,Object... val) throws ArgsCountNotMatch {
		objects = new ArrayList<>();
		entities = new ArrayList<>();
		List<Object> list = Arrays.asList(val);
		//优先匹配entity类型参数值
		for(Object o:list) {
			for(String s:types) {
				if(o!=null && o.getClass().getSimpleName().equals(s)) {
					//TODO 类型校验
					entities.add(o);
				}
			}
		}
		if(entities.size()<=0) {
			if(val.length!=fields.size()&& val.length!=1)
				//参数值个数只能为1个或者和字段个数相同
				throw new ArgsCountNotMatch(fields.size(), val.length);
			objects.addAll(list);
		}
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
	public List<Object> entityList() {
		return entities;
	}

	
	
	


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
		String ent = entities==null?"null":entities.toString();
		
		String[] values = {vbo,vby,vin,vlo,vfl,vdo,vst,obj,ent};
		for(String value:values) {
			if(!value.equals("null"))
				return value;	
		}
		return null;
	}
	
}
