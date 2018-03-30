package com.cloverframework.core.repository;

import com.cloverframework.core.course.Action;
import com.cloverframework.core.course.Course;
import com.cloverframework.core.course.CourseProxy;
import com.cloverframework.core.domain.DomainService;
/**
 * 一个通用的仓储，整合了Course和经典仓储
 * @author yl
 *
 */
public class GeneralRepository<T> extends AbstractRepository<T>{
	//建议使用IOC容器注入
		private ICourseMode courseMode;
		private IClassicalMode baseMode;
		
		
		public void setCourseMode(ICourseMode courseMode) {
			this.courseMode = courseMode;
		}

		public void setBaseMode(IClassicalMode baseMode) {
			this.baseMode = baseMode;
		}

		public final int fromProxy(CourseProxy proxy) {
			return super.fromProxy(proxy, courseMode);
		}
		
		public final int fromAction(Action<?> action) {
			return super.fromAction(action, courseMode);
		}
		
		public Object query(Course course) {
			return super.query(course, courseMode);
		}
		
		public Integer commit(Course course) {
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
