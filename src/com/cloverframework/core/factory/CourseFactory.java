package com.cloverframework.core.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cloverframework.core.dsl.Course.AND;
import com.cloverframework.core.dsl.Course.Add;
import com.cloverframework.core.dsl.Course.Aggregate;
import com.cloverframework.core.dsl.Course.By;
import com.cloverframework.core.dsl.Course.Condition;
import com.cloverframework.core.dsl.Course.Count;
import com.cloverframework.core.dsl.Course.Get;
import com.cloverframework.core.dsl.Course.GroupBy;
import com.cloverframework.core.dsl.Course.NOT;
import com.cloverframework.core.dsl.Course.OR;
import com.cloverframework.core.dsl.Course.Put;
import com.cloverframework.core.dsl.Course.Remove;
import com.cloverframework.core.util.interfaces.CourseType;
import com.cloverframework.core.util.lambda.CreateMain;

public class CourseFactory implements CourseType{
	
	/**
	 * 创建根节点，要求根节点有默认构造方法
	 * @param c
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <C> C create(Class<C> c) throws InstantiationException, IllegalAccessException {
		return c.newInstance();
	}
	
	private static Map<String,CreateMain<?>> constructors = new HashMap<>();
		
		{
			constructors.put(get, Get::new);
			constructors.put(add, Add::new);
			constructors.put(put, Put::new);
			constructors.put(remove, Remove::new);
			constructors.put(con, Condition::new);
			constructors.put(by, By::new);
			constructors.put(and, AND::new);
			constructors.put(or, OR::new);
			constructors.put(not, NOT::new);
			constructors.put(groupBy, GroupBy::new);
			constructors.put(agg, Aggregate::new);
			constructors.put(count, Count::new);
			
			constructors = Collections.unmodifiableMap(constructors);
		}
	
		/**
		 * 根据给定的节点类型返回对应的节点构造方法函数引用
		 * @param type
		 * @return
		 */
		public static CreateMain<?> getConstructor(String type) {
			CreateMain<?> cm = constructors.get(type);
			if(cm==null)
				return getExtendsConstructor(type);
			return cm;
		}
	
	
	static CreateMain<?> getExtendsConstructor(String type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
