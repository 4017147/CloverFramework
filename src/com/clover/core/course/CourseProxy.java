package com.clover.core.course;

import java.util.HashMap;

import com.clover.core.factory.EntityFactory;
import com.clover.core.repository.CourseRepository;
import com.domain.DomainService;
import com.infrastructure.util.lambda.Literal;
/**
 * course�����ṩ�������û���course�����͹�������ͨ��ʹ�ø��ഴ��ҵ����̶�����course��
 * ����󲿷ַ������̲߳���ȫ�ġ�
 * @author yl
 *
 */
public class CourseProxy implements CourseOperation{
	/** ���ڼ����������ֵ�ķ���ջ���Ƿ�Ϸ���
	 * �����ķ����е��ø����е�START()��START(args)�����������������п����ã��������أ�����Ҫ��Ӧ��+1*/
	byte level = 1;
	
	/**��������course����*/
	Course newest;
	/**eden�������ڻ���course����*/
	HashMap<String,Course> eden = new HashMap<String,Course>();
	
	protected DomainService service;
	
	CourseRepository repository;
	/*
	 * ������д�ӿڵķ����������courseProxy��ͬeden��newest�Ĳ���ʵ�֣�
	 * ��ʵ���У�������Ҫʹ�ú��ʵļ��ϺͶ�Ӧ�Ĳ������粢�������߶��У�
	 * ����ͨ��������д��Щ��������ʵ�֣�������������Խ��иĶ�
	 */
	
	@Override
	public Course getCurrCourse() {
		return newest;
	}
	
	@Override
	public Course removeCurrCourse() {
		Course old = getCurrCourse();
		newest = null;
		return old;
	}
	
	@Override
	public void setCurrCourse(Course course) {
		newest = course;
	}
	
	@Override
	public Course getCourse(String id) {
		return eden.get(id);
	}
	
	@Override
	public void addCourse(String id,Course course) {
		eden.put(id, course);
	}
	
	@Override
	public Course removeCourse(String id) {
		return eden.remove(id);
	}
	
	/*----------------------private method-------------------- */
	/**
	 * ��ʼ��һ��course
	 */
	private Course initCourse(Course course,DomainService service,CourseProxy proxy,byte status) {
		course.domainService = service;
		course.proxy = proxy;
		course.status = status;
		return course;
	}
	
	/**
	 * �÷������ʼ��һ��course�����͵�factory��course�����У�
	 * ���һ��жϴ����course��ոմ�����course�Ƿ�������ͬ��
	 * �������ͬ���׳��쳣��Ϊ��ֹ��ȡ�����ջ��߱�jvm�Ż���
	 * @return ����һ�����ڵ�
	 */
	private Course begin() {
		Thread t = Thread.currentThread();
		//�����÷�����λ����Ҫ�޸�length��ֵ��ÿ��һ���ϼ���������length-1
		Course course = EntityFactory.putCourse(getCurrCourse(), t, t.getStackTrace().length-level);
		if(course.status==Course.WAIT) {
			return course;
		}
		//������Ϸ��صĲ��Ǹոմ����Ķ���
		return null;
	}

	/**
	 * ����course�ڲ���ʱ������
	 * @param course
	 */
	private void setCourseTime(Course course) {
		long exe = System.currentTimeMillis()-course.createTime;
		course.max_exe = (exe>course.max_exe?exe:course.max_exe);
		course.min_exe = (exe<course.min_exe?exe:course.min_exe);
		if(course.min_exe==0)
			course.min_exe = exe;
		course.avg_exe = (exe+course.avg_exe)/(course.avg_exe==0?1:2);
	}

	
	/*----------------------public method-------------------- */
	
	
	
	public CourseProxy() {}

	public CourseProxy(DomainService service) {
		this.service = service;
	}
	
	public HashMap<String, Course> getEden() {
		return eden;
	}
	
	public void setRepository(CourseRepository repository) {
		this.repository = repository;
	}

	/**
	 * ��ȡcourse list���Ѻ���Ϣ
	 * @see CourseProxy#getInfo()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		for(String key:eden.keySet()) {
			Course course = eden.get(key);
			course.condition1 = true;
			course.condition2 = true;
			sb.append(course.toString()+"\n");
		}
		return sb.toString();			
	}

	/**
	 * ��ӡcourse list����ϸ����û��Ҫ��ʹ��toString()
	 * @return
	 */
	public String getInfo() {
		return getCurrCourse().toString()+"\n";
	}

