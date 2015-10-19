package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.searchActivity;
import com.putaotown.net.objects.ModelKeyWord;
import com.putaotown.net.objects.ResKeyWord;
import com.putaotown.net.objects.ResSearch;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;

public class SearchUtil
{
	public static void getkeyword(final searchActivity context,String keyword) {
		ModelKeyWord req = new ModelKeyWord();
		req.setKeyword(keyword);
		
		RequestUtil.getInstance().post("getkeyword"
				,req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("SearchUtil TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResKeyWord res = gson.fromJson(response.toString(), ResKeyWord.class);				
								context.onGetKeyWords(res.getWords());
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
	
	public static void searchTown(final searchActivity context,String keyword) {
		ModelKeyWord req = new ModelKeyWord();
		req.setKeyword(keyword);
		
		RequestUtil.getInstance().post("searchtown"
				,req
				,new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("SearchUtil TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResSearch res = gson.fromJson(response.toString(), ResSearch.class);				
								context.onSearchTown(res.getTowns());
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