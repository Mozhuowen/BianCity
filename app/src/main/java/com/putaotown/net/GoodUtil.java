package com.putaotown.net;

import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.BaseActivity;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.tools.LogUtil;

public class GoodUtil
{
	/**
	 * 获取赞情况
	 * @param baseactivity 回调的activity
	 * @param type	赞的类型 0-town 1-putao 2-comment 3-mess
	 * @param id
	 */
	public static void getGoods(BaseActivity baseactivity,int type,int id) {
		ModelGood req = new ModelGood();
		req.setTargetid(id);
		req.setType(type);
		GoodListener listener = new GoodListener(baseactivity);
		RequestUtil.getInstance().post("getgoods", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
	/**赞动作，action 0-赞 1-取消赞*/
	public static void doGood(BaseActivity baseactivity,int type,int id,int action ) {
		ModelGood req = new ModelGood();
		req.setTargetid(id);
		req.setType(type);
		req.setAction(action);
		GoodListener listener = new GoodListener(baseactivity);
		RequestUtil.getInstance().post("dogood", req, listener
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());					
						}
					}
			});
	}
}