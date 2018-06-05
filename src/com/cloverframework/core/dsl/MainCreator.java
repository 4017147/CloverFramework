package com.cloverframework.core.dsl;

import com.cloverframework.core.dsl.interfaces.Constant;
import com.cloverframework.core.exceptions.CourseIsClosed;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.factory.CourseFactory;
import com.cloverframework.core.util.lambda.CreateMain;

/**
 * 主线节点创建器接口
 * @author yl
 *
 */
public interface MainCreator extends Constant{

	/**
	 * 通过输入的节点创建函数表达式执行节点创建，如果相同位置的节点已存，
	 * 并且被认为和当前创建节点等价，则不会创建,如果相同位置的节点不存在或者不等价，
	 * 如果LOCKED，则进行rebase，如果不是LOCKED，则替换原有节点
	 * @param old
	 * @param constructor
	 * @param previous
	 * @param obj
	 * @return
	 */
	default <O extends AbstractCourse> O create(O old,CreateMain<O> constructor,AbstractCourse previous,Object ...obj) {
		if(previous.getStatus()<=END)
			throw ExceptionFactory.wrapException("Course create error,id:"+previous.getId(), new CourseIsClosed(previous.getType()));
		if(old!=null) {
			return old;
		}
		return constructor.apply(previous, obj);
	}

	/**
	 * 将一个正在进行对比的course的节点作为末端，创建一个fork类型的course,
	 * 并返回最后匹配成功的节点对应的新的course节点，如果正在调用的是子节点，
	 * 则从子节点开始返回,注意，该方法将忽略彼此的value一致性
	 * @param <P>
	 * @return
	 */
	 static AbstractCourse rebase(AbstractCourse previous) {
		 if(previous.isSon==true)
			 return previous;
		 String head = ((CourseProxy)previous.proxy).getCourse(previous.id).head;//优化
		 AbstractCourse cur = previous;
		 while(cur.previous!=null&&cur.previous.isSon==false) {
			 cur = cur.previous;
		 }
		 if(head!=null&&cur.head!=null&&cur.head.equals(head))
			return previous;
		 AbstractCourse next = cur.next;
		 cur = (AbstractCourse) previous.proxy.receive(cur, rebase);
		 while(next!=null&&next!=previous) {
			 cur.next = (AbstractCourse) CourseFactory.getConstructor(next.getType()).apply(cur, next.getArgs());
			 cur = cur.next;
			 next = next.next;
		 }
		 return cur;
	 	}
	
	 /**
	  * 如果两个id相同的course的head相等，那么认为是同一个course
	  * @param a
	  * @param b
	  * @return
	  */
	 static boolean equals(AbstractCourse a,AbstractCourse b) {
		 if(a==null||b==null)
			 return false;
		 while(a.previous!=null) {
			 a = a.previous;
		 }
		 while(b.previous!=null) {
			 b = b.previous;
		 }
		 if(a.head.equals(b.head)||a.id.equals(b.id)) 
			 return true;
		 else
			 return false;
	 }
	 
	 
}
