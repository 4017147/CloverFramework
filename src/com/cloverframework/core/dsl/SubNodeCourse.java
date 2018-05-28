package com.cloverframework.core.dsl;

import java.util.function.Function;

import com.cloverframework.core.dsl.Course.Condition;
import com.cloverframework.core.dsl.Course.Count;
import com.cloverframework.core.util.interfaces.CourseType;

/**
 * 创建course的子节点默认方法接口，在proxy中通过继承该接口实现方法调用创建子节点。
 * 不可继承其他接口，避免菱形继承复杂判断。
 * @author yl
 *
 */
public interface SubNodeCourse{
	public AbstractCourse getCurrCourse();
	
	
	/**
	 * 创建子节点通用函数
	 * @param function
	 * @return
	 */
	default <R> R createNode(Function<AbstractCourse,R> function) {
		AbstractCourse course = getCurrCourse();
		AbstractCourse last = null;
		//搜索最后主干节点
		while(course!=null) {
			last = course;
			course = course.next;
		}
		R r = function.apply(last);
		return r;
	}

	/**
	 * 创建condition类型子节点
	 * @param obj
	 * @return
	 */
	default Condition $(Object...obj){
		//TODO 当操作类型eq中作为子节点如何处置其中的entity等信息
		return createNode((last)->new Condition(last,CourseType.by,true,obj));
	}
	
	/**
	 * 创建count聚合函数
	 * @param obj
	 * @return
	 */
	default Count count(Object obj) {
		return createNode((last)->new Count(last,CourseType.count,true,obj));
	}
}
