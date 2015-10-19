package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.UserActivity;
import com.putaotown.UserInterface;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.ResponseUser;
import com.putaotown.tools.LogUtil;

public class UserUtil
{
	public static void getUserInfo(final UserInterface ac,ModelUser user) {
		
		RequestUtil.getInstance().post("getuserinfo"
				,user
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.d("UserUtilListener TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
							} else {											//成功
								Gson gson = new Gson();
								ResponseUser res = gson.fromJson(response.toString(), ResponseUser.class);				
								ac.onNetWorkFinish(res.getUser());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}
					
				}
				,new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());	
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
						}
					}
			});
	}
	
	public static void CUserInfo(final UserInterface ac,ModelUser user) {
		
		RequestUtil.getInstance().post("cuserinfo"
				,user
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.d("UserUtilListener TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), NetFailToast.show(response.getInt("errcode")), Toast.LENGTH_SHORT).show();
								NetFailToast.show(response.getInt("errcode"));
							} else {											//成功
								Gson gson = new Gson();
								ResponseUser res = gson.fromJson(response.toString(), ResponseUser.class);				
								ac.onNetWorkFinish(res.getUser());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}
					
				}
				,new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());	
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
						}
					}
			});
	}
}