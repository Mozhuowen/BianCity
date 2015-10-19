package com.putaotown.net.objects;

import com.putaotown.localio.UserPreUtil;
/**
 * 所有请求数据模型的基类，仅包含token和id
 * @author awen
 *
 */
public class BaseRequest
{
	private String ptoken;	//用户token
	private int ptuserid;	//用户id
	
	public BaseRequest() {
		this.ptoken = UserPreUtil.getPtoken();
		this.ptuserid = UserPreUtil.getUserid();
	}
	
	public void setPtoken(String t) {
		this.ptoken = t;
	}
	public String getToken() {
		return this.ptoken;
	}
	public void setPtuserid(int u) {
		this.ptuserid = u;
	}
	public int getPtuserid() {
		return this.ptuserid;
	}
}