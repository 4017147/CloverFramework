package com.cloverframework.core.course;

import java.util.HashMap;

import com.cloverframework.core.course.Course.Condition;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.factory.EntityFactory;
import com.cloverframework.core.repository.CourseRepository;
import com.cloverframework.core.util.CourseType;
import com.cloverframework.core.util.Pattern;
import com.cloverframework.core.util.lambda.Literal;
import com.infrastructure.util.Matcher;
/**
 * course代理提供了面向用户的course操作和管理方法，通常使用该类创建业务过程而不是course，
 * 该类大部分方法是线程不安全的。
 * @author yl
 *
 */
public class CourseProxy implements CourseOperation{
	/** 用于计算产生字面值的方法栈长是否合法，
	 * 如果别的方法中调用该类中的START()或START(args)方法（仅开发过程中可设置，对外隐藏），需要相应的+1*/
	byte level = 1;
	
	/**最后产生的course对象*/
	Course newest;
	/**share区，用于缓存course对象*/
	HashMap<String,Course> shareSpace = new HashMap<String,Course>();
	
	protected DomainService service;
	
	CourseRepository repository;
	
	Pattern pattern = new Matcher();
	
	/**并集 */
	public static final String U = "U";//
	/**交集 */
	public static final String I = "I";//
	/**补集*/
	public static final String C = "C";//
	/**前置并集 */
	public static final String UB = "UB";
	/**后置并集 */
	public static final String UA = "UA";
	/**前置混合 */
	public static final String MB = "MB";//
	/**后置混合 */
	public static final String MA = "MA";//
	/**正交 */
	public static final String M = "M";//
	/**反交 */
	public static final String RM = "RM";//
	/**左补 */
	public static final String CB = "CB";//
	/**右补 */
	public static final String CA = "CA";//
	
	
	public static final String[] Model = {U,I,C,UB,UA,MB,MA,M,RM,CB,CA};
	
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
		return shareSpace.get(id);
	}
	
	@Override
	public void addCourse(String id,Course course) {
		shareSpace.put(id, course);
	}
	
	@Override
	public Course removeCourse(String id) {
		return shareSpace.remove(id);
	}
	
	/*----------------------private method-------------------- */
	/**
	 * 初始化一个course
	 */
	private Course initCourse(String id,Course course,DomainService service,CourseProxy proxy,byte status) {
		course.domainService = service;
		course.proxy = proxy;
		course.setStatus(status);
		return course;
	}
	
	/**
	 * 该方法会初始化一个course并发送到filter的course集合中，
	 * 并且会判断存入的course与刚刚创建的course是否引用相同，
	 * 如果不相同则抛出异常，为防止获取到快照或者被jvm优化。
	 * @return 返回一个根节点
	 */
	private Course begin() {
		Thread t = Thread.currentThread();
		//调整该方法的位置需要修改length的值，每多一个上级方法调用length-1
		//System.out.println(t.getStackTrace().length-level);
		Course course = EntityFactory.putCourse(getCurrCourse(), t, t.getStackTrace().length-level);
		if(course.getStatus()==Course.WAIT) {
			return course;
		}
		//如果集合返回的不是刚刚创建的对象
		return null;
	}

	/**
	 * 设置course内部的时间属性
	 * @param course
	 */
	protected void setCourseTime(Course course) {
		long exe = System.currentTimeMillis()-course.create;
		course.max = (exe>course.max?exe:course.max);
		course.min = (exe<course.min?exe:course.min);
		if(course.min==0)
			course.min = exe;
		course.avg = (exe+course.avg)/(course.avg==0?1:2);
	}

	
	/*----------------------public method-------------------- */
	
	
	
	public CourseProxy() {}

	public CourseProxy(DomainService service) {
		this.service = service;
	}
	
	public HashMap<String, Course> getEden() {
		return shareSpace;
	}
	
	public void setRepository(CourseRepository repository) {
		this.repository = repository;
	}

	/**
	 * 获取course list的友好信息
	 * @see CourseProxy#getInfo()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		for(String key:shareSpace.keySet()) {
			Course course = shareSpace.get(key);
			course.condition1 = true;
			course.condition2 = true;
			sb.append(course.toString()+"\n");
		}
		return sb.toString();			
	}

	/**
	 * 打印course list的详细，如没必要，使用toString()
	 * @return
	 */
	public String getInfo() {
		return getCurrCourse().toString()+"\n";
	}

	/**
	 * 将一个新的course替换上一个course，返回上一个course
	 * @param cover false：不覆盖集合中的上一个course，true：覆盖集合中的上一个course
	 * @return
	 */
	protected Course addCourse(String id,boolean cover) {
		Course old = removeCurrCourse();
		Course newc = new Course(id);
		initCourse(id,newc,service,this,Course.WAIT);
		if(cover)
			addCourse(old.id, newc);
		setCurrCourse(newc);
		return old;
	}
	
	
	/*------如果希望调用者只能以匿名类的形式使用，这些方法或重写应当为protected------*/
	
	
	/**
	 * 开始一个course的方法，通过该方法可以链式执行GET ADD PUT REMVE等方法
	 * @return 返回一个根节点
	 */
	public Course START() {
		addCourse(null,false);
		return begin();
	}
	
	/**
	 * 在开始course时提供一个id作为它的标识，同时该course在end之后会被缓存，
	 * 如果执行时该id的course存在缓存，则使用缓存的course。
	 * 如果取出的course已经被缓存，则后面\重复的修改不会生效
	 * @param id 这个course的标识，不能包含空格
	 * @return
	 */
	public Course START(String id) {
		Course old = null;
		old = getCourse(id);
		if(old!=null) {
			setCurrCourse(old);
			return old;
		}
		//System.out.println("begin");
		addCourse(id,false);
		return begin();
	}
	
	/**
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照START(id)模式进行
	 * @param id
	 * @return
	 */
	public Course FORKM(String id) {
		Course course = getCourse(id);
		if(course!=null) {
			addCourse(id+"_FM_"+System.currentTimeMillis(),false);
			getCurrCourse().isForkm = true;
			cross(id,getCurrCourse());
		}else {
			addCourse(id+"_NFM_"+System.currentTimeMillis(),false);
		}
		return begin();
	}
	
	/**
	 * 根据sharespace中的一个course创建分支引用，如果对应id的course存在，
	 * 则进行分支，否则不进行分支，并且按照START()模式进行
	 * @param id
	 * @return
	 */
	public Course FORK(String id) {
		Course course = getCourse(id);
		if(course!=null) {
			addCourse(id+"_F_"+System.currentTimeMillis(),false);
			getCurrCourse().isFork = true;
			cross(id,getCurrCourse());
		}else {
			addCourse(id+"_NF_"+System.currentTimeMillis(),false);
		}		
		return begin();
	}
	
	
	public Course cross(String id,Course course) {
		course.origin = getCourse(id).next;
		return course;
	}

	/**
	 * 将当前course标记为end状态并放入sharespace
	 * 1、如果course没有一个有效的id则不会放入sharespace
	 * 2、如果没有对当前course进行end，在下一次start新course时当前course会被取代。
	 * 3、分支course不会放入sharespace
	 */
	protected void END() {
		Course course = getCurrCourse();
		if(course.getStatus()==Course.END && course.id!=null && !course.isFork) {
			setCourseTime(course);
			addCourse(course.id, course);			
		}else if(course.getStatus()==Course.END && course.id==null){
			setCourseTime(course);
			course.id = String.valueOf(course.hashCode());
		}
	}

	/**
	 * 直接执行当前的一条course语句
	 * @return
	 */
	public Object executeOne() {
		return repository.query(newest);
	}
	
	
	/**
	 * 一系列的字面值参数的开头，例如：如果在GET($(),user.getName(),user.getId())之前，
	 * 在course方法以外的地方使用了User.getName()等方法，
	 * 则$()方法是必需出现在参数的第一位，以抹除之前无关的字面值。
	 * @return null
	 */
	public Object $() {
		getCurrCourse().literal.clear();
		return null;
	}
	
	/**
	 * 方法引用，以lambda的方式提供方法字面参数，
	 * 如GET($(user::getName))相当于GET(user.getName()),当有多个方法字面值需要获取，最好用该方式。<p>
	 * 例如：GET($(user::getName,user::getId,user,user::getCode),首项为lambda的情况下不需要$()。
	 * 如果字面值中还有user.getName()这样的方法引用，那么lambda的任意一个表达式必需写在字面值参数的第一位。
	 * 
	 * @param lt 实体类的lambda表达式，如user::getName，建议采用此种语法而非()->user.getName(),
	 * 因为这种写法会带来隐患，比如可在lambda中执行多个句柄，这样会令字面值参数跟期望的不一致，
	 * 尽管Course已经尽力避免这种情况，但是无法完全阻止通过这种手段输入，这是由于java语言机制限制了，因此在实际使用中
	 * 需要多加留意。并且，在其他层面（如domainMatch、EntityFactory）会对该问题进行原则上的处理。
	 * @return null
	 */
	public Object $(Literal ...lt) {
		Course course = getCurrCourse();
		if(course.getStatus()==Course.WAIT)
			course.literal.clear();
		course.setStatus(Course.LAMBDA);
		for(Literal li:lt) {
			li.literal();
		}
		course.setStatus(Course.METHOD);
		return null;
	}
	
	/**
	 * 可获取三元运算结果的字面值
	 * @param obj @see {@link CourseProxy#$(Literal...)}
	 * @return
	 */
	public Object te(Object obj) {
		Course course = getCurrCourse();
		course.literal_te.add(course.literal.get(course.literal.size()-1));
		course.literal.remove(course.literal.size()-1);
		course.literal.remove(course.literal.size()-1);
		return Course.Te.te;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * 节点引用，同于创建子节点，不能脱离当前节点使用
	 * @param course
	 * @return
	 */
	public Condition $(Object... obj){
		AbstractCourse<?> course = getCurrCourse();
		AbstractCourse<?> last = null;
		while(course!=null) {
			last = course;
			course = course.next;
		}
		Condition con = new Condition(last,CourseType.con,true,obj);
		return con;
		
	}
	
}
