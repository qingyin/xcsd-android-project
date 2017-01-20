package com.tuxing.app.bean;

import java.io.Serializable;
import java.util.List;

public class TestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;
	
	public String name;
	
	public String description;
	
	public List<SubjectBean> subjests;
	
}
