package com.putaotown.net.objects;

import java.io.Serializable;

public class PackageComment extends BaseRequest
{
	private int townid;	//边城id
	private int putaoid;	//故事id
	private String content;	//评论内容
	private String username;	//评论者用户名
	private String cover;	//评论者头像
	private String time;	//评论时间
	private int goods;		//评论获赞数
	private boolean dogood;	//当前用户是否点赞
	private int commentid;	//评论id
	private int commentposition;	//获取下一条评论开始的位置,第一次获取为0,第二次获取为15,依次类推
	private int userid;		//评论者id
	private int replyid;	//被回复评论的id
	private String replyname;	//被回复者用户名
	public void setUserid(int u) {
		this.userid = u;
	}
	public int getUserid() {
		return this.userid;
	}
	public void setCommentposition(int c) {
		this.commentposition = c;
	}
	public int getCommentposition() {
		return this.commentposition;
	}
	public void setCommentid(int c) {
		this.commentid = c;
	}
	public int getCommentid() {
		return this.commentid;
	}
	public int getTownid() {
		return townid;
	}
	public void setTownid(int townid) {
		this.townid = townid;
	}
	public int getPutaoid() {
		return putaoid;
	}
	public void setPutaoid(int putaoid) {
		this.putaoid = putaoid;
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
	public boolean isDogood() {
		return dogood;
	}
	public void setDogood(boolean dogood) {
		this.dogood = dogood;
	}
	public int getReplyid() {
		return replyid;
	}
	public void setReplyid(int replyid) {
		this.replyid = replyid;
	}
	public String getReplyname() {
		return replyname;
	}
	public void setReplyname(String replyname) {
		this.replyname = replyname;
	}
}