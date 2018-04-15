package com.cloverframework.core.util.json;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author yl
 *
 */
public abstract class Jsonable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private Set<String> types;
	private List<String> fields;
	private String optype;
	private String values;
	private List<? extends Jsonable> son;
	private Jsonable next;
	
	public Jsonable(String type, String optype, List<String> fields, Set<String> types, String values,
			List<? extends Jsonable> son, Jsonable next) {
		super();
		this.type = type;
		this.optype = optype;
		this.fields = fields;
		this.types = types;
		this.values = values;
		this.son = son;
		this.next = next;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public List<? extends Jsonable> getSon() {
		return son;
	}

	public void setSon(List<Jsonable> son) {
		this.son = son;
	}

	public Jsonable getNext() {
		return next;
	}

	public void setNext(Jsonable next) {
		this.next = next;
	}


	




}
