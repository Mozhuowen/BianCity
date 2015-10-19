package com.putaotown.community.models;

import java.util.List;

import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.Image;
import com.putaotown.net.objects.ModelUser;
/**
 * 快速回复数据模型
 * @author awen
 *
 */
public class ModelTieReply extends BaseRequest
{
	private int parentie;	//隶属那个帖子的id
	private int tieid;		//帖子id
	private int userid;		//用户id
	private String username;	//用户名
	private String usercover;	//用户头像
	private long time;		//发表时间
	private String content;		//回复内容
	private int bereplyid;	//被回复贴的id
	public int getTieid() {
		return tieid;
	}
	public void setTieid(int tieid) {
		this.tieid = tieid;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getParentie() {
		return parentie;
	}
	public void setParentie(int parentie) {
		this.parentie = parentie;
	}
	public int getBereplyid() {
		return bereplyid;
	}
	public void setBereplyid(int bereplyid) {
		this.bereplyid = bereplyid;
	}
}