package com.cloverframework.core.dsl;

import java.util.function.Function;

import com.cloverframework.core.util.lambda.Choice;
/**
 * 用于动态的创建DSL节点
 * @author yl
 *
 * @param <A>
 */
public abstract class DynamicCreator<A> {
	
	/**
	 * if...
	 * @param condition 条件
	 * @param f	满足时执行
	 */
	@SuppressWarnings("unchecked")
	public void IS(boolean condition,Choice<A> f) {
		if(condition==true)
			f.apply((A) this);
	}
	
	/**
	 * if...
	 * @param b1
	 * @param b2
	 * @param f
	 */
	@SuppressWarnings("unchecked")
	public <B> void IS(B b1,B b2,Choice<A> f) {
		if(b1==b2)f.apply((A) this);
	}

	/**
	 * if...else...
	 * @param condition 条件
	 * @param then	满足时执行
	 * @param andThen	否则执行
	 */
	@SuppressWarnings("unchecked")
	public void IS(boolean condition,Choice<A> then,Choice<A> orthen) {
		if(condition==true)
			then.apply((A) this);
		else
			orthen.apply((A) this);
	}
	
	/**
	 * if...else...
	 * @param b1
	 * @param b2
	 * @param then
	 * @param andthen
	 */
	@SuppressWarnings("unchecked")
	public <B> void IS(B b1,B b2,Choice<A> then,Choice<A> andthen) {
		if(b1==b2)
			then.apply((A) this);
		else
			andthen.apply((A) this);
	}
	
	/**
	 * switch类型分支语句，不会进行break
	 * @param functions
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final void Switch(Function<A,Boolean> ...functions) {
		for(Function<A,Boolean> function:functions ) {
			function.apply((A) this);
		}
	}
	
	/**
	 * switch类型分支语句，不会进行break
	 * @param def 无满足则执行
	 * @param functions
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final void Switch(Choice<A> def,Function<A,Boolean> ...functions) {
		boolean end = false;
		for(Function<A,Boolean> function:functions ) {
			if(function.apply((A) this)==true)end=true;
		}
		if(end==false)def.apply((A) this);
	}
	
	/**
	 * switch类型分支语句，满足则break
	 * @param functions
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final void choose(Function<A,Boolean> ...functions) {
		for(Function<A,Boolean> function:functions ) {
			if(function.apply((A) this)==true)break;
		}
	}
	
	/**
	 * switch类型分支语句，满足则break
	 * @param def 没有满则则执行
	 * @param functions
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final void choose(Choice<A> def,Function<A,Boolean> ...functions) {
		for(Function<A,Boolean> function:functions ) {
			if(function.apply((A) this)==true)break;
		}
		def.apply((A) this);
	}
}
