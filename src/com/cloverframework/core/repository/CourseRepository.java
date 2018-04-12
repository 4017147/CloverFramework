package com.cloverframework.core.repository;

import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.repository.interfaces.ICourseMode;

/**
 * 一个针对Course工具提交的仓储，大部分业务已course定义的操作都可以使用该仓储
* @author yl  
* 
*    
*/
public class CourseRepository<T,C extends AbstractCourse> extends AbstractRepository<T,C>{
	//建议使用IOC容器注入
	private ICourseMode<T> mode;
	public void setMode(ICourseMode<T> mode) {
		this.mode = mode;
	}

	public final int fromProxy(CourseProxy<T,C> proxy) {
		return super.fromProxy(proxy, mode);
	}
	
	public final int fromAction(Action<T,C> action) {
		return super.fromAction(action, mode);
	}
	
	public T query(C course) {
		return super.query(course, mode);
	}

	public int commit(C course) {
		return super.commit(course, mode);
	}

}
