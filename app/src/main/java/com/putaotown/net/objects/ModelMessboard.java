package com.putaotown.net.objects;

import com.putaotown.geographic.GeoInfo;

public class ModelMessboard extends BaseRequest
{
	private int townid;		//边城id
	private String content;	//留言内容
	private String username;	//留言作者用户名
	private String cover;	//留言者头像
	private String time;	//留言时间
	private int goods;		//留言获赞数
	private boolean dogood;	//当前用户是否对该留言点赞
	private int messid;		//留言id
	private int messposition;	//加载下一条留言开始的位置
	private GeoInfo geo;	//评论者的地理信息，提交评论时用.
	private int userid;		//评论的用户的id
	public void setUserid(int u) {
		this.userid = u;
	}
	public int getUserid() {
		return this.userid;
	}
	public int getTownid() {
		return townid;
	}
	public void setTownid(int townid) {
		this.townid = townid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getGoods() {
		return goods;
	}
	public void setGoods(int goods) {
		this.goods = goods;
	}
	public boolean getDogood() {
		return dogood;
	}
	public void setDogood(boolean dogood) {
		this.dogood = dogood;
	}
	public int getMessid() {
		return messid;
	}
	public void setMessid(int messid) {
		this.messid = messid;
	}
	public int getMessposition() {
		return messposition;
	}
	public void setMessposition(int messposition) {
		this.messposition = messposition;
	}
	public GeoInfo getGeo() {
		return geo;
	}
	public void setGeo(GeoInfo geo) {
		this.geo = geo;
	}
}