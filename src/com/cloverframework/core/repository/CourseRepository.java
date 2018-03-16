package com.clover.core.repository;

import com.clover.core.course.Action;
import com.clover.core.course.Course;
import com.clover.core.course.CourseProxy;

/**
 * һ�����Course�����ύ�Ĳִ����󲿷�ҵ����course����Ĳ���������ʹ�øòִ�
* @author yl  
* 
*    
*/
public class CourseRepository extends AbstractRepository{
	//����ʹ��IOC����ע��
	private ICourseMode mode;
	public void setMode(ICourseMode mode) {
		this.mode = mode;
	}

	public final int fromProxy(CourseProxy proxy) {
		return super.fromProxy(proxy, mode);
	}
	
	public final int fromAction(Action<?> action) {
		return super.fromAction(action, mode);
	}
	
	public <T> T query(Course course) {
		return super.query(course, mode);
	}

	public int commit(Course course) {
		return super.commit(course, mode);
	}

}
