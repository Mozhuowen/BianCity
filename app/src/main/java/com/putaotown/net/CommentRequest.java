package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.putaotown.net.objects.PackageComment;
import com.putaotown.tools.LogUtil;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CommentRequest extends PutaoBaseNetwork
{
	final Activity activity;
	private Handler messhandler;
	private int putaoid;
	private int townid;
	private String content;
	private int commentposition;
	private int replyId;
	
	private BCRequest jsonObjectRequest;
	
	public CommentRequest(Activity ac,Handler handler,int putaoid,int townid,String content,int replyId) {
		this.activity = ac;
		this.messhandler = handler;
		this.putaoid = putaoid;
		this.townid = townid;
		this.content = content;
		this.replyId = replyId;
	}
	
	public CommentRequest(Activity ac,Handler handler,int putaoid) {
		this.activity = ac;
		this.messhandler = handler;
		this.putaoid = putaoid;
	}
	
	public void submitComment() {
		PackageComment comment = new PackageComment();
		comment.setPutaoid(putaoid);
		comment.setTownid(townid);
		comment.setContent(content);
		comment.setReplyid(replyId);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(comment));
			LogUtil.d("CommnetRequest obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/submitcomment"
				,jobj
				,new SubmitCommentListener(activity,messhandler)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					Log.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
	/**
	 * 加载所有评论
	 */
	public void loadComments() {
		PackageComment comment = new PackageComment();
		comment.setPutaoid(putaoid);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(comment));
			LogUtil.d("CommnetRequest obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();return;}
		
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/getcomment"
				,jobj
				,new SubmitCommentListener(activity,messhandler)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					LogUtil.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
	
	public void loadMoreComments(int commentposition) {
		PackageComment comment = new PackageComment();
		comment.setPutaoid(putaoid);
		comment.setCommentposition(commentposition);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(comment));
			LogUtil.d("CommnetRequest obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();return;}
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/getcomment"
				,jobj
				,new SubmitCommentListener(activity,messhandler)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					LogUtil.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
}