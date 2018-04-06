package com.cloverframework.core.repository;

import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.CourseProxy;

/**
 * 一个针对Course工具提交的仓储，大部分业务已course定义的操作都可以使用该仓储
* @author yl  
* 
*    
*/
public class CourseRepository<T> extends AbstractRepository<T>{
	//建议使用IOC容器注入
	private ICourseMode<T> mode;
	public void setMode(ICourseMode<T> mode) {
		this.mode = mode;
	}

	public final int fromProxy(CourseProxy<T> proxy) {
		return super.fromProxy(proxy, mode);
	}
	
	public final int fromAction(Action<?> action) {
		return super.fromAction(action, mode);
	}
	
	public T query(Course course) {
		return super.query(course, mode);
	}

	public int commit(Course course) {
		return super.commit(course, mode);
	}

}
