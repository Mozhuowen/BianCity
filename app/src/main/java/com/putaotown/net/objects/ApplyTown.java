package com.putaotown.net.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import com.putaotown.geographic.GeoInfo;

/**
 * 创建边城数据模型
 * @author awen
 *
 */
public class ApplyTown extends BaseRequest implements Serializable
{
	
	private int townid;			//边城id
	private String townname;	//边城名
	private String descri;		//边城简介
	private String cover;		//边城封面
	private String createtime;	//创建时间
	private int subscriptions = 0;	//订阅数
	private GeoInfo geoinfo;	//边城地理信息
	private int good;			//边城获赞数
	private boolean dosubscri;	//当前访问用户是否赞了此边城
	private int userid;			//边城创建者id
	private String username;	//边城创建者用户名
	private String usercover;	//边城创建者封面
	private int storycount;		//此边城包含的故事数
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
	public void setUserid(int u) {
		this.userid = u;
	}
	public int getUserid() {
		return this.userid;
	}
	public void setDosubscri(boolean s) {
		this.dosubscri = s;
	}
	public boolean getDosubscri(){
		return this.dosubscri;
	}
	public void setGood(int g) {
		this.good = g;
	}
	public int getGood() {
		return this.good;
	}
	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(int subscriptions) {
		this.subscriptions = subscriptions;
	}

	public void setTownid(int t) {
		this.townid = t;
	}
	public int getTownid() {
		return this.townid;
	}
	
	public String getCover() {
		return this.cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getTownname() {
		return townname;
	}
	public void setTownname(String townname) {
		this.townname = townname;
	}
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public GeoInfo getGeoinfo() {
		return geoinfo;
	}
	public void setGeoinfo(GeoInfo geoinfo) {
		this.geoinfo = geoinfo;
	}
	
	public String getCoordstr() {
		double lon_second = this.geoinfo.getLon_second();
		double lat_second = this.geoinfo.getLat_second();
		BigDecimal bg1 = new BigDecimal(lon_second);
		BigDecimal bg2 = new BigDecimal(lat_second);
		double lon_second_2 = bg1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		double lat_second_2 = bg2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		String tmpstr = "E"+geoinfo.getLon_degree()+"°"+geoinfo.getLon_minute()+"'"+lon_second_2+"''  "+"N"+geoinfo.getLat_degree()+"°"+geoinfo.getLat_minute()+"'"+lat_second_2+"''";
		return tmpstr;
	}
	public int getStorycount() {
		return storycount;
	}
	public void setStorycount(int storycount) {
		this.storycount = storycount;
	}
}