package com.putaotown.net;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.putaotown.CreateTownActivity;
import com.putaotown.LoginActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.RegisteActivity;
import com.putaotown.net.objects.ModelCName;
import com.putaotown.net.objects.ModelRegisteQQ;
import com.putaotown.net.objects.ModelRegisteWb;
import com.putaotown.net.objects.RequestLogin;
import com.putaotown.net.objects.RequestRegiste;
import com.putaotown.tools.LogUtil;
import com.sina.weibo.sdk.openapi.models.User;

public class UserRequest extends PutaoBaseNetwork
{
	final Activity activity;
	String name;
	String password;
	String email;
	String cover;
	String localfilepath;
	Handler messhandler;
	//new param
	String uid;	//微博是uid,qq是openid
	String token;
	long expirestime;
	int logintype;
	private MultiUpImage mCurrTask;
	
	private BCRequest jsonObjectRequest;
	
	/**
	 * 用于注册的构造器
	 * @param ac
	 * @param name
	 * @param password
	 * @param email
	 * @param cover
	 * @param localfilepath
	 */
	public UserRequest(Activity ac,Handler messhandler,String name,String password,String email,String cover,String localfilepath) {
		this.activity = ac;
		this.name = name;
		this.password = password;
		this.email = email;
		this.cover = cover;
		this.localfilepath = localfilepath;
		this.messhandler = messhandler;
	}
	/**
	 * 用于登录的构造器
	 * @param ac
	 * @param name
	 * @param password
	 * @param email
	 */
	public UserRequest(Activity ac,Handler messhandler,String name,String password,String email) {
		this.activity = ac;
		this.name = name;
		this.password = password;
		this.email = email;
		this.messhandler = messhandler;
	}
	public UserRequest(Activity ac,Handler messhandler,int logintype,String uid,String token,long expirestime) {
		this.activity = ac;
		this.logintype = logintype;
		this.uid = uid;
		this.token = token;
		this.expirestime = expirestime;
		this.messhandler = messhandler;
	}
	public UserRequest(Activity ac) {
		this.activity = ac;
	}
	/**
	 * 注册的方法调用，先上传照片再提交注册信息。
	 */
	public void registe() {	
		//上传图片
		UpImage upimage = new UpImage(activity,this,localfilepath,cover);
		upimage.start();
	}
	
	/**
	 * 提交用户注册信息
	 */
	public void registe_user() {
//		String param = "?name="+name+"&password="+password+"&email="+email+"&cover="+cover;
		/*RequestRegiste reqobj = new RequestRegiste();
		reqobj.setName(name);
		reqobj.setEmail(email);
		reqobj.setCover(cover);
		reqobj.setPassword(password);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(reqobj));
			Log.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}

		
		jsonObjectRequest = new JsonObjectRequest(Method.POST,host+"/putao/registe"
				,jobj
				,new RegisteListener(activity,messhandler)
				,new Response.ErrorListener(){
        			@Override
        			public void onErrorResponse(VolleyError error) {
        				if(error!=null)
        					Log.e("TAG-volleyError", error.toString());
        				if (activity instanceof RegisteActivity) {
        					Message mess = new Message();
        					mess.what = 200;
        					messhandler.sendMessage(mess);
        				}
        			}
        		});

		mQueue.add(jsonObjectRequest);*/
	}
	
	public void registe(User u,ModelRegisteQQ qquser,int ptuserid,String ptoken,int logintype) {	
		RequestRegiste reqobj = new RequestRegiste();
		reqobj.setPtuserid(ptuserid);
		reqobj.setPtoken(ptoken);
		reqobj.setLogintype(logintype);
		if (logintype == 0) {
			ModelRegisteWb userinfo = new ModelRegisteWb();
			userinfo.parse(u);		
			reqobj.setRegistInfo(userinfo);
			
		} else {	// 注册QQ信息
			reqobj.setRegistQqInfo(qquser);
			reqobj.setLogintype(1);
		}
		
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(reqobj));
			LogUtil.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host+"/registe"
				,jobj
				,new RegisteListener(activity,messhandler)
				,new Response.ErrorListener(){
        			@Override
        			public void onErrorResponse(VolleyError error) {
        				if(error!=null)
        					LogUtil.e("TAG-volleyError", error.toString());
        				if (activity instanceof RegisteActivity) {
        					Message mess = new Message();
        					mess.what = 200;
        					messhandler.sendMessage(mess);
        				}
        			}
        		});

		mQueue.add(jsonObjectRequest);
	}
	/**
	 * 登录方法调用
	 */
	public void login() {
		RequestLogin reqobj = new RequestLogin();
		//set data
		reqobj.setUid(uid);
		reqobj.setToken(token);
		reqobj.setExpire(expirestime);
		if (logintype == 0)
			reqobj.setLogintype(0);
		else 
			reqobj.setLogintype(1);
		
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(reqobj));
			LogUtil.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/login"
				,jobj
				,new LoginListener(activity,messhandler)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null)
					LogUtil.e("UserRequest->login->TAG-volleyError",host + "/login "+error.toString());
				if (activity instanceof LoginActivity) {
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
	public void cname(int ptuid,String ptoken,String name) {
		ModelCName reqobj = new ModelCName();
		reqobj.setPtuserid(ptuid);
		reqobj.setPtoken(ptoken);
		reqobj.setUsername(name);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(reqobj));
			Log.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/cname"
				,jobj
				,new CNameListener(activity)
				,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null)
					LogUtil.e("TAG-volleyError", error.toString());
				if (activity instanceof LoginActivity) {
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
		mQueue.add(jsonObjectRequest);
	}
	/**
	 * 上传照片结束后回调此方法
	 * @param upimage
	 * @param activity
	 */
	public void onFinishUpImage(UpImage upimage) {
		Log.d("GetRequest finish upimage", ""+upimage.getResult());
		
		if(upimage.getResult() == false) {
			if (activity instanceof RegisteActivity){
				Message mess = new Message();
				mess.what = 200;
				messhandler.sendMessage(mess);
			}
			return;
		}
		
		//注册用户信息
		this.registe_user();
	}
}