package com.clover.core.repository;

import java.util.List;
import java.util.Map;

import com.clover.core.course.Action;
import com.clover.core.course.Course;
import com.clover.core.course.CourseProxy;
import com.clover.core.course.Course.CourseType;
import com.clover.core.factory.EntityFactory;
import com.domain.DomainService;
/**
 * ����һ������Ĳִ��������������������㣬�����ฺ��ִ��ӿڵ�ʵ�ʵ��á�
 * @author yl
 *
 */
public abstract class AbstractRepository{
	
	private EntityFactory entityFactory = EntityFactory.getInstance();
	
	private final <T> T doGet(Course course,ICourseMode mode) {
		Enum<CourseType> type = course.getType();
		if (type == CourseType.GET) {
			return mode.get(course);
		}
		return null;
	}
	
	private final int doOther(Course course,ICourseMode mode) {
		Enum<CourseType> type = course.getType();
		if (type == CourseType.ADD) {
			return(mode.add(course));
		}
		if (type == CourseType.PUT) {
			return(mode.put(course));
		}
		if (type == CourseType.REMOVE) {
			return(mode.remove(course));
		}
		return 0;
	}
	

	public final int fromProxy(CourseProxy proxy,ICourseMode mode) {
		if(proxy instanceof Action)
			return 0;
		Map<String,Course> map = proxy.getEden();
		for(String key:map.keySet()) {
			Course course = map.get(key);
			if(course.getType()==CourseType.GET)
				doGet(course,mode);
			else
				doOther(course,mode);
		}
		return 1;
	}
	
	public final int fromAction(Action<?> action,ICourseMode mode) {
		List<Course> list = Action.getWork();
		for(Course course:list) {
			if(course.getType()==CourseType.GET)
				doGet(course,mode);
			else
				doOther(course,mode);
		}
		return 1;
	}
	
	public final <T> T query(Course course,ICourseMode mode) {
		return doGet(course,mode);
	}
	public final int commit(Course course,ICourseMode mode) {
		return doOther(course,mode);
	}
	
	/*====================��CRUDģʽģ�巽��================== */
	
	
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
