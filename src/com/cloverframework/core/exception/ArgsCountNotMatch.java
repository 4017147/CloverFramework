package com.cloverframework.core.exception;

/**
 * course的value给定的参数个数跟字段个数不匹配则抛出该异常
 */
public class ArgsCountNotMatch extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public ArgsCountNotMatch(int f,int a) {
		super("expect "+f+" args but "+a+" args");
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
