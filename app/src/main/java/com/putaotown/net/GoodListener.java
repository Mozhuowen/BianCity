package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.BaseActivity;
import com.putaotown.CreateTownActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.TownActivity;
import com.putaotown.net.objects.ResponseGood;
import com.putaotown.net.objects.ResponsePutao;
import com.putaotown.tools.LogUtil;

public class GoodListener implements Response.Listener<JSONObject>
{
	BaseActivity baseactivity;
	
	public GoodListener(BaseActivity b) {
		this.baseactivity = b;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.v("GoodListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
//				Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
			} else {											//成功
				Gson gson = new Gson();
				ResponseGood res = gson.fromJson(response.toString(), ResponseGood.class);				
				baseactivity.onGood(res.getGood());
			}
			baseactivity = null;
		} catch (JSONException e) {e.printStackTrace();}
		
	}
	
}