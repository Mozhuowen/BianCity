package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.BaseActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.net.objects.ResponseFavorite;
import com.putaotown.tools.LogUtil;

public class FavoriteListener implements Response.Listener<JSONObject>
{
	BaseActivity baseactivity;
	
	public FavoriteListener(BaseActivity b) {
		this.baseactivity = b;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.v("FavoriteListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
//				Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
			} else {											//成功
				Gson gson = new Gson();
				ResponseFavorite res = gson.fromJson(response.toString(), ResponseFavorite.class);				
				baseactivity.onFavorite(res.getFavori());
			}
			/**释放引用*/
			baseactivity = null;
		} catch (JSONException e) {e.printStackTrace();}
	}
	
}