package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.putaotown.PutaoActivity;
import com.putaotown.PutaoxActivity;
import com.putaotown.net.objects.ResponseComment;
import com.putaotown.tools.LogUtil;

public class SubmitCommentListener implements Response.Listener<JSONObject>
{
	Context context;
	Handler messhandler;
	
	public SubmitCommentListener(Activity ac,Handler messhandler) {
		this.context = ac;
		this.messhandler = messhandler;
	}

	@Override
	public void onResponse(JSONObject response) {
		LogUtil.d("SubmitComment Listener TAG", response.toString());
		try {
			boolean stat = response.getBoolean("stat");
			if (!stat) {										//失败
				int errorcode = response.getInt("errcode");		//获取失败码
				NetFailToast.show(errorcode);		//提示失败原因
			} else {											//成功
				Gson gson = new Gson();
				ResponseComment res = gson.fromJson(response.toString(), ResponseComment.class);
				if (context instanceof PutaoxActivity)
					((PutaoxActivity)context).onFinishNetwork(res.getComments());
				else
					Toast.makeText(context, "回复成功", Toast.LENGTH_SHORT).show();
			}
			/**释放引用*/
			messhandler = null;
			context = null;
		} catch (JSONException e) {e.printStackTrace();}
	}
	
}