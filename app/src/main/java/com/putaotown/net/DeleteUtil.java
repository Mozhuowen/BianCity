package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.BaseActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.net.objects.ModelDelete;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.net.objects.ResponseUser;
import com.putaotown.tools.LogUtil;

public class DeleteUtil
{
	public static void delete(final BaseActivity ba,int type,int id) {
		ModelDelete req = new ModelDelete();
		req.setType(type);
		req.setId(id);
		
		RequestUtil.getInstance().post("invisible"
				,req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("DeleteUtilListener TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResponseSimple res = gson.fromJson(response.toString(), ResponseSimple.class);				
								ba.onDelete(res);
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