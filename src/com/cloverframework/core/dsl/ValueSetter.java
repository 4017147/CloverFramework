package com.cloverframework.core.dsl;

import com.cloverframework.core.data.Values;
import com.cloverframework.core.data.interfaces.CourseValues;
import com.cloverframework.core.dsl.interfaces.Accessable;
import com.cloverframework.core.exceptions.ArgsCountNotMatch;
import com.cloverframework.core.exceptions.ExceptionFactory;


public interface ValueSetter extends Accessable{


	default public CourseValues getValues() {
		return getThis().values.get();
	}
	
	/**
	 * 设置该节点的参数值，如name = ?中的参数，如果参数是基本类型，
	 * 建议使用value属性的基本类型方法，以减少装箱和类型转换的开销。<p>
	 * 参数是领域字段（字典，lambda，方法字面值），需要通过$输入参数:
	 * {@link CourseProxy#$(Object...)}来获取，否则一律作为值来对待。
	 * 多个参数，可传入这些参数的领域实体一次性完成，但是你的程序应当知道如何处理它们的关系，又如
	 * 参数是自定义值对象，你也许需要通过工具实现跟领域实体字段之间的匹配和复制，如beancopier，
	 * 但是一般情况下你无须使用自定义值对象，而是利用dsl构造关系即可。
	 * 无论输入是什么类型，在dsl中都不会负责字段检查和操作（除了获取字面值和判断参数个数），
	 * 如何检查和获取参数的规则由你的程序决定。
	 * @param <V>
	 * @throws ArgsCountNotMatch 检查当前节点字段参数跟值参数个数，值参数只能为1或者与之相等，否则抛出异常
	 */
	default public  AbstractCourse setValues(Object... values){
		AbstractCourse c = getThis();
			byte n = 0;
			int size = 0;
			int count = 0;
			try {
				for(Object o:values) {
					if(o instanceof AbstractCourse) {
						n++;
						size = size + ((AbstractCourse)o).getElements().length;
					}
					count = size+values.length-n;
				}
				if(AbstractCourse.ifCountValues) {
					//如果字段数>1则参数个数必须和字段数相等
					if(c.fields.size()>1 && count!=c.fields.size()) {
						throw new ArgsCountNotMatch(c.fields.size(),count);
						//如果字段数为1则至少有一个参数
					}else if(c.fields.size()<2 && count<1) {
						throw new ArgsCountNotMatch(c.fields.size(),count);
					}
				}else if(!AbstractCourse.ifCountValues && count<1) {
					throw new ArgsCountNotMatch(c.fields.size(),count);
				}
				c.values.remove();
				c.values.set(new Values(values));
			} catch (ArgsCountNotMatch e) {
				throw ExceptionFactory.wrapException("Error setting values in "+c.id+",cause：", e);
			}
			return c;
	}
	
	/**
	 * 传入一个构造完成的值对象设置为当前节点的values
	 * @param values
	 * @return
	 */
	default public AbstractCourse setValues(CourseValues values) {
		getThis().values.set(values);
		return getThis();
	}
	
	
	default public AbstractCourse setBoolean(boolean... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	default public AbstractCourse setByte(byte... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	default public AbstractCourse setShort(short... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	default public AbstractCourse setInt(int... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	default public AbstractCourse setLong(long... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}

	default public AbstractCourse setFloat(float... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}

	default public AbstractCourse setDouble(double... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	default public AbstractCourse setString(String... val) {
		getThis().setValues(new Values(val));
		return getThis();
	}
	
	
	

}
