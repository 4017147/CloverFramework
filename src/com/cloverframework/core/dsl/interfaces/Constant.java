package com.cloverframework.core.dsl.interfaces;

/**
 * 常量接口
 * @author yl
 *
 */
public interface Constant {
	
	//-----------------------course-----------------------
	/**异常*/
	int ERROR 		=-4;
	
	/**关闭,不能执行任何设置操作*/
	int END 		=-3;
	
	/**锁定*/
	int LOCKED 		=-2;
	
	/**正在填充*/
	int FILL 		=-1;
	
	/**待填充*/
	int UNLOCKED 	= 0;
	
	/**添加字面值(从lambda)*/
	int LAMBDA 		= 1;
	
	/**添加字面值(从方法)*/
	int METHOD 		= 2;
	
	/**添加字面值(从lambda三元)*/
	int LAMBDA_TE 	= 3;
	
	/**添加字面值(三元)*/
	int METHOD_TE 	= 4;
	
	//-------------------------proxy-------------------------
	
	/**准备*/
	int ready 			= 10;
	
	/**查询*/
	int execute 		= 11;
	
	/**更新*/
	int commit 			= 12;
	
	/**无阻塞查询*/
	int executeFuture 	= 13;
	
	/**无阻塞更新*/
	int commitFuture 	= 14;
	
	/**重建分支*/
	int rebase 			= 15;
	
	//-----------------------domainScope--------------------------
	
	/**领域服务内*/
	int domain 	= 20;
	
	/**本地线程*/
	int local 	= 21;
	
}
