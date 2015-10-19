package com.putaotown.net.community;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.community.Community;
import com.putaotown.community.models.ModelSimple;
import com.putaotown.community.models.ResGetTieTh;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.PutaoBaseNetwork;
import com.putaotown.net.RequestUtil;
import com.putaotown.tools.LogUtil;

public class JoinBBS extends PutaoBaseNetwork
{
	public static void join(final Community activity,int communityid) {
		ModelSimple req = new ModelSimple();
		req.setCommunityid(communityid);
		
		RequestUtil.getInstance().post("joincommunity", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("JoinBBS Response TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因							
							} else {											//成功
								activity.onJoinBBS(true);
							}
						} catch (JSONException e) {e.printStackTrace();}
					}}
				, new Response.ErrorListener(){
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