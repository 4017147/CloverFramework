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
	 * 
	 */
	@SuppressWarnings("unchecked")
	default public void END() {
		AbstractCourse c = getThis();
		if(c.getStatus()>END) {
			c.proxy.receive(c, END);			
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	default public void LOCK() {
		AbstractCourse c = getThis();
		if(c.getStatus()>LOCKED) {
			c.proxy.receive(c, LOCKED);			
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	default public void UNLOCK() {
		AbstractCourse c = getThis();
		if(c.getStatus()<UNLOCKED) {
			c.proxy.receive(c, UNLOCKED);			
		}
	}
	
	@SuppressWarnings("unchecked")
	default public void READY() {
		AbstractCourse c = getThis();
		if(c.getStatus()>END) {
			c.proxy.receive(c, ready);			
		}
	}
	
	default public Object execute() {
		return getThis().proxy.receive(getThis().getRoot(), execute);
	}
	
	default public void resultAsync() {
		getThis().proxy.receive(getThis().getRoot(), async);
	}
	
	
	default public int commit() {
		return (Integer)(getThis().proxy.receive(getThis().getRoot(), commit));
	}
	
}
