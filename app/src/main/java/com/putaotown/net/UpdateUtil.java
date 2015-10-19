package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.MainActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.SettingActivity;
import com.putaotown.UpdateCallBack;
import com.putaotown.fragment.PersonFragment;
import com.putaotown.net.objects.ModelAppUpdate;
import com.putaotown.net.objects.ResponseVersion;
import com.putaotown.tools.LogUtil;

public class UpdateUtil
{
	public static void checkUpdate(final UpdateCallBack callback) throws NameNotFoundException {
		ModelAppUpdate req = new ModelAppUpdate();
		int currVersionCode = PutaoApplication.getContext().getPackageManager().getPackageInfo(PutaoApplication.getContext().getPackageName(), 0).versionCode;
		req.setVersioncode(currVersionCode);
		
		RequestUtil.getInstance().post("update", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("UpdateUtilListener TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
							} else {											//成功
								Gson gson = new Gson();
								ResponseVersion res = gson.fromJson(response.toString(), ResponseVersion.class);
								callback.onReceive(res.getVersion());
								
							}
						} catch (JSONException e) {e.printStackTrace();}
					}				
				}
				, new Response.ErrorListener(){
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