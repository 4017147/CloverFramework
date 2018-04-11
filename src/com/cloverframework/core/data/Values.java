package com.cloverframework.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.exception.ArgsCountNotMatch;

public final class Values implements CourseValues{
	private final List<Object> objects;
	private final List<Object> entities;
	
	
	public Values(Set<String> types,List<String> fields,Object...val) throws ArgsCountNotMatch {
		//两个集合互斥，如果参数值全部和字段对应关系，则不会有实体集合，
		//否则只要有一个实体跟字段所属实体对应，则放弃前者取后者
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


	@Override
	public List<Object> objectList() {
		return objects;
	}


	@Override
	public List<Object> entityList() {
		return entities;
	}
	
	
	
}
