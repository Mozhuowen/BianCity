package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.TownActivity;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.ResponseUser;
import com.putaotown.tools.LogUtil;

public class FansUtil
{
	public static void getFans(final TownActivity ta,int userid){
		ModelUser user = new ModelUser();
		user.setUserid(userid);
		
		RequestUtil.getInstance().post("getfans"
				,user
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("FansListener TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResponseUser res = gson.fromJson(response.toString(), ResponseUser.class);				
								ta.onFans(res.getUser());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}
					
				}
				,new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
}