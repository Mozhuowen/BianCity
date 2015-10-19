package com.putaotown.net.community;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.CommunitylistActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.community.models.ResCommunity;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.ResponseSubscri;
import com.putaotown.tools.LogUtil;

public class GetJoinCommunity
{
	public static void submit(final CommunitylistActivity context,int userid) {
		BaseRequest req = new BaseRequest();
		
		RequestUtil.getInstance().post("getcommunitylist",req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("GetJoinCommunity info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResCommunity res = gson.fromJson(response.toString(), ResCommunity.class);				
								context.onNetWorkFinish(res.getComunity());
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