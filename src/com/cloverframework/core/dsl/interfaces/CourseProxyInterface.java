package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.SonCreator;
import com.cloverframework.core.repository.CourseRepository;
import com.cloverframework.core.util.interfaces.ELType;

public interface CourseProxyInterface<T,C extends AbstractCourse> extends SonCreator,ELType{
	/**
	 * proxy和service必需是一对一的关系
	 * @return
	 */
	DomainService getDomainService();
	
	void setDomainService(DomainService domainService);
	
	C receive(C c,int option);
	
	void setRepository(CourseRepository<T,C> repository);
	
	C initCourse(String id,C course,CourseProxyInterface<T,C> proxy,int status);
	
	T execute();
	
	T execute(C course);
	
	T executeFuture();
	
	Object execute(String id);
	
	int commit();
	
	int commit(C course);
	
	int commitFuture();
	
}
