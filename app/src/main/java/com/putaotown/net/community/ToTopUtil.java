package com.putaotown.net.community;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.PutaoApplication;
import com.putaotown.community.ThemeTieActivity;
import com.putaotown.community.models.ModelSimple;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.RequestUtil;
import com.putaotown.tools.LogUtil;

public class ToTopUtil
{
	public static void submit(final ThemeTieActivity context,int zhulouid) {
		ModelSimple req = new ModelSimple();
		req.setZhulouid(zhulouid);
		
		RequestUtil.getInstance().post("totop", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("ToTopUtil Response TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因							
							} else {											//成功
								context.onToTop(true);
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