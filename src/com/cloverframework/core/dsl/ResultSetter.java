package com.cloverframework.core.dsl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cloverframework.core.data.interfaces.CourseResult;
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
	default public void setResult(CourseResult<?> result) {
		AbstractCourse root = getThis();
		while(root.previous!=null) {
			root = root.previous;
		}
		root.createResult();
		root.futureResult.remove();
		root.result.set(result);
	}

	/**
	 * 设置异步result对象
	 * @param futureResult
	 */
	default public void setResult(CompletableFuture<CourseResult> futureResult) {
		AbstractCourse root = getThis();
		while(root.previous!=null) {
			root = root.previous;
		}
		root.createFutureResult();
		root.futureResult.remove();
		root.futureResult.set(futureResult);
	}
	
	/**
	 * 返回该course的result对象，如果同步result存在则优先返回，否则返回异步result
	 */
	default public CourseResult<?> getResult(){
		AbstractCourse root = getThis();
		while(root.previous!=null) {
			root = root.previous;
		}
		if(root.result!=null)
			return root.result.get();
		if(root.futureResult!=null)
			try {
				return root.futureResult.get().get(CourseProxy.getGetResultTimeout(), TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				ExceptionFactory.wrapException("CourseResult error in "+root.id, e);
			}
		return null;
	}

	default public ThreadLocal<CompletableFuture<CourseResult>> getFutureResult() {
		AbstractCourse root = getThis();
		while(root.previous!=null) {
			root = root.previous;
		}
		return root.futureResult;
	}
}
