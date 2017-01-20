package com.tuxing.app.domain;


import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import java.io.Serializable;

public class Conversation implements Serializable, Comparable<Conversation>{

	@Override
	public int compareTo(Conversation another) {
		if (latestTime > another.latestTime){
			return -1;
		}else if(latestTime < another.latestTime){
			return 1;
		}else{
			return 0;
		}
	}

	public enum TYPE{
		CHAT,
		GROUP_CHAT,
		LEARN_GARDEN,
		NOTICE,
		CHECK_IN,
		COMMUNION,
		HOMEWORK
	}

	public long id;
	public String name;
	public String avatar;
	public int defaultAvatar;
	public String desc;
	public int  count;
	public TYPE type;
	public long chatId;
	public long latestTime;
	public String showTime;
	public EMMessage.Status msgStatus;
	public EMConversation emConversation;

	public Conversation(String name, TYPE type){
		this.name = name;
		this.type = type;
	}

}
