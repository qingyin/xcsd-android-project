package com.tuxing.app.bean;

import java.io.Serializable;

public class EvaluationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int id;
	
	public String childPic;
	
	public int testSerialNumber;

	public String description;
	
	public int evaluationType;

	public SocialArticleBean socialArticle;

	public AuthorityBean authority;

}
