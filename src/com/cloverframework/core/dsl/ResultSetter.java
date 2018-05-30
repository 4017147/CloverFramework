package com.cloverframework.core.dsl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.cloverframework.core.data.interfaces.CourseResult;
import com.cloverframework.core.dsl.interfaces.Accessable;
import com.cloverframework.core.exceptions.ExceptionFactory;

public interface ResultSetter extends Accessable{
	/**
	 * 返回该course的result对象，如果同步result存在则优先返回，否则返回异步result
	 */
	default public CourseResult<?> getResult(){
		AbstractCourse c = getThis();
		if(c.result!=null)
			return c.result.get();
		if(c.futureResult!=null)
			try {
				return c.futureResult.get().get(CourseProxy.getGetResultTimeout(), TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				ExceptionFactory.wrapException("CourseResult error in "+c.id, e);
			}
		return null;
	}

	/**
	 * result不会跟随节点立刻创建，根据流程会推迟到仓储接收返回结果时创建
	 * @param result
	 */
	default public void setResult(CourseResult<?> result) {
		getThis().createResult();
		getThis().result.set(result);
	}

	/**
	 * 设置异步result对象
	 * @param futureResult
	 */
	default public void setResult(CompletableFuture<CourseResult> futureResult) {
		getThis().createFutureResult();
		getThis().futureResult.set(futureResult);
	}
	
	default public ThreadLocal<CompletableFuture<CourseResult>> getFutureResult() {
		return getThis().futureResult;
	}
}
