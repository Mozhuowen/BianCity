package com.putaotown.net;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.putaotown.PutaoApplication;
import com.putaotown.localio.UserPreUtil;

import android.content.Context;

public class PutaoBaseNetwork
{
	public static final Context context = PutaoApplication.getContext();
//	public String host = "http://192.168.199.200";
//	public String host = "http://api.biancity.com:8080";
	public String host = "";
	public int ptuserid;
	public String username;
	public String ptoken;
	public static final RequestQueue mQueue = Volley.newRequestQueue(context);
	public void onFinishUpImage(UpImage upimage){};
	public void onFinishUpImage(boolean iserror){};
	
	public PutaoBaseNetwork() {
		ptuserid = UserPreUtil.getUserid();
		username = UserPreUtil.getUsername();
		ptoken = UserPreUtil.getToken();
	}
}