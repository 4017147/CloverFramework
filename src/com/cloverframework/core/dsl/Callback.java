package com.cloverframework.core.dsl;

import com.cloverframework.core.dsl.interfaces.Accessable;
import com.cloverframework.core.dsl.interfaces.Constant;

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
	@SuppressWarnings("unchecked")
	default public void END() {
		AbstractCourse c = getThis();
		if(c.getStatus()>END) {
			c.proxy.receive(c, END);			
		}
	}
	
	@SuppressWarnings("unchecked")
	default public void LOCK() {
		AbstractCourse c = getThis();
		if(c.getStatus()>LOCKED) {
			c.proxy.receive(c, LOCKED);			
		}
	}
	
	@SuppressWarnings("unchecked")
	default public void UNLOCK() {
		AbstractCourse c = getThis();
		if(c.getStatus()<UNLOCKED) {
			c.proxy.receive(c, UNLOCKED);			
		}
	}
	
	default public Object execute() {
		LOCK();
		return getThis().proxy.execute();
	}
	
	default public Object executeFuture() {
		LOCK();
		return getThis().proxy.executeFuture();
	}
	
	
	default public int commit() {
		LOCK();
		return getThis().proxy.commit();
	}
	
	default public int commitFuture() {
		LOCK();
		return getThis().proxy.commitFuture();
	}
	
}
