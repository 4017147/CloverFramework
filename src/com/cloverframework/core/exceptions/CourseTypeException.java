package com.cloverframework.core.exceptions;

public class CourseTypeException extends RuntimeException{

	/**
	 * Course类型错误时抛出
	 */
	private static final long serialVersionUID = -4766259643625340683L;
	
	public CourseTypeException(String type) {
		super("CourseType is incorrect:"+type);
		// TODO Auto-generated constructor stub
	}
	
	
}
