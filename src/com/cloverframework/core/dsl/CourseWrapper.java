package com.cloverframework.core.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.data.interfaces.Values;
import com.cloverframework.core.data.interfaces.Wrapper;

/**
 * 此类对course进行了包装，框架之外可以通过该类对course进行合法访问
 * @author yl
 *
 */
@SuppressWarnings("rawtypes")
public class CourseWrapper implements Wrapper{
	
	private AbstractCourse course;
	private AbstractCourse head;
	
	public CourseWrapper(AbstractCourse course) {
		super();
		this.course = course;
		this.head = course;
	}

	@Override
	public String id() {
		return head.getId();
	}

	@Override
	public String type() {
		return course.getType();
	}

	@Override
	public String opType() {
		return course.getOptype();
	}

	@Override
	public Wrapper previous() {
		return new CourseWrapper(course.previous);
	}

	@Override
	public Wrapper next() {
		return new CourseWrapper(course.next);
	}

	@Override
	public Wrapper parent() {
		return new CourseWrapper(course.parent);
	}

	@Override
	public List<Wrapper> sons() {
		List<Wrapper> list = new ArrayList<Wrapper>();
		for(AbstractCourse c:(List<AbstractCourse>)course.sons) {
			list.add(new CourseWrapper(c));
		}
		return list;
	}

	@Override
	public List<String> fields() {
		return course.fields;
	}

	@Override
	public Set<String> types() {
		return course.types;
	}

	@Override
	public List<Object> entities() {
		return course.entities;
	}

	@Override
	public Values value() {
		return course.getValues();
	}

	@Override
	public String json() {
		return course.getJsonString();
	}

	@Override
	public boolean hasNext() {
		return course.next==null?false:true;
	}

	/**
	 * 设置course的result为当前result，返回原来的result
	 */
	@Override
	public Result getResult() {
		return head.getResult();
	}

	@Override
	public void setResult(Result result) {
		head.setResult(result);
	}





	
	

}
