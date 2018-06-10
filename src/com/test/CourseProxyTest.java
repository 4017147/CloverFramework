package com.test;

import static com.test.SYSOUT.println;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.domain.annotation.Domain;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.Course.Get;
import com.cloverframework.core.dsl.CourseProxy;
import com.dict.entity.Demo_D;
import com.entity.Demo;
import com.entity.User;

@Domain("Demo")
public class CourseProxyTest implements DomainService{
	private static Demo demo;

	@BeforeClass
	public static void setup() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(null);
		 demo= cp.getStaple(Demo.class);
		 demo.setF5("f5");
		 demo.setF6("f6");
		 demo.setF7("f7");
	}
	
	@Rule public TestName name = new TestName();
	@Before
	public void start() {
		println("===================== "+name.getMethodName());
	}
	
	/**
	 * 构造测试,输出测试
	 * 方法字面值获取
	 * 非节点范围的方法字面值不获取
	 */
	@Test
	public void test1() {
		Demo de = new Demo();
		de.getF10();de.getF9();
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this);
		cp.Master("a").get(cp.$$(),
				demo.getF1(),
				demo.getF2(),
				demo.getF3(),
				demo.getF4()).by(
						demo.getF5(),
						demo.getF6()).and(demo.getF5());
		
		Get get = cp.Master("b").get(cp.$$(),
				demo.getF1(),
				demo.getF2(),
				demo.getF3(),
				demo.getF4());
		de.getF10();de.getF9();
		get.by(demo.getF5(),
				demo.getF6()).LIMIT(0, 10);//实体对象，limit
		println(cp.toString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典
	 */
	@Test
	public void test2() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a").get($$(),
					demo.getF1(),
					demo.getF2(),
					demo,demo,
					Demo_D.f3,
					Demo_D.f4).by($$(),
							demo.getF1(),
							demo.getF2(),
							demo,demo,//
							Demo_D.f3,
							Demo_D.f4).END();
		}};
		println(cp.toString());
	}
	
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典+lambda
	 * toJsonString测试
	 */
	@Test
	public void test3() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("b1111").get(
					$$(demo::getF5,demo::getF6),
					Demo_D.f1,
					Demo_D.f2,
					demo.getF3(),
					demo.getF4(),
					demo,demo).by(
							demo,//by输入实体的意义分析
							Demo_D.f1,
							Demo_D.f2).END();
		}};
		println(cp.toString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典+lambda
	 * 三元表达式
	 */
	@Test
	public void test4() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("b").get(
					$$(demo::getF5,demo::getF6,demo::getF9),
					Demo_D.f1,//顺序
					demo.getF7(),
					demo.getF8(),
					$$(demo!=null?demo::getF4:demo::getF3),
					$$(demo==null?demo::getF4:demo::getF3),
					te(demo.getF5()==null?demo.getF3():demo.getF4()),
					te(demo.getF5()!=null?demo.getF3():demo.getF4()),
					Demo_D.f2,
					demo,demo
					).END();
			println(getCourse("b").toString());
		}};
		//println(cp.toString());
	}
	
	
	/**
	 * sharespace区缓存测试
	 * 只缓存生产的第一个DSL
	 */
	@Test
	public void test5() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a").get(Demo_D.f2);
			Master("a").get(Demo_D.f1,Demo_D.f2);
			Master("b").get(Demo_D.f3,Demo_D.f4);
			Master("c").get(Demo_D.f5,Demo_D.f6);
			Master("c").get(Demo_D.f6);
		}};
		println(cp.toString());
	}
	
	/**
	 * FORK基础测试
	 * FROK()不缓存分支
	 * FORKM()存入sharespace
	 */
	@Test
	public void test6() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a").get(Demo_D.f1,Demo_D.f2).END();
			Branch("a").get(Demo_D.f3,Demo_D.f4).END();
			BranchM("a").get(Demo_D.f5,Demo_D.f6).by(Demo_D.f7,Demo_D.f8).END();
		}};
		println(cp.toString());
		
	}
	
	/**
	 * FORK模式测试
	 */
	@Test
	public void test7() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).END();
			//FORKM("a").get(U,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(I,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(C,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(CA,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(CB,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			BranchM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f9,Demo_D.f10,Demo_D.f3).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f9,Demo_D.f10,Demo_D.f3).END();
			
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6).END();
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			BranchM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			
			//FORKM("a").get(MA,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			//FORKM("a").get(MB,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
		}};
		println(cp.toString());
		
	}
	
	/**
	 * FORK模式测试
	 * 首个参数用来开启FORK模式，参数不匹配不开启
	 * 节点类型不匹配则关闭该节点和后续节点的FORK模式
	 */
	@Test
	public void test8() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			//Master("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).by(Demo_D.f10).END();
			Master("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).groupBy(Demo_D.f10).END();
			//Master("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).END();
			
			BranchM("a").get(Demo_D.f5,Demo_D.f6,Demo_D.f7).by(MB,Demo_D.f8,Demo_D.f9).END();
			//BranchM("a").get(MB,Demo_D.f5,Demo_D.f6,Demo_D.f7).by(Demo_D.f8,Demo_D.f9).END();
			//BranchM("a").get().by(Demo_D.f8,Demo_D.f9).END();
		}};
		println(cp.toString());
		
	}
	
	/**
	 * 子节点测试
	 * 子节点应当包含在父节点内
	 */
	@Test
	public void test9() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a")
			.get(Demo_D.f1)
			.by(Demo_D.f10).eq(20)
			.and($$(Demo_D.f9).eq(33).or(Demo_D.f8).le(11)).eq(10)
			.or(Demo_D.f3,$$(Demo_D.f5).gt(33).or(Demo_D.f6).lt(11))//condition节点共存问题
			.END();
			
		}}; 
		println(cp.toString());
		println(cp.getCourse("a").getJsonString());
	}
	 
	/**
	 * 聚合子节点测试
	 * 子节点应当包含在父节点内
	 */
	@Test
	public void test10() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a")
			.get(count(Demo_D.f2),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
			.by(Demo_D.f10).eq(20)
			.END();
			
		}}; 
		println(cp.toString());
		println(cp.getCourse("a").getJsonString());
	}
	
	/**
	 * 参数值设置测试
	 * 参数值应当正常输出，重复的设置应当覆盖之前的值
	 */
	@Test
	public void test11() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a")
			.get(count(Demo_D.f2),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
			.by(Demo_D.f10).eq(20).setInt(30).setString("hello")
			.END();
		}}; 
		println(cp.toString());
		println(cp.getCourse("a").getJsonString());
	}
	
	/**
	 * 使用$传入字段和方法参数
	 * 值对象能够判断参数个数是否符合要求,当多个字段值时，
	 * 值参数（包括$取的值）只能为1个或者跟字段值个数相同，否则抛出异常
	 * 当单个字段值时，参数不能为空但是个数没有限制
	 */
	@Test
	public void test12() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a")
			.get(demo.getF5(),count(Demo_D.f2),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
			.by(Demo_D.f10,Demo_D.f8).eq(40,$$(demo.getF5())).and(demo.getF5()).eq(1,2)
			.END();
		}}; 
		println(cp.toString());
		println(cp.getCourse("a").getJsonString());
	}
	
	/**
	 * 回调测试
	 */
	@Test
	public void test13() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			Master("a")
			.get(count(Demo_D.f2),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
			.by(Demo_D.f10).eq(20)
			.LOCK();
			println(getCourse("a").getStatus());
			getCourse("a").UNLOCK();
			println(getCourse("a").getStatus());
			getCourse("a").END();
			println(getCourse("a").getStatus());
		}}; 
	}
	
	/**
	 * 缓存判断测试
	 * 如果DSL已存在则不执行当前DSL,
	 * 测试10000次执行效率
	 */
	@Test
	public void test14() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			int k = 10;
			int b = 20;
			User user = new User();
			for(int i=0;i<1000;i++) {
				Master(String.valueOf(i),(a)->{
					Master(a)
					.get(count(Demo_D.f2),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
					.by(Demo_D.f10,Demo_D.f8).eq(k,b).and(demo.getF5()).eq(user.getId(),2);
				});
			}
		}}; 
		//println(cp.toString());
	}
	
	@Test
	public void test15() {
		CourseProxy<User,Course> cp = new CourseProxy<User,Course>(this) {{
			User user = new User();
			Master("dynamic").get(Demo_D.f1,Demo_D.f4)
				.te(user.getId()==1,
					(t1)->{t1.by(Demo_D.f3).eq(1)
						.te(user.getUsername()!=null,
							(t2)->{t2.and(Demo_D.f1).eq(1);});},
					(otherwise)->{otherwise.by(Demo_D.f4).eq(1);
					});
		}};
		println(cp);
	}
	
	
}
