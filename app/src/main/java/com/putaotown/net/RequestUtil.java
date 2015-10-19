package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.objects.BaseRequest;
import com.putaotown.tools.LogUtil;

public class RequestUtil extends PutaoBaseNetwork
{
	private static RequestUtil instance;
	
	public static RequestUtil getInstance() {
		if (instance == null ){
			instance = new RequestUtil();
		}
		return instance;
	}
	
	public void post(String action,Object postObject,Response.Listener<JSONObject> listener,Response.ErrorListener onError) {
		JSONObject jobj = null;
		BaseRequest targetreq = (BaseRequest)postObject;
		targetreq.setPtoken(UserPreUtil.getPtoken());
		targetreq.setPtuserid(UserPreUtil.getUserid());
		try {
			jobj = new JSONObject(new Gson().toJson(postObject));
			LogUtil.v("RequestUtil obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		LogUtil.v("RequestUtil info: ", "url:"+ host + "/" + action);
		//构造请求
		BCRequest jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/" + action
				,jobj
				,listener
				,onError);
		mQueue.add(jsonObjectRequest);
	}
}