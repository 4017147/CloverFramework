package com.cloverframework.core.repository.interfaces;

import com.cloverframework.core.data.interfaces.DataSwap;

/**
* @author yl  
* 
*    
*/
public interface ICourseMode<T> {
	T get(DataSwap<T> swaper);
	int add(DataSwap<T> swaper);
	int put(DataSwap<T> swaper);
	int remove(DataSwap<T> swaper);
}
