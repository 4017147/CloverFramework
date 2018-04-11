package com.cloverframework.core.exception;

public class CourseIsClosed extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CourseIsClosed(String id) {
		super("This Course is closed:"+id);
		// TODO Auto-generated constructor stub
	}
	
	
	
}
