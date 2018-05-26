package com.cloverframework.core.exceptions;

/**
 * 给定的参数个数跟节点字段个数不匹配则抛出该异常
 */
public class ArgsCountNotMatch extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public ArgsCountNotMatch(int f,int a) {
		super("expect "+f+" args but found "+a+" args");
	}
	
	
	
	
}
