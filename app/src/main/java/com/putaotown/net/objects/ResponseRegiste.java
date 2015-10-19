package com.putaotown.net.objects;

import java.util.List;

public class ResponseRegiste
{
	private boolean stat;
	private boolean needchangename; //此次注册后是否需要修改用户名，默认使用微博或者qq的用户名，如果在边城服务器中有重名则需要修改
	private int errcode;
	private String name;
	private String cover;
	private String uid;
	private List<ApplyTown> mytowns;
	private String sex;
	private String location;	
	private int ptuserid;	//注册边城帐号时需要用到这两个
	private String ptoken;	//注册边城帐号时需要用到这两个
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
	public boolean isStat() {
		return stat;
	}
	public void setStat(boolean stat) {
		this.stat = stat;
	}
	public boolean isNeedchangename() {
		return needchangename;
	}
	public void setNeedchangename(boolean needchangename) {
		this.needchangename = needchangename;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<ApplyTown> getMytowns() {
		return mytowns;
	}
	public void setMytowns(List<ApplyTown> mytowns) {
		this.mytowns = mytowns;
	}
	public int getPtuserid() {
		return ptuserid;
	}
	public void setPtuserid(int ptuserid) {
		this.ptuserid = ptuserid;
	}
	public String getPtoken() {
		return ptoken;
	}
	public void setPtoken(String ptoken) {
		this.ptoken = ptoken;
	}
	
}