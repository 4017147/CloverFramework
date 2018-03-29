package com.cloverframework.core.util;

import java.util.HashSet;
import java.util.Set;

import com.cloverframework.core.course.CourseProxy;

public class ELOperation {
	
	public static Object[] shorter(Object[] a,Object[] b) {
		if(a.length<=b.length) 
			return a;
		else
			return b;
	}
	
	public static Object[] longer(Object[] a,Object[] b) {
		if(a.length<=b.length) 
			return b;
		else
			return a;
	}
	
	/**
	 * 合并两个course节点Elements
	 * @param origin
	 * @param fork
	 */
	public static Object[] mergeElements(Object[] origin,Object[] fork,String model) {
		Object[] elements = null;
		if(model!=null) {
			//
			if(model==CourseProxy.M||model==CourseProxy.RM) {
				elements = new Object[origin.length+fork.length];
				int i = 0,j = 0;
				
				if(origin.length<=fork.length && model!=CourseProxy.RM||origin.length>fork.length && model==CourseProxy.RM )
					j = 1;	
				else
					i = 1;
				
				Object[] l = longer(origin,fork);
				Object[] s = shorter(origin,fork);
				
				for(Object obj:s) {
					if(i+1<elements.length)
						elements[i] = obj;
					i+=2;
				}
				for(Object obj:l) {
					if(j<elements.length)
						elements[j] = obj;
					if(j>i-2) {
						j++;
					}else {
						j+=2;						
					}
				}
			}
			//
			if(model==CourseProxy.MA||model==CourseProxy.MB) {
				elements = new Object[origin.length+fork.length];
				int i = 0;
				if(model==CourseProxy.MA) {
					for(Object obj:origin) {
						elements[i] = obj;
						i++;
					}
					for(Object obj:fork) {
						elements[i] = obj;
						i++;
					}					
				}
				if(model==CourseProxy.MB) {
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
			if(model==CourseProxy.U || model==CourseProxy.UB || model==CourseProxy.UA) {
				Set<Object> set = new HashSet<>();
				Set<Object> a = new HashSet<>();
				Set<Object> b = new HashSet<>();
				for(Object obj:fork) {
					set.add(obj);
					a.add(obj);
				} 
				for(Object obj:origin) {
					set.add(obj);
					b.add(obj);
				}
				if(model==CourseProxy.U)
					elements = set.toArray();	
				set.clear();
			}
			
			//
			if(model==CourseProxy.I||model==CourseProxy.C||model==CourseProxy.CB||model==CourseProxy.CA) {
				
				Set<Object> u = new HashSet<>();
				Set<Object> i = new HashSet<>();
				Set<Object> a = new HashSet<>();
				Set<Object> b = new HashSet<>();
				Set<Object> c = null;
				Object[] l = longer(origin,fork);
				Object[] s = shorter(origin,fork);
				for(Object obj:l) 
					u.add(obj);
				for(Object obj:s) {
					if(u.contains(obj))
					i.add(obj);
				} 
				if(model!=CourseProxy.I) {
					int k = 0;
					for(@SuppressWarnings("unused") Object obj:l) {
						if(!i.contains(l[k])) {
							b.add(l[k]);
						}
						if(k<s.length && !i.contains(s[k])) {
							a.add(s[k]);
						}	
						k++;
					}
				}
				
				if(model==CourseProxy.I)
					c = i;
				if(model==CourseProxy.CB)
					c = b;
				if(model==CourseProxy.CA)
					c = a;
				if(model==CourseProxy.C) {
					b.addAll(a);
					c = b;
				}		
				elements = c.toArray();
				u.clear();
				i.clear();
			}
		}
		return elements;
	}
}
