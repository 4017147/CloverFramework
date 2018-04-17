package com.cloverframework.core.util;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.factory.EntityFactory;
import com.cloverframework.core.util.interfaces.IArgsMatcher;

public class ArgsFilter {
	/**
	 *过滤合法的参数，返回合法的领域实体或实体类或枚举，否则返回null
	 * @param e
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object filter(Object e,DomainService domainService,IArgsMatcher pattern) {
		Class entityClass = null;
		Class domainClass = domainService.getClass();
		if(e.getClass()==Class.class && EntityFactory.isMatchDomain((Class)e, domainClass)) {
			return e;
		}else {
			entityClass = e.getClass();
		}
		if(entityClass.isEnum())
			return e;
		if(EntityFactory.isMatchDomain(entityClass, domainClass)||pattern.isMatch(e)) 
			return e;	
		return null;
	}
}
