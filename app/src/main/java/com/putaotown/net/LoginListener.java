package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.LoginActivity;
import com.putaotown.RegisteActivity;
import com.putaotown.WelcomeActivity;
import com.putaotown.net.objects.ResponseLogin;
import com.putaotown.tools.LogUtil;

public class LoginListener implements Response.Listener<JSONObject> 
{
	Context context;
	Handler messhandler;
	
	public LoginListener(Activity ac,Handler messhandler) {
		this.context = ac;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.v("LoginListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
				
				if(context instanceof LoginActivity) {		//回调activity
					Message mess = new Message();
					mess.what = 300;
					messhandler.sendMessage(mess);
				}
			} else {											//成功
				Gson gson = new Gson();
				ResponseLogin reslogin = gson.fromJson(response.toString(), ResponseLogin.class);
				
				((WelcomeActivity)context).onFinishLogin(reslogin);
			}
			/**释放引用*/
			messhandler = null;
			context = null;
		} catch (JSONException e) {e.printStackTrace();}	
		
	}
	
}