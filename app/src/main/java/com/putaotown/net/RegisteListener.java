package com.putaotown.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.RegisteActivity;
import com.putaotown.WelcomeActivity;
import com.putaotown.net.objects.ResponseLogin;
import com.putaotown.net.objects.ResponseRegiste;
import com.putaotown.tools.LogUtil;

public class RegisteListener implements Response.Listener<JSONObject> 
{
	Context context;
	Handler messhandler;
	
	public RegisteListener(Activity ac,Handler messhandler) {
		this.context = ac;
		this.messhandler = messhandler;
	}

	@Override
	public void onResponse(JSONObject response) {
		// TODO Auto-generated method stub
		LogUtil.v("RegisteListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
				
				if(context instanceof RegisteActivity) {		//回调activity
					Message mess = new Message();
					mess.what = 300;
					messhandler.sendMessage(mess);
				}
			} else {											//成功
				Gson gson = new Gson();
				ResponseRegiste resregiste = gson.fromJson(response.toString(), ResponseRegiste.class);
				
				((WelcomeActivity)context).onFinishRegiste(resregiste);
			}
			/**释放引用*/
			messhandler = null;
			context = null;
		} catch (JSONException e) {e.printStackTrace();}	
		
	}
	
}