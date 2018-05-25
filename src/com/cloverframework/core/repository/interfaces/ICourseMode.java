package com.cloverframework.core.repository.interfaces;

import com.cloverframework.core.data.interfaces.DataSwap;

/**
* @author yl  
* 
*    
*/
public interface ICourseMode<T> {
	T query(DataSwap<T> swaper);
	int update(DataSwap<T> swaper);
}
