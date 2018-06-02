package com.cloverframework.core.dsl.interfaces;

/**
 * 常量接口，无方法
 * @author yl
 *
 */
public interface Constant {
	
	//-----------------------course-----------------------
	/**异常*/
	public static final int ERROR 		=-4;
	/**关闭,不能执行任何设置操作*/
	public static final int END 		=-3;
	/**锁定*/
	public static final int LOCKED 		=-2;
	/**正在填充*/
	public static final int FILL 		=-1;
	/**待填充*/
	public static final int WAIT 		= 0;
	/**添加字面值(从lambda)*/
	public static final int LAMBDA 		= 1;
	/**添加字面值(从方法)*/
	public static final int METHOD 		= 2;
	/**添加字面值(从lambda三元)*/
	public static final int LAMBDA_TE 	= 3;
	/**添加字面值(三元)*/
	public static final int TE 			= 4;
	
	//-------------------------proxy-------------------------
	
	/**查询*/
	public static final int execute 		= 11;
	/**更新*/
	public static final int commit 			= 12;
	/**无阻塞查询*/
	public static final int executeFuture 	= 13;
	/**无阻塞更新*/
	public static final int commitFuture 	= 14;
	/**重建分支*/
	public static final int rebase 			= 15;
}
