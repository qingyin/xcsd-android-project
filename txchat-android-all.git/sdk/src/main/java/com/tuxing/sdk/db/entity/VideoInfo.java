package com.tuxing.sdk.db.entity;

import java.io.Serializable;

public class VideoInfo implements Serializable{
	
	public String videoName;
	public long createOn;
	public String videoUrl;
	public long duration;

	public VideoInfo(String videoName, String videoUrl, long createOn, long duration){
		this.videoName = videoName;
		this.videoUrl = videoUrl;
		this.createOn = createOn;
		this.duration = duration;
	}
}
