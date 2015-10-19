package com.putaotown.net.objects;

import com.putaotown.PutaoApplication;

import android.content.Context;
import android.telephony.TelephonyManager;

public class RequestRegiste extends BaseRequest
{
	public int logintype;	//注册方式 0-微博 1-qq 2-边城帐号
	public ModelRegisteWb registInfo;
	public ModelRegisteQQ registqqInfo;
	public String username;		//用户名
	public String password;		//密码
	private String imei;	//手机唯一imei号
	private String sv;		//系统版本
	private String phonemodel;	//手机型号
	private String brand;	//手机品牌
	
	public void setRegistQqInfo(ModelRegisteQQ info) {
		this.registqqInfo = info;
	}
	public ModelRegisteQQ getRegistQqInfo() {
		return this.registqqInfo;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	public ModelRegisteWb getRegistInfo() {
		return registInfo;
	}
	public void setRegistInfo(ModelRegisteWb registInfo) {
		this.registInfo = registInfo;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getSv() {
		return sv;
	}
	public void setSv(String sv) {
		this.sv = sv;
	}
	public String getPhonemodel() {
		return phonemodel;
	}
	public void setPhonemodel(String phonemodel) {
		this.phonemodel = phonemodel;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	
}