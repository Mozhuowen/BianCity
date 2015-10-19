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
import com.putaotown.SublistActivity;
import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ResponseSubscri;
import com.putaotown.tools.LogUtil;

public class SubscriUtil
{
	/**获取当前用户对某town的订阅情况*/
	public static void getSubscri(BaseActivity ba,int townid){
		ModelSubscriTown req = new ModelSubscriTown();
		req.setTownid(townid);
		SubscriListener listener = new SubscriListener(ba);
		
		RequestUtil.getInstance().post("getsubscri", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
	/**订阅动作网络请求*/
	public static void doSubscri(BaseActivity ba,int townid,int action) {
		ModelSubscriTown req = new ModelSubscriTown();
		req.setTownid(townid);
		req.setAction(action);
		SubscriListener listener = new SubscriListener(ba);
		
		RequestUtil.getInstance().post("dosubscri", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
	/**获取用户订阅的列表*/
	public static void getsubslist(final SublistActivity sa,int userid) {
		BaseRequest req = new BaseRequest();
		RequestUtil.getInstance().post("getsubslist",req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("SuscriUtil info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResponseSubscri res = gson.fromJson(response.toString(), ResponseSubscri.class);				
								sa.onNetWorkFinish(res.getTowns());
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