package com.cloverframework.core.dsl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.dsl.interfaces.Accessable;
import com.cloverframework.core.exceptions.ExceptionFactory;

/**
 * 结果设置器
 * @author yl
 *
 */
public interface ResultSetter extends Accessable{
	
	
	/**
	 * result不会跟随节点立刻创建，根据流程会推迟到仓储接收返回结果时创建
	 * @param result
	 */
	default public void setResult(Result<?> result) {
		AbstractCourse<?> root = getThis().getRoot();
		root.createResult();
		root.result.remove();
		root.result.set(result);
	}

	/**
	 * 设置异步result对象
	 * @param futureResult
	 */
	default public void setResult(CompletableFuture<Result<?>> asyncResult) {
		AbstractCourse<?> root = getThis().getRoot();
		root.createAsyncResult();
		root.asyncResult.remove();
		root.asyncResult.set(asyncResult);
	}
	
	default public Result<?> getResult() {
		return getResult(0,true);
	}
	
	/**
	 * 返回该course的result对象，如果同步result存在则优先返回，否则返回异步result
	 * ，最后将接收的result存入local的result中
	 */
	default public Result<?> getResult(int timeout,boolean isCancelled){
		AbstractCourse<?> root = getThis().getRoot();
		Result<?> result = null;
		if(root.result!=null) {
			result = (Result<?>) root.result.get();
			return result;			
		}
		if(getAsyncResult()!=null) {
			try {
				CompletableFuture<Result<?>> future = getAsyncResult().get();
				if(timeout==0) {
					//log 
					result = future.get();
					setResult(result);
					return result;
				}else if(timeout>0){
					//注意，异步结果获取必需和执行内容对应，即一次执行一次获取
					result = future.get(Math.min(CourseProxy.getGetResultTimeout(),timeout/1000), TimeUnit.SECONDS);
					if(future.isDone()==false&&isCancelled==true) {
						System.out.println("mission is not completion: "+root.id);
						future.cancel(true);
						System.out.println("mission is cacelled:"+root.id);
					}
					//由于异步的setResult是在守护线程进行的，因此需要在本地线程再次setResult
					setResult(result);
					return result;
				}
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				ExceptionFactory.wrapException("CourseResult error in "+root.id, e);
			}			
		}
		return result;
	}

	default public ThreadLocal<CompletableFuture<Result<?>>> getAsyncResult() {
		return getThis().getRoot().asyncResult;
	}
}
