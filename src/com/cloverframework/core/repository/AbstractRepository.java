package com.cloverframework.core.repository;

import java.util.List;
import java.util.Map;

import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.data.interfaces.Wrapper;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.dsl.CourseWrapper;
import com.cloverframework.core.exceptions.CourseTypeException;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.repository.interfaces.ClassicalMode;
import com.cloverframework.core.repository.interfaces.CourseMode;
import com.cloverframework.core.util.interfaces.CourseType;
/**
 * 这是一个抽象的仓储，其子类面向领域服务层，由子类负责仓储接口的实际调用。
 * @author yl
 *
 */
public abstract class AbstractRepository<T,C extends AbstractCourse>{
	private final T doQuery(C course,CourseMode<T> mode) {
		Wrapper wrapper = new CourseWrapper(course);
		String type = wrapper.next().type();
		if (type == CourseType.get) {
			return mode.query(new DataSwaper<T>(course));
		}else {
			throw ExceptionFactory.wrapException("Type of course id:"+course.getId()+" should be "+CourseType.get, new CourseTypeException(type));
		}
	}
	
	private final int doUpdate(C course,CourseMode<T> mode) {
		Wrapper wrapper = new CourseWrapper(course);
		String type = wrapper.next().type();
		if (type != CourseType.get) {
			return(mode.update(new DataSwaper<T>(wrapper)));
		}else {
			throw ExceptionFactory.wrapException("Type of course id:"+course.getId()+" should not be "+CourseType.get, new CourseTypeException(type));
		}
	}
	
	protected final Result<T> doResult(C course,CourseMode<T> mode){
		return mode.result(new DataSwaper<T>(new CourseWrapper(course)));
	}
	

	public final int fromProxy(CourseProxy<T,C> proxy,CourseMode<T> mode) {
		if(!(proxy instanceof Action))
			return 0;
		Map<String,C> map = proxy.getShareSpace();
		for(String key:map.keySet()) {
			C course = map.get(key);
			if(course.getNextType()==CourseType.get)
				doQuery(course,mode);
			else
				doUpdate(course,mode);
		}
		return 1;
	}
	
	public final int fromAction(Action<T,C> action,CourseMode<T> mode) {
		List<C> list = action.getWorkSpace();
		for(C course:list) {
			if(course.getNextType()==CourseType.get)
				doQuery(course,mode);
			else
				doUpdate(course,mode);
		}
		return 1;
	}
	
	public final T query(C course,CourseMode<T> mode) {
		return doQuery(course,mode);
	}
	public final int commit(C course,CourseMode<T> mode) {
		return doUpdate(course,mode);
	}
	
	/*====================简单CRUD模式模板方法================== */
	
	
	public final <E> E get(Class<E> Class,Integer key,ClassicalMode mode,DomainService service) {
		return mode.get(Class,key);			
	}
	public final <E> int add(E entity,ClassicalMode mode,DomainService service) {
		return mode.add(entity);
	}
	public final <E> int put(E entity,ClassicalMode mode,DomainService service) {
		return mode.put(entity);
	}
	public final <E> int remove(Class<E> Class,Integer key,ClassicalMode mode,DomainService service) {
		return mode.remove(Class,key);
	}
	
}