	/**
	 * ��һ���µ�course�滻��һ��course��������һ��course
	 * @param cover false�������Ǽ����е���һ��course��true�����Ǽ����е���һ��course
	 * @return
	 */
	protected Course addCourse(String id,boolean cover) {
		Course old = removeCurrCourse();
		Course newc = new Course(id);
		initCourse(newc,service,this,Course.WAIT);
		if(cover)
			addCourse(old.id, newc);
		setCurrCourse(newc);
		return old;
	}
	
	
	/*------���ϣ��������ֻ�������������ʽʹ�ã���Щ��������дӦ��Ϊprotected------*/
	
	
	/**
	 * ��ʼһ��course�ķ�����ͨ���÷���������ʽִ��GET ADD PUT REMVE�ȷ���
	 * @return ����һ�����ڵ�
	 */
	public Course START() {
		addCourse(null,false);
		return begin();
	}
	
	/**
	 * �ڿ�ʼcourseʱ�ṩһ��id��Ϊ���ı�ʶ��ͬʱ��course��end֮��ᱻ���棬
	 * ���ִ��ʱ��id��course���ڻ��棬��ʹ�û����course
	 * @param id ���course�ı�ʶ�����ܰ����ո�
	 * @return
	 */
	public Course START(String id) {
		Course cache = null;
		String reg = "^[\\S]*$";
		if(id!=null && id.matches(reg))
			cache = getCourse(id);
		if(cache!=null)
			return cache;
		addCourse(id,false);
		return begin();
	}
	
	/**
	 * ����ǰcourse���Ϊend״̬������
	 * 1�����courseû��һ����Ч��id�򲻻Ỻ�档
	 * 2�����û�жԵ�ǰcourse����end������һ��start��courseʱ��ǰcourse�ᱻȡ����
	 */
	public void END() {
		Course course = getCurrCourse();
		setCourseTime(course);
		if(course.status==Course.END && course.id!=null)
			addCourse(course.id, course);
		removeCurrCourse();
	}

	/**
	 * ֱ��ִ�е�ǰ��һ��course���
	 * @return
	 */
	public Object executeOne() {
		return repository.query(newest);
	}
	
	
	/**
	 * һϵ�е�����ֵ�����Ŀ�ͷ�����磺�����GET($(),user.getName(),user.getId())֮ǰ��
	 * ��course��������ĵط�ʹ����User.getName()�ȷ�����
	 * ��$()�����Ǳ�������ڲ����ĵ�һλ����Ĩ��֮ǰ�޹ص�����ֵ��
	 * @return null
	 */
	public Object $() {
		getCurrCourse().literalList.clear();
		return null;
	}
	
	/**
	 * �������ã���lambda�ķ�ʽ�ṩ�������������
	 * ��GET($(user::getName))�൱��GET(user.getName()),���ж����������ֵ��Ҫ��ȡ������ø÷�ʽ��<p>
	 * ���磺GET($(user::getName,user::getId,user,user::getCode),����Ϊlambda������²���Ҫ$()��
	 * �����user.getName()�����ķ������ã���ôlambda������һ�����ʽ����д�ڲ����ĵ�һλ��
	 * 
	 * @param lt ʵ�����lambda���ʽ����user::getName��������ô����﷨����()->user.getName(),
	 * ��Ϊ����д��������������������lambda��ִ�ж�������������������ֵ�����������Ĳ�һ�£�
	 * ����Course�Ѿ�����������������������޷���ȫ��ֹͨ�������ֶ����룬��������java���Ի��������ˣ������ʵ��ʹ����
	 * ��Ҫ������⡣���ң����������棨��domainMatch��EntityFactory����Ը��������ԭ���ϵĴ���
	 * @return null
	 */
	public Object $(Literal ...lt) {
		Course course = getCurrCourse();
		if(course.status==Course.WAIT)
			course.literalList.clear();
		course.status = Course.LAMBDA;
		for(Literal li:lt) {
			li.literal();
		}
		course.status = Course.METHOD;
		return null;
	}
	
	/**
	 * ��Ԫ�������ã��ɻ�ȡ��Ԫ��������lambda��������Ϊ������ֵ�����б�����һ��
	 * @param li @see {@link CourseProxy#$(Literal...)}
	 * @return
	 * @
	 */
	public Object $te(Literal li) {
		Course course = getCurrCourse();
		int c = course.status;
		if(course.status==Course.WAIT)
			course.literalList.clear();
		course.status = Course.LAMBDA;
		li.literal();
		course.status = Course.METHOD;
		if(c!=0)
			course.literalList.remove(course.literalList.size()-2);
		return null;
	}
	
	/**
	 * �ɻ�ȡ��Ԫ������������ֵ
	 * @param obj @see {@link CourseProxy#$(Literal...)}
	 * @return
	 */
	public Object te(Object obj) {
		Course course = getCurrCourse();
		course.literalList.remove(course.literalList.size()-2);
		return null;
	}
	
	
}
