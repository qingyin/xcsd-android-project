package com.xcsd.app.teacher.data;

import java.io.Serializable;
import java.util.List;

/**
 * 班级类
 * 
 */

public class Classes implements Serializable{
	
	private static final long serialVersionUID = 6544048425180247798L;
	public int head;
	public String user;
	public int count;
	public boolean hasChild;
	public boolean isGroupMembers; // 教师组
	public List<Clazz> clazzList;
}
