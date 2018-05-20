package com.cloverframework.core.repository;

import java.util.List;
import java.util.Map;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.repository.interfaces.IClassicalMode;
import com.cloverframework.core.repository.interfaces.ICourseMode;
import com.cloverframework.core.util.interfaces.CourseType;
/**
 * 这是一个抽象的仓储，其子类面向领域服务层，由子类负责仓储接口的实际调用。
 * @author yl
 *
 */
public abstract class AbstractRepository<T,C extends AbstractCourse>{
	private final T doGet(C course,ICourseMode<T> mode) {
		String type = course.getSubType();
		if (type == CourseType.get) {
			return mode.get(new DataSwaper<T>(course));
		}
		return null;
	}
	
	private final int doOther(C course,ICourseMode<T> mode) {
		String type = course.getType();
		if (type == CourseType.add) {
			return(mode.add(new DataSwaper<T>(course)));
		}
		if (type == CourseType.put) {
			return(mode.put(new DataSwaper<T>(course)));
		}
		if (type == CourseType.remove) {
			return(mode.remove(new DataSwaper<T>(course)));
		}
		return 0;
	}
	

	public final int fromProxy(CourseProxy<T,C> proxy,ICourseMode<T> mode) {
		if(!(proxy instanceof Action))
			return 0;
		Map<String,C> map = proxy.getShareSpace();
		for(String key:map.keySet()) {
			C course = map.get(key);
			if(course.getType()==CourseType.get)
				doGet(course,mode);
			else
				doOther(course,mode);
		}
		return 1;
	}
	
	public final int fromAction(Action<T,C> action,ICourseMode<T> mode) {
		List<C> list = action.getWorkSpace();
		for(C course:list) {
			if(course.getType()==CourseType.get)
				doGet(course,mode);
			else
				doOther(course,mode);
		}
		return 1;
	}
	
	public final T query(C course,ICourseMode<T> mode) {
		return doGet(course,mode);
	}
	public final int commit(C course,ICourseMode<T> mode) {
		return doOther(course,mode);
	}
	
	/*====================简单CRUD模式模板方法================== */
	
	
	public final <E> E get(Class<E> Class,Integer key,IClassicalMode mode,DomainService service) {
		return mode.get(Class,key);			
	}
	public final <E> int add(E entity,IClassicalMode mode,DomainService service) {
		return mode.add(entity);
	}
	public final <E> int put(E entity,IClassicalMode mode,DomainService service) {
		return mode.put(entity);
	}
	public final <E> int remove(Class<E> Class,Integer key,IClassicalMode mode,DomainService service) {
		return mode.remove(Class,key);
	}
	
}
