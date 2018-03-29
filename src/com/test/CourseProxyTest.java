package com.test;

import static com.test.SYSOUT.println;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.cloverframework.core.course.Course.Get;
import com.cloverframework.core.course.CourseProxy;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.domain.annotation.Domain;
import com.cloverframework.core.factory.EntityFactory;
import com.dict.entity.Demo_D;
import com.entity.Demo;

@Domain("Demo")
public class CourseProxyTest implements DomainService{
	private static Demo demo;

	@BeforeClass
	public static void setup() {
		 demo= EntityFactory.getInstance().getStaple(Demo.class);
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
		CourseProxy cp = new CourseProxy(this);
		cp.START("a").get(cp.$(),
				demo.getF1(),
				demo.getF2(),
				demo.getF3(),
				demo.getF4()).by(
						demo.getF5(),
						demo.getF6()).LIMIT(0, 10).END();
		
		Get get = cp.START("b").get(cp.$(),
				demo.getF1(),
				demo.getF2(),
				demo.getF3(),
				demo.getF4());
		de.getF10();de.getF9();
		get.by(demo.getF5(),
				demo.getF6()).LIMIT(0, 10).END();
		println(cp.toString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典
	 */
	@Test
	public void test2() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get($(),
					demo.getF1(),
					demo.getF2(),
					demo,demo,
					Demo_D.f3,
					Demo_D.f4).by($(),
							demo.getF1(),
							demo.getF2(),
							demo,demo,
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
		CourseProxy cp = new CourseProxy(this) {{
			START("b").get(
					//
					$(demo::getF5,demo::getF6),
					Demo_D.f1,
					Demo_D.f2,
					demo.getF3(),
					demo.getF4(),
					demo,demo).by(
							demo,
							Demo_D.f1,
							Demo_D.f2).END();
		}};
		println(cp.toString());
		println(cp.START("b").getJsonString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典+lambda
	 * 三元表达式
	 */
	@Test
	public void test4() {
		CourseProxy cp = new CourseProxy(this) {{
			START("b").get(
					Demo_D.f1,
					$(demo::getF5,demo::getF6,demo::getF9),
					demo.getF7(),
					demo.getF8(),
					$(demo!=null?demo::getF4:demo::getF3),
					$(demo==null?demo::getF4:demo::getF3),
					te(demo.getF5()==null?demo.getF3():demo.getF4()),
					te(demo.getF5()!=null?demo.getF3():demo.getF4()),
					Demo_D.f2,
					demo,demo
					).END();
		}};
		println(cp.toString());
	}
	
	
	/**
	 * sharespace区测试
	 */
	@Test
	public void test5() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get(Demo_D.f1,Demo_D.f2).END();
			START("b").get(Demo_D.f3,Demo_D.f4).END();
			START("c").get(Demo_D.f5,Demo_D.f6).END();
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
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get(Demo_D.f1,Demo_D.f2).END();
			FORK("a").get(Demo_D.f3,Demo_D.f4).END();
			FORKM("a").get(Demo_D.f5,Demo_D.f6).by(Demo_D.f7,Demo_D.f8).END();
		}};
		println(cp.toString());
		
	}
	
	/**
	 * FORK模式测试
	 */
	@Test
	public void test7() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).END();
			//FORKM("a").get(U,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(I,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(C,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(CA,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(CB,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f1,Demo_D.f2).END();
			FORKM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f9,Demo_D.f10,Demo_D.f3).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7,Demo_D.f8,Demo_D.f9,Demo_D.f10,Demo_D.f3).END();
			
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6).END();
			//FORKM("a").get(RM,Demo_D.f5,Demo_D.f6).END();
			//FORKM("a").get(M,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			FORKM("a").get(RM,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			
			//FORKM("a").get(MA,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
			//FORKM("a").get(MB,Demo_D.f5,Demo_D.f6,Demo_D.f7).END();
		}};
		println(cp.toString());
		
	}
	
	/**
	 * FORK模式测试
	 * 首个参数开启FORK模式，参数不匹配不开启
	 * 节点不匹配关闭该节点和后续节点FORK模式
	 * FORK节点如果没有参数，则该节点使用master节点的元素
	 */
	@Test
	public void test8() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).by(Demo_D.f10).END();
			//START("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).groupBy(Demo_D.f10).END();
			//START("a").get(Demo_D.f1,Demo_D.f2,Demo_D.f3,Demo_D.f4).END();
			
			FORKM("a").get(Demo_D.f5,Demo_D.f6,Demo_D.f7).by(MB,Demo_D.f8,Demo_D.f9).END();
			FORKM("a").get(MB,Demo_D.f5,Demo_D.f6,Demo_D.f7).by(Demo_D.f8,Demo_D.f9).END();
			//FORKM("a").get().by(Demo_D.f8,Demo_D.f9).END();
		}};
		println(cp.toString());
		
	}
	
	@Test
	public void test9() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a")
			.get(Demo_D.f1)
			.by(Demo_D.f10).eq(20)
			.and($(Demo_D.f9).eq(33).or(Demo_D.f8).eq(11))
			.or(Demo_D.f3,$(Demo_D.f5).eq(33).and(Demo_D.f6).eq(11)).END();
			
		}};
		println(cp.toString());
		println(cp.START("a").getJsonString());
		//println(cp.START("a").getJsonString());
	}
	 
	
	
}
