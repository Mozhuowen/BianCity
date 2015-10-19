package com.putaotown.net;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.putaotown.tools.EncryptUtil;
import com.putaotown.tools.LogUtil;

public class BCRequest extends Request<JSONObject>
{
	private static Gson gsonUtil;
	private final Listener<JSONObject> mListener;
	private final JSONObject requestJson;
	private static final String GLOBAL_CHARSET = "utf-8";
	/** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", GLOBAL_CHARSET);
	private final int ENCRYPT = 0;
	private final int DECRYPT = 1;

	public BCRequest(String url,JSONObject requestJson,Listener<JSONObject> listener, ErrorListener errlistener) {
		super(Method.POST, url, errlistener);
		// TODO Auto-generated constructor stub
		this.mListener = listener;
		this.requestJson = requestJson;
		gsonUtil = new Gson();
	}
	/*
	 * 接收response后先进行解密，然后转换JSONObject
	 * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.NetworkResponse)
	 */
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String responseStr = new String(response.data,  GLOBAL_CHARSET);
//			LogUtil.v("Got Response String", responseStr);
			//解密
			responseStr = new String(EncryptUtil.getImportantInfoByJNI(responseStr, DECRYPT),GLOBAL_CHARSET);
			//封装成json
			JSONObject json = new JSONObject(responseStr);
			return Response.success(json,HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}
	@Override
	protected void deliverResponse(JSONObject response) {
		// TODO Auto-generated method stub
		mListener.onResponse(response);
	}
	/**增加http header*/
	@Override  
    protected Map<String, String> getParams() throws AuthFailureError
    {
		String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("timestamp", timeStamp);
		return null;
    }
	@Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }
	/**输出上传数据，加密输出*/
	@Override
    public byte[] getBody() {
		byte[] requestData = EncryptUtil.getImportantInfoByJNI(this.requestJson.toString(), ENCRYPT);
		return requestData;
	}
	
}