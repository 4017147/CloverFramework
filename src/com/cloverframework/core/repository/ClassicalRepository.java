package com.clover.core.repository;

import com.domain.DomainService;

/**
 * һ���ľ���dao��ִ���������Course�����ύ��������Ȼ��ͨ�õģ�ֱ�����������ֱ�ӵ��ýӿڷ������dao�����ݡ�
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
