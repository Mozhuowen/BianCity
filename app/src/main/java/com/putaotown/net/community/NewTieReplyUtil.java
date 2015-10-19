package com.putaotown.net.community;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.community.ReplyTieActivity;
import com.putaotown.community.models.ModelTieReply;
import com.putaotown.community.models.ResSubmiTieReply;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.PutaoBaseNetwork;
import com.putaotown.net.RequestUtil;
import com.putaotown.tools.LogUtil;

public class NewTieReplyUtil extends PutaoBaseNetwork
{
	public static void submit(final ReplyTieActivity ac,int parenttie,int bereplyid,String content) {
		ModelTieReply req = new ModelTieReply();
		req.setParentie(parenttie);
		req.setBereplyid(bereplyid);
		req.setContent(content);
		
		RequestUtil.getInstance().post("submitiereply", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("NewTieReplyUtil Response TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
								ac.onReply(false, null);
							} else {											//成功
								ac.onReply(true, new Gson().fromJson(response.toString(), ResSubmiTieReply.class).getTiereply());	
							}
						} catch (JSONException e) {e.printStackTrace();}
					}}	
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());	
							ac.onReply(false, null);
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
						}
					}
			});
	}
}