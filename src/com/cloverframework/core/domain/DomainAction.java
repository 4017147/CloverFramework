package com.cloverframework.core.domain;

import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.Course;

public class DomainAction<T> extends Action<T,Course> implements DomainActionService{
	
	public DomainAction() {
		domainService = (DomainService) this;
	}
	
}
