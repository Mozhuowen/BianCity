package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.putaotown.CNameActivity;
import com.putaotown.tools.LogUtil;

public class CNameListener implements Response.Listener<JSONObject> 
{
	Context context;
	
	public CNameListener(Activity ac) {
		this.context = ac;
	}
	@Override
	public void onResponse(JSONObject response) {
		LogUtil.v("CNameListener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				((CNameActivity)context).onFail();
//				Toast.makeText(context,NetFailToast.NAME_EXIST, Toast.LENGTH_SHORT).show();
			} else {											//成功				
				((CNameActivity)context).onSuccess();
			}
			/**释放引用*/
			context = null;
		} catch (JSONException e) {e.printStackTrace();}	
		
	}
	
}