package com.putaotown.net.objects;

import java.util.List;
import java.util.Set;

public class ResponseLogin
{
	private boolean stat;
	private boolean needregiste;	//标记此次登录是否需要注册，如果无需注册以下字段将会有值
	private int errcode;
	private int ptuserid;			//此次登录用户的id
	private String name;			//此次登录用户名
	private String password;		//密码
	private String cover;			//用户头像
	private String ptoken;			//用户ptoken
	private int logintype = 0;		//登录类型
	private String uid;				//微博的uid或者qq的openid
	private List<ApplyTown> mytowns;	//该登录用户创建过的边城列表
	private String sex;				//性别
	private String location;		//地址
	private boolean needcname;		//此次登录后是否需要修改用户名，默认使用微博或者qq的用户名，如果在边城服务器中有重名则需要修改
	
	public void setNeedcname (boolean n) {
		this.needcname = n;
	}
	public boolean getNeedcname(){
		return this.needcname;
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
	public void setMytowns(List<ApplyTown> m) {
		this.mytowns = m;
	}
	public List<ApplyTown> getMytowns() {
		return this.mytowns;
	}	
	
	public boolean isStat() {
		return stat;
	}
	public void setStat(boolean stat) {
		this.stat = stat;
	}
	public boolean isNeedregiste() {
		return needregiste;
	}
	public void setNeedregiste(boolean needregiste) {
		this.needregiste = needregiste;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public int getPtuserid() {
		return ptuserid;
	}
	public void setPtuserid(int ptuserid) {
		this.ptuserid = ptuserid;
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
	public String getPtoken() {
		return ptoken;
	}
	public void setPtoken(String ptoken) {
		this.ptoken = ptoken;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}