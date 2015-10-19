package com.putaotown.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.putaotown.tools.LogUtil;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类进行数据包的相关设置，包括加密签名header，timestamp等
 * @author awen
 *
 */
public class PutaoJsonRequest extends BCRequest
{
	private String jsoncontent;
	private static final String DIR = "http://123.57.132.31:8080/v2";
//	private static final String DIR = "http://192.168.199.191/v2";

	public PutaoJsonRequest(int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {	
		super(DIR + url, jsonRequest, listener, errorListener);
		this.jsoncontent = jsonRequest.toString();
		LogUtil.v("PutaoJsonRequest", "url: "+DIR + url);
	}
	/**加入认证签名*/
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
	{
		String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("timestamp", timeStamp);
        headers.put("signature", "test");	//现在发到网络到消息已经全部加密,无需再进行签名认证,但为了和服务器兼容这两个HTTP头部还要加上
//		try {
////			headers.put("signature", MsgCrypt.encryptMsg(timeStamp, jsoncontent));
//		} catch (AesException e) {
//			// TODO Auto-generated catch block
//			LogUtil.v("Encrypt exception: ", e.toString());
//		}

		return headers;
	}
	
}