package com.tuxing.app.bean;

import java.io.Serializable;
import java.util.List;

public class SubjectBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int id;
		
	public String subject;
	
	public int num;
	
	public List<OptionBean> options;
	
}
