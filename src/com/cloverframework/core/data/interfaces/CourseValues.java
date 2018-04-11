package com.cloverframework.core.data.interfaces;

import java.util.List;

/**
 * course的查询参数为值对象，该对象的分装必需实现该接口，
 * 该接口的两个方法的返回集合是互斥的，即一个长度大于0，则另一个长度必定为零
 * @author yl
 *
 */
public interface CourseValues {
	List<Object> entityList();
	List<Object> objectList();
	
}
