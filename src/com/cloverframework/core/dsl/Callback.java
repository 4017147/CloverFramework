package com.cloverframework.core.dsl;

import com.cloverframework.core.dsl.interfaces.Accessable;
import com.cloverframework.core.dsl.interfaces.Constant;
import com.cloverframework.core.factory.EntityFactory;

/**
 * 回调proxy接口
 * @author yl
 *
 */
public interface Callback extends Accessable,Constant{
	
	/**
	 * 结束当前的一条course语句，则该course不可再添加语句，
	 * 并且执行end方法在大多情况下都是必须的，如果没有正常的执行end，
	 * 会导致当前定义的course被下一次操作快速抛弃而不会进行缓存
	 */
	default public void END() {
		AbstractCourse c = getThis();
		try {
			if(c.getStatus()!=END) {
				c.setStatus(END);
				if (c.previous!=null) 
					c.previous.END(); 
				else {
					c.proxy.receive(c,END);
					EntityFactory.removeCourse(Thread.currentThread().getId());				
				}				
			}
		}
		finally {
			if(c.getStatus()!=END) {
				c.proxy = null;
				c.literal = null;
				c.literal_te = null;
				EntityFactory.removeCourse(Thread.currentThread().getId());
			}
		}
	}
	
	default public void LOCK() {
		AbstractCourse c = getThis();
		try {
			if(c.getStatus()!=LOCKED) {
				c.setStatus(LOCKED);
				if (c.previous!=null) 
					c.previous.LOCK(); 
				else {
					c.proxy.receive(c,LOCKED);
					EntityFactory.removeCourse(Thread.currentThread().getId());				
				}				
			}
		}
		finally {
			if(c.getStatus()!=LOCKED) {
				c.proxy = null;
				c.literal = null;
				c.literal_te = null;
				EntityFactory.removeCourse(Thread.currentThread().getId());
			}
		}
		
	}
	
	
	/**
	 * 直接END()并执行当前对象course语句
	 * @return
	 */

	default public Object execute() {
		END();
		return getThis().proxy.execute();
	}
	
	default public Object executeFuture() {
		END();
		return getThis().proxy.executeFuture();
	}
	
	
	default public int commit() {
		END();
		return getThis().proxy.commit();
	}
	
	default public int commitFuture() {
		END();
		return getThis().proxy.commitFuture();
	}
	
}
