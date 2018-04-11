package com.cloverframework.core.util;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.factory.EntityFactory;
import com.cloverframework.core.util.interfaces.IArgsMatcher;

public class ArgsFilter {
	/**
	 *过滤合法的参数
	 * @param e
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static boolean filter(Object e,DomainService domainService,IArgsMatcher pattern) {
		boolean classMatch = false;
		Class entityClass = null;
		Class domainClass = domainService.getClass();
		if(e.getClass()==Class.class) {
			classMatch = EntityFactory.isMatchDomain((Class)e, domainClass);
		}else {
			entityClass = e.getClass();
		}
		if(!classMatch) {
			if(EntityFactory.isMatchDomain(entityClass, domainClass)||
				entityClass.isEnum()||pattern.isMatch(e)) {
			classMatch = true;
			}
		}	
		return classMatch;
	}
}
