package com.cloverframework.core.repository.interfaces;

import com.cloverframework.core.repository.DataSwaper;

/**
* @author yl  
* 
*    
*/
public interface ICourseMode<T> {
	T get(DataSwaper<T> swaper);
	int add(DataSwaper<T> swaper);
	int put(DataSwaper<T> swaper);
	int remove(DataSwaper<T> swaper);
}
