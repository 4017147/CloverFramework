package com.cloverframework.core.exceptions;

/**
 * Course关闭处于不可操作状态时抛出
 * @author yl
 *
 */
public class CourseIsClosed extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CourseIsClosed(String id) {
		super("This Course is closed:"+id);
		// TODO Auto-generated constructor stub
	}
	
	
	
}
