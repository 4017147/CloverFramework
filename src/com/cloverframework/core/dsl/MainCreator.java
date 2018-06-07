package com.cloverframework.core.dsl;

import com.cloverframework.core.exceptions.CourseIsClosed;
import com.cloverframework.core.exceptions.ExceptionFactory;
import com.cloverframework.core.factory.CourseFactory;
import com.cloverframework.core.util.interfaces.CourseType;
import com.cloverframework.core.util.lambda.CreateMain;

/**
 * 主线节点创建器
 * @author yl
 *
 */
public interface MainCreator extends LiteralSetter{
	/**当前线程方法栈帧基准累计数，用于计算产生字面值的方法栈长是否合法*/
	int level = 0;
	
	/**
	 * 通过输入的节点创建函数表达式执行节点创建，如果相同位置的节点已存在
	 * 则返回已有节点
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
			while(old.isComplete.get()==false) {
				//已有节点是否构造完成
				System.out.println("waiting");
			}
			//NOTE 
			$();
			setLevel(previous,old);
			return old;
		}else {
			O newo = constructor.apply(previous, obj);
			if(newo.isComplete.compareAndSet(false, true)==false) {
				//
				System.out.println("isComletet");
			}
			$();
			setLevel(previous,newo);
			return newo;			
		}
	}

	/**
	 * 该方法尽量用在同一个方法中，因为栈长需要固定的数
	 * @param previous
	 * @param newc
	 */
	default void setLevel(AbstractCourse previous,AbstractCourse newc) {
		//int l = newc.level.get();
		if(previous.type!=CourseType.root) 
			beginLiteral(newc, level+2);
		else
			beginLiteral(newc, level+2);
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
		 cur = previous.proxy.receive(cur, rebase);
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
