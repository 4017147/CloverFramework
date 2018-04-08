package com.cloverframework.core.domain;

import com.cloverframework.core.dsl.Action;

public class ActionDomain<T> extends Action<T> implements DomainActionService{
	
	public ActionDomain() {
		domainService = (DomainService) this;
	}
	
}
