package com.cloverframework.core.domain;

import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.repository.CourseRepository;
import com.cloverframework.core.repository.interfaces.ICourseMode;
import com.dao.UserDao;
import com.entity.User;

public class DomainAction<T> extends Action<T,Course> implements DomainActionService{
	
	public DomainAction() {
		domainService = (DomainService) this;
	}
	
}
