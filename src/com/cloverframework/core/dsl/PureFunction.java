package com.cloverframework.core.dsl;

import java.util.function.Function;

import com.cloverframework.core.util.lambda.Choice;

public interface PureFunction {
	default <B, A> Function<A,Boolean> Case(B b1,B b2,Choice<A> then) {
		return (a)->{
			if(b1==b2) {
				then.apply(a);
				return true;
			}
			else
				return false;
					};
	}
	
	default <B, A> Function<A,Boolean> Case(boolean b,Choice<A> then) {
		return (a)->{
			if(b==true) {
				then.apply(a);
				return true;
			}
			else
				return false;
					};
	}
}
