package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.BaseActivity;
import com.putaotown.FavolistActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ResponsePutao;
import com.putaotown.net.objects.ResponseSubscri;
import com.putaotown.tools.LogUtil;

public class FavoriteUtil
{
	public static void getFavorite(BaseActivity ba,int putaoid) {
		ModelFavorite req = new ModelFavorite();
		req.setPutaoid(putaoid);
		FavoriteListener listener = new FavoriteListener(ba);
		RequestUtil.getInstance().post("getfavorite", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
	
	public static void doFavorite(BaseActivity ba,int putaoid,int action) {
		ModelFavorite req = new ModelFavorite();
		req.setPutaoid(putaoid);
		req.setAction(action);
		FavoriteListener listener = new FavoriteListener(ba);
		RequestUtil.getInstance().post("dofavorite", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
	
	public static void getFavolist(final FavolistActivity fa,int userid) {
		BaseRequest req = new BaseRequest();
		RequestUtil.getInstance().post("getfavolist",req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("getFavolist info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResponsePutao res = gson.fromJson(response.toString(), ResponsePutao.class);				
								fa.onNetworkFinish(res.getPutao());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}		
				}
				,new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							Log.e("TAG volleyResponseError", error.toString());		
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
						}
					}
			});
	}
}