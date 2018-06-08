package com.cloverframework.core.repository.interfaces;

import com.cloverframework.core.data.interfaces.Result;
import com.cloverframework.core.data.interfaces.Swaper;

/**
* @author yl  
* 
*    
*/
public interface CourseMode<T> {
	T query(Swaper<T> swaper);
	int update(Swaper<T> swaper);
	Result<T> result(Swaper<T> swaper);
}
