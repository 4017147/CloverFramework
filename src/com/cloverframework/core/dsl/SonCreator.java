package com.cloverframework.core.dsl;

import com.cloverframework.core.dsl.Course.Condition;
import com.cloverframework.core.dsl.Course.Count;
import com.cloverframework.core.util.lambda.CreateSon;

/**
 * 创建course的子节点默认方法接口，在proxy中通过继承该接口实现方法调用创建子节点。
 * 不可继承其他接口，避免菱形继承复杂判断。
 * @author yl
 *
 */
public interface SonCreator{
	public AbstractCourse getCurrCourse();
	
	
	/**
	 * 创建子节点通用函数
	 * @param function
	 * @return
	 */
	default <R> R create(CreateSon<R> constructor,boolean isSon, Object ...obj) {
		AbstractCourse course = getCurrCourse();
		AbstractCourse last = null;
		//搜索最后主干节点
		while(course!=null) {
			last = course;
			course = course.next;
		}
		return constructor.apply(last, isSon, obj);
	}

	/**
	 * 创建condition类型子节点
	 * @param obj
	 * @return
	 */
	default Condition $(Object...obj){
		//TODO 当操作类型eq中作为子节点如何处置其中的entity等信息
		return create(Condition::new,true,obj);
	}
	
	/**
	 * 创建count聚合函数
	 * @param obj
	 * @return
	 */
	default Count count(Object obj) {
		return create(Count::new,true,obj);
	}
}
