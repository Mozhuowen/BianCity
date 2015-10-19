package com.putaotown.net.objects;

public class ModelPushGood
{
	private int goodtype;	//赞类型 // 0-story 1-town 2-comment
	private int userid;		//点赞的人的id
	private String username;	//点赞的人的用户名
	private String usercover;	//点赞的人的头像
	private PackagePutao story;	//如果是赞故事或者评论该字段有值
	private ApplyTown town;		//如果赞边城该字段有值
	private long time;
	
	public int getGoodtype() {
		return goodtype;
	}
	public void setGoodtype(int goodtype) {
		this.goodtype = goodtype;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsercover() {
		return usercover;
	}
	public void setUsercover(String usercover) {
		this.usercover = usercover;
	}
	public PackagePutao getStory() {
		return story;
	}
	public void setStory(PackagePutao story) {
		this.story = story;
	}
	public ApplyTown getTown() {
		return town;
	}
	public void setTown(ApplyTown town) {
		this.town = town;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}