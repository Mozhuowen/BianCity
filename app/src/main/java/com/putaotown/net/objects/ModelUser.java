package com.putaotown.net.objects;

import java.io.Serializable;
import java.util.List;
/**
 * 用户基本信息
 * @author awen
 *
 */
public class ModelUser extends BaseRequest implements Serializable
{
	private String name;		//用户名
	private String cover;		//用户头像
	private List<ApplyTown> mytowns;	//该用户创建的边城列表
	private String sex;			//用户性别
	private String location;	//用户地址
	private int fans;			//粉丝数
	private int subscricount;	//订阅数
	private int favoritecount;	//收藏数
	private int begoodcount;	//被赞数
	private int towncount;		//创建小镇数
	private int putaocount;		//创建葡萄数
	private int bbscount;	//加入的社区数量
	private int userid;			//目标用户的ptuserid
	private boolean onlystatis = false;	//是否只获取统计数据
	private String wallimage;	//墙纸
	
	public ModelUser(int userid,String name,String cover) {
		this.userid = userid;
		this.name = name;
		this.cover = cover;
	}
	
	public ModelUser() {}
	
	public void setWallimage(String w) {
		this.wallimage = w;
	}
	public String getWallimage() {
		return this.wallimage;
	}
	public void setOnlystatis(boolean o) {
		this.onlystatis = o;
	}
	public boolean getOnlystatis() {
		return this.onlystatis;
	}
	public void setUserid(int u) {
		this.userid = u;
	}
	public int getUserid() {
		return this.userid;
	}
	public int getSubscricount() {
		return subscricount;
	}
	public void setSubscricount(int subscricount) {
		this.subscricount = subscricount;
	}
	public int getFavoritecount() {
		return favoritecount;
	}
	public void setFavoritecount(int favoritecount) {
		this.favoritecount = favoritecount;
	}
	public int getBegoodcount() {
		return begoodcount;
	}
	public void setBegoodcount(int begoodcount) {
		this.begoodcount = begoodcount;
	}
	public int getTowncount() {
		return towncount;
	}
	public void setTowncount(int towncount) {
		this.towncount = towncount;
	}
	public int getPutaocount() {
		return putaocount;
	}
	public void setPutaocount(int putaocount) {
		this.putaocount = putaocount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public List<ApplyTown> getMytowns() {
		return mytowns;
	}
	public void setMytowns(List<ApplyTown> mytowns) {
		this.mytowns = mytowns;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getFans() {
		return fans;
	}
	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getBbscount() {
		return bbscount;
	}

	public void setBbscount(int bbscount) {
		this.bbscount = bbscount;
	}
}