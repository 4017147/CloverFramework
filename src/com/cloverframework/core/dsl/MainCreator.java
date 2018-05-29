package com.cloverframework.core.dsl;

import com.cloverframework.core.exceptions.CourseIsClosed;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.util.lambda.CreateMain;

public interface MainCreator {
	/**
	 * 通过输入的节点创建函数表达式执行节点创建，如果节点已存在并且认为和当前创建节点等价，
	 * 则不会重复创建,并返回原有节点
	 * @param <R>
	 */
	default <R> R create(AbstractCourse old,CreateMain<R> constructor,AbstractCourse previous,Object ...obj) {
		if(previous.getStatus()<AbstractCourse.WAIT)
			try {
				throw new CourseIsClosed(previous.getType());	
			} catch (CourseIsClosed e) {
				throw ExceptionFactory.wrapException("Course create error,id:"+previous.getId(), e);	
			}
		if(old!=null) {
			int[] oldArgsHash = old.getArgsHash();
			if(oldArgsHash!=null && oldArgsHash.length==obj.length) {
				for(int i = 0;i<oldArgsHash.length;i++) {
					if(oldArgsHash[i]!=(obj[i]==null?0:obj[i].hashCode())) {
						return constructor.apply(previous,obj);
					}
				}
				return (R) old;
			}
		}
		return constructor.apply(previous, obj);
	}
	
	
}
