package com.cloverframework.core.util;

public interface Pattern {
	boolean isMatch(Object obj);
	default boolean isMatch() {
		return false;	
	}
}
