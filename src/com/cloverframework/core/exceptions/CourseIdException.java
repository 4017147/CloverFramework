package com.cloverframework.core.exceptions;

public class CourseIdException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7052670271197412693L;

	public CourseIdException(String id) {
		super("set course id error,id:"+id);
	}
}
