package com.cloverframework.core.dsl;

import com.cloverframework.core.dsl.interfaces.Accessable;

public interface HeadCreator extends Accessable{
	
	default String getHead() {
		return getThis().head;
	}
	
	default void setHead() {
		char[] head = new char[7];
		for(int i = 0;i<7;i++) {
			double d = Math.random();
			if(d<0.3)
				head[i] = (char) (int)(Math.random()*10+48);
			if(d>=0.3&&d<=0.6)
				head[i] = (char) (int)(Math.random()*26+65);
			if(d>0.6)
				head[i] = (char) (int)(Math.random()*26+97);
		}
		getThis().head = String.valueOf(head);
	}
	
}
