package com.putaotown.net.objects;

import java.util.Calendar;

public class ModelPushComment
{
	private int userid;			//用户id
	private String usercover;	//用户头像
	private String username;	//用户名
	private String content;		//评论内容
	private int commentid;		//评论id
	private PackagePutao story;	//所属的故事
	private long time;			//时间
	private int retype;	//类型 0-评论 1-回复
	
	public void parse(PackagePutao s,PackageComment c) {
		this.usercover = c.getCover();
		this.userid = c.getUserid();
		this.username = c.getUsername();
		this.content = c.getContent();
		this.commentid = c.getCommentid();
		this.story = s;
	}
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsercover() {
		return usercover;
	}
	public void setUsercover(String usercover) {
		this.usercover = usercover;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCommentid() {
		return commentid;
	}
	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}
	public PackagePutao getStory() {
		return story;
	}
	public void setStory(PackagePutao story) {
		this.story = story;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getRetype() {
		return retype;
	}

	public void setRetype(int retype) {
		this.retype = retype;
	}
}