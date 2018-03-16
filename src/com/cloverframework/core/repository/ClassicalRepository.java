package com.clover.core.repository;

import com.domain.DomainService;

/**
 * 一个的经典dao层仓储，不适用Course工具提交，但它仍然是通用的，直接由领域服务直接调用接口方法获得dao层数据。
 * @author yl
 *
 */
public class ClassicalRepository extends AbstractRepository{
	private IClassicalMode mode;
	
	public void setMode(IClassicalMode mode) {
		this.mode = mode;
	}
	public <E> E get(Class<E> Class,Integer key,DomainService service) {
		return super.get(Class,key,mode, service);
	}
	public <E> Integer add(E entity,DomainService service) {
		return super.add(entity, mode,service);
	}
	public <E> Integer put(E entity,DomainService service) {
		return super.put(entity, mode,service);
	}
	public <E> Integer remove(Class<E> Class,Integer key,DomainService service) {
		return super.remove(Class,key, mode,service);
	}
}
