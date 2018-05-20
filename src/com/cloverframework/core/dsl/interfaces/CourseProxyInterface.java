package com.cloverframework.core.dsl.interfaces;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.repository.CourseRepository;

public interface CourseProxyInterface<T,C extends AbstractCourse> {
	/**
	 * proxy和service必需是一对一的关系
	 * @return
	 */
	DomainService getDomainService();
	
	void setDomainService(DomainService domainService);
	
	void END();
	
	void setRepository(CourseRepository<T,C> repository);
	
	C initCourse(String id,C course,CourseProxyInterface<T,C> proxy,byte status);
	
	T executeOne(C course);
	
	Object execute(String type);
	
	int commit(C course);
	
}
