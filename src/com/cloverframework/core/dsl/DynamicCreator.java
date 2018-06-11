package com.cloverframework.core.dsl;

import com.cloverframework.core.util.lambda.IFCreate;
/**
 * 用于动态的创建DSL节点
 * @author yl
 *
 * @param <A>
 */
public abstract class DynamicCreator<A> {
	
	/**
	 * 
	 * @param condition 条件
	 * @param f	满足时执行
	 */
	@SuppressWarnings("unchecked")
	public void te(boolean condition,IFCreate<A> f) {
		if(condition==true)
			f.apply((A) this);
	}
	
	/**
	 * 
	 * @param condition 条件
	 * @param IF	满足时执行
	 * @param ELSE	否则执行
	 */
	@SuppressWarnings("unchecked")
	public void te(boolean condition,IFCreate<A> IF,IFCreate<A> ELSE) {
		if(condition==true)
			IF.apply((A) this);
		else
			ELSE.apply((A) this);
	}
}
