package com.putaotown.net;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.BaseTownShow;
import com.putaotown.PutaoApplication;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.objects.ModelHotTown;
import com.putaotown.net.objects.ModelNearTown;
import com.putaotown.net.objects.ResponseHotTown;
import com.putaotown.net.objects.ResponseSubscri;
import com.putaotown.tools.LogUtil;

public class LoadTownUtil
{
	public static void loadHot(final BaseTownShow bs,List<Integer> rejectid) {
		ModelHotTown req = new ModelHotTown();
		req.setRejectid(rejectid);
		
		RequestUtil.getInstance().post("gethot", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("LoadTownUtil info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								NetFailToast.show(response.getInt("errcode"));
							} else {											//成功
								Gson gson = new Gson();
								ResponseHotTown res = gson.fromJson(response.toString(), ResponseHotTown.class);				
								bs.onReceive(res.getTowns());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}		
				}
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());		
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
							bs.onReceive(null);
						}
					}
			});
	}
	
	public static void loadNear(final BaseTownShow bs,GeoInfo geo,List<Integer> rejectid) {
		ModelNearTown req = new ModelNearTown();
		req.setRejectid(rejectid);
		req.setGeo(geo);
		
		RequestUtil.getInstance().post("getnear", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("LoadTownUtil info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
							} else {											//成功
								Gson gson = new Gson();
								ResponseHotTown res = gson.fromJson(response.toString(), ResponseHotTown.class);				
								bs.onReceive(res.getTowns());
							}
						} catch (JSONException e) {e.printStackTrace();}
					}		
				}
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