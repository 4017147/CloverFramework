package com.cloverframework.core.util;

import java.util.HashSet;
import java.util.Set;

import com.cloverframework.core.course.Course;

public class ELOperation {
	/**
	 * 合并两个course节点Elements
	 * @param origin
	 * @param fork
	 */
	public static Object[] mergeElements(Object[] origin,Object[] fork,String model) {
		Object[] elements = null;
		if(model!=null) {
			//
			if(model==Course.M||model==Course.RM) {
				elements = new Object[origin.length+fork.length];
				int i = -1;
				int j = 0;
				if(model==Course.RM) {
					i = 0;
					j = -1;
				}
				for(Object obj:origin) {
					if(i+1<elements.length)
						elements[i+1] = obj;
					i++;
				}
				for(Object obj:fork) {
					if(j+1<elements.length)
						elements[j+1] = obj;
					j++;
				}
			}
			//
			if(model==Course.MA||model==Course.MB) {
				elements = new Object[origin.length+fork.length];
				int i = 0;
				for(Object obj:origin) {
					elements[i] = obj;
					i++;
				}
				for(Object obj:fork) {
					elements[i] = obj;
					i++;
				}
				if(model==Course.MB) {
					for(Object obj:fork) {
						elements[i] = obj;
						i++;
					}
					for(Object obj:origin) {
						elements[i] = obj;
						i++;
					}
				}
			}
			//
			if(model==Course.U) {
				Set<Object> set = new HashSet<>();
				for(Object obj:fork) 
					set.add(obj);
				for(Object obj:origin) 
					set.add(obj);
				elements = new Object[set.size()];
				int i = 0;
				for(Object obj:set) {
					elements[i] = obj;
					i++;
				}	
				set.clear();
			}
			//
			if(model==Course.I||model==Course.C||model==Course.CB||model==Course.CA) {
				
				Set<Object> u = new HashSet<>();
				Set<Object> i = new HashSet<>();
				Set<Object> a = new HashSet<>();
				Set<Object> b = new HashSet<>();
				Set<Object> c = null;
				Object[] l = null;
				Object[] s = null;
				if(origin.length<=fork.length) {
					s = origin;
					l = fork;
				}else {
					s = fork;
					l = origin;
				}
				for(Object obj:l) 
					u.add(obj);
				for(Object obj:s) {
					if(u.contains(obj))
					i.add(obj);
				} 
				if(model!=Course.I) {
					int k = 0;
					for(Object obj:i) {
						if(!i.contains(origin[k])) {
							b.add(obj);
						}
						if(!i.contains(fork[k])) {
							a.add(obj);
						}	
						k++;
					}
				}
				
				if(model==Course.I)
					c = i;
				if(model==Course.CB)
					c = b;
				if(model==Course.CA)
					c = a;
				if(model==Course.C) {
					b.addAll(a);
					c = b;
				}		
				
				elements = new Object[c.size()];
				int K = 0;
				for(Object obj:c) {
					elements[K] = obj;
					K++;
				}	
				u.clear();
				i.clear();
			}
		}
		return elements;
	}
}
