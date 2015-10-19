package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.MessboardActivity;
import com.putaotown.PutaoxActivity;
import com.putaotown.net.objects.ResponseComment;
import com.putaotown.net.objects.ResponseMess;
import com.putaotown.tools.LogUtil;

public class SubmitMessListener implements Response.Listener<JSONObject>
{
	Context context;
	Handler messhandler;
	
	public SubmitMessListener(Activity ac,Handler messhandler) {
		this.context = ac;
		this.messhandler = messhandler;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.d("SubmitMessListener Listener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
			} else {											//成功
				Gson gson = new Gson();
				ResponseMess res = gson.fromJson(response.toString(), ResponseMess.class);
				((MessboardActivity)context).onFinishNetwork(res.getMess());
			}
			/**释放引用*/
			messhandler = null;
			context = null;
		} catch (JSONException e) {e.printStackTrace();}
	}
	
}