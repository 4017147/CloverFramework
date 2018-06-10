package com.cloverframework.core.dsl;

import com.cloverframework.core.util.lambda.IFCreate;

public abstract class DynamicCreator<A> {
	
	@SuppressWarnings("unchecked")
	public void te(boolean condition,IFCreate<A> f) {
		if(condition==true)
			f.apply((A) this);
	}
	
	@SuppressWarnings("unchecked")
	public void te(boolean condition,IFCreate<A> IF,IFCreate<A> ELSE) {
		if(condition==true)
			IF.apply((A) this);
		else
			ELSE.apply((A) this);
	}
}
