package com.test;
import static com.test.SYSOUT.println;
import com.cloverframework.core.dsl.Course;
import com.cloverframework.core.dsl.CourseProxy;
import com.dict.entity.Demo_D;
import com.entity.Demo;
import com.entity.User;

public class ThreadCourseProxyTest {
	static Demo demo = new Demo();
	/**
	 * 多线程安全测试
	 * 值和结果线程独立
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		CourseProxy<User,Course> cp1 = new CourseProxy<User,Course>();
		 demo= cp1.getStaple(Demo.class);
		 demo.setF5("f5");
		 demo.setF6("f6");
		 demo.setF7("f7");
		
			CourseProxy<User,Course> cp = new CourseProxy<User,Course>(new CourseProxyTest()) {{
				Thread t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						Master("a1").get(demo.getF9(),count(demo.getF2()),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
						.by(Demo_D.f10,demo.getF10()).eq(40,$(demo.getF5())).and(demo.getF5(),$(demo.getF5())).eq(1,2).and(Demo_D.f3);
						//System.out.println("t1"+getCourse("a1"));
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						println("t1"+getCourse("a1"));
						println("t1"+getCourse("a1").get().by().getValues().toString());
					}
				});
				
				Thread t2 = new Thread(new Runnable() {
					@Override
					public void run() {
						//Course b = Master("b");
						Master("a1").get(demo.getF7(),count(demo.getF2()),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
						.by(Demo_D.f10,demo.getF10()).eq(50,$(demo.getF6())).and(demo.getF6(),$(demo.getF6())).eq(3,4).and(Demo_D.f3);
						println("t2"+getCourse("a1"));
						
						Master("a1").get(demo.getF7(),count(demo.getF2()),Demo_D.f1,Demo_D.f4,count(Demo_D.f3))
						.by(Demo_D.f10,demo.getF10()).eq(50,$(demo.getF6())).and(demo.getF6(),$(demo.getF6())).eq(3,4).and(Demo_D.f3);
						println("t2"+getCourse("a1"));
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						println("t2"+getCourse("a1").get().by().getValues().toString());
					}
				});
				
				t2.start();
				t1.start();
				//Thread.sleep(1000);
			}}; 
	}
}
