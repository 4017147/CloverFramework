package com.clover.core.repository;

import com.clover.core.course.Action;
import com.clover.core.course.Course;
import com.clover.core.course.CourseProxy;
import com.domain.DomainService;
/**
 * һ��ͨ�õĲִ���������Course�;���ִ�
 * @author yl
 *
 */
public class GeneralRepository extends AbstractRepository{
	//����ʹ��IOC����ע��
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
		
		
		/*====================��CRUDģʽģ�巽��================== */
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
