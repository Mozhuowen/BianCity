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
import com.putaotown.CreateTownActivity;
import com.putaotown.EditTownActivity;
import com.putaotown.TownActivity;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ResponsePutao;
import com.putaotown.tools.LogUtil;

public class GetPutaosListener implements Response.Listener<JSONObject> 
{
	Context context;
	Handler messhandler;
	
	public GetPutaosListener(Activity ac,Handler messhandler) {
		this.context = ac;
		this.messhandler = messhandler;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.v("GetPutaoListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);					//提示失败原因
				if(context instanceof CreateTownActivity) {		//回调activity
					Message mess = new Message();
					mess.what = 300;
					messhandler.sendMessage(mess);
				}
			} else {											//成功
				Gson gson = new Gson();
				ResponsePutao res = gson.fromJson(response.toString(), ResponsePutao.class);
				
				if(context instanceof TownActivity) {
					((TownActivity)context).onFinishNetwork(res.getPutao());
				}
			}
			/**释放引用*/
			messhandler = null;
			context = null;
		} catch (JSONException e) {e.printStackTrace();}
		
	}
	
}