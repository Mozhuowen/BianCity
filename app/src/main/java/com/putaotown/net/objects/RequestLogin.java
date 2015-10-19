package com.putaotown.net.objects;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.putaotown.PutaoApplication;

public class RequestLogin extends BaseRequest
{
	/**登录类型，0微博 1QQ 2边城帐号*/
	private int logintype;
	private String uid;	//微博是uid,qq是openid
	private String token;	
	private long expire;
	private String imei;	//手机唯一imei号
	private String sv;		//系统版本
	private String phonemodel;	//手机型号
	private String brand;	//手机品牌
	private String username;	//边城帐号
	private String password;	//边城帐号密码
	
	public RequestLogin() {
		this.imei = ((TelephonyManager)PutaoApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		this.phonemodel = android.os.Build.MODEL;
		this.sv = android.os.Build.VERSION.RELEASE;
		this.brand = android.os.Build.BRAND;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}