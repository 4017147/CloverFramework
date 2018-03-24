package com.test;

import static com.test.SYSOUT.println;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.cloverframework.core.course.Course.Get;
import com.cloverframework.core.domain.DomainService;
import com.cloverframework.core.domain.annotation.Domain;
import com.cloverframework.core.course.CourseProxy;
import com.cloverframework.core.factory.EntityFactory;
import com.dict.entity.Demo_D;
import com.entity.Demo;

@Domain("Demo")
public class CourseProxyTest implements DomainService{
	private static Demo demo;

	@BeforeClass
	public static void setup() {
		 demo= EntityFactory.getInstance().getStaple(Demo.class);
		 demo.setField5("f5");
		 demo.setField6("f6");
		 demo.setField7("f7");
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
	//@Test
	public void test1() {
		Demo de = new Demo();
		de.getField10();de.getField9();
		CourseProxy cp = new CourseProxy(this);
		cp.START("a").get(cp.$(),
				demo.getField1(),
				demo.getField2(),
				demo.getField3(),
				demo.getField4()).by(
						demo.getField5(),
						demo.getField6()).LIMIT(0, 10).END();
		
		Get get = cp.START("b").get(cp.$(),
				demo.getField1(),
				demo.getField2(),
				demo.getField3(),
				demo.getField4());
		de.getField10();de.getField9();
		get.by(demo.getField5(),
				demo.getField6()).LIMIT(0, 10).END();
		println(cp.toString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典
	 */
	//@Test
	public void test2() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get($(),
					demo.getField1(),
					demo.getField2(),
					demo,demo,
					Demo_D.field3,
					Demo_D.field4).by($(),
							demo.getField1(),
							demo.getField2(),
							demo,demo,
							Demo_D.field3,
							Demo_D.field4).END();
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
					$(demo::getField5,demo::getField6),
					Demo_D.field1,
					Demo_D.field2,
					demo.getField3(),
					demo.getField4(),
					demo,demo).by(
							demo,
							Demo_D.field1,
							Demo_D.field2).END();
		}};
		println(cp.toString());
		println(cp.START("b").toJSONString());
	}
	
	/**
	 * 构造测试,输出测试
	 * 内部类
	 * 实体方法+实体对象+字典+lambda
	 * 三元表达式
	 */
	//@Test
	public void test4() {
		CourseProxy cp = new CourseProxy(this) {{
			START("b").get(
					Demo_D.field1,
					$(demo::getField5,demo::getField6,demo::getField9),
					demo.getField7(),
					demo.getField8(),
					$(demo!=null?demo::getField4:demo::getField3),
					$(demo==null?demo::getField4:demo::getField3),
					te(demo.getField5()==null?demo.getField3():demo.getField4()),
					te(demo.getField5()!=null?demo.getField3():demo.getField4()),
					Demo_D.field2,
					demo,demo
					).END();
		}};
		println(cp.toString());
	}
	
	
	/**
	 * sharespace区测试
	 */
	//@Test
	public void test5() {
		CourseProxy cp = new CourseProxy(this) {{
			START("a").get(Demo_D.field1,Demo_D.field2).END();
			START("b").get(Demo_D.field3,Demo_D.field4).END();
			START("c").get(Demo_D.field5,Demo_D.field6).END();
		}};
		println(cp.toString());
	}
	
}
