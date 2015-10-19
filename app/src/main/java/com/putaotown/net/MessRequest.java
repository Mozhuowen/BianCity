package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.objects.ModelMessboard;
import com.putaotown.tools.LogUtil;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessRequest extends PutaoBaseNetwork
{
	final Activity activity;
	private Handler messhandler;
	private int townid;
	private String content;
	private int messposition;
	private GeoInfo geo;
	private BCRequest jsonObjectRequest;
	
	public MessRequest(Activity ac,Handler handler,int townid,String content,int messposition,GeoInfo geo){
		this.activity = ac;
		this.messhandler = handler;
		this.townid = townid;
		this.content = content;
		this.messposition = messposition;
		this.geo = geo;
	}
	
	public void submitMess(){
		
	}
	
	public void loadMoreMess(int position) {
		
	}
	
	public void loadMess() {
		ModelMessboard mess = new ModelMessboard();
		mess.setPtoken(ptoken);
		mess.setPtuserid(ptuserid);
		mess.setTownid(townid);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(mess));
			LogUtil.d("messRequest obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();return;}
		
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/getmess"
				,jobj
				,new SubmitMessListener(activity,messhandler)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					LogUtil.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
}