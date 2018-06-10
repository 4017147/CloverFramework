package com.cloverframework.core.repository;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.dsl.AbstractCourse;
import com.cloverframework.core.dsl.Action;
import com.cloverframework.core.dsl.CourseProxy;
import com.cloverframework.core.repository.interfaces.ClassicalMode;
import com.cloverframework.core.repository.interfaces.CourseMode;
/**
 * 一个通用的仓储，整合了Course和经典仓储
 * @author yl
 *
 */
public class GeneralRepository<T,C extends AbstractCourse<C>> extends AbstractRepository<T,C>{
	//建议使用IOC容器注入
		private CourseMode<T> courseMode;
		private ClassicalMode baseMode;
		
		
		public void setCourseMode(CourseMode<T> courseMode) {
			this.courseMode = courseMode;
		}

		public void setBaseMode(ClassicalMode baseMode) {
			this.baseMode = baseMode;
		}

		public final int fromProxy(CourseProxy<T,C> proxy) {
			return super.fromProxy(proxy, courseMode);
		}
		
		public final int fromAction(Action<T,C> action) {
			return super.fromAction(action, courseMode);
		}
		
		public Object query(C course) {
			return super.query(course, courseMode);
		}
		
		public Integer commit(C course) {
			return super.commit(course, courseMode);
		}
		
		
		/*====================简单CRUD模式模板方法================== */
		public <E> E get(Class<E> Class,Integer key,DomainService service) {
			return super.get(Class,key,baseMode, service);
		}
		public <E> int add(E entity,DomainService service) {
			return super.add(entity, baseMode,service);
		}
		public <E> int put(E entity,DomainService service) {
			return super.put(entity, baseMode,service);
		}
		public <E> int remove(Class<E> Class,Integer key,DomainService service) {
			return super.remove(Class,key, baseMode,service);
		}
}
