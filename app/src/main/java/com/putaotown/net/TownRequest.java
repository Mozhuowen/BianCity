package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.putaotown.CreateTownActivity;
import com.putaotown.LoginActivity;
import com.putaotown.RegisteActivity;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;

public class TownRequest extends PutaoBaseNetwork
{
	ProgressBar probar;
	final Activity activity;
	String localfilepath;
	String descri;
	String townname;
	String cover;
	GeoInfo geoinfo;
	Handler messhandler;
	private int hasUpload = 0;
	private MultiUpImage mCurrTask;
	
	private BCRequest jsonObjectRequest;
	
	public TownRequest(Activity ac,ProgressBar probar,Handler messhandler,String localfilepath,String descri,String townname,String cover,GeoInfo geoinfo) {
		this.activity = ac;
		this.localfilepath = localfilepath;
		this.descri = descri;
		this.townname = townname;
		this.geoinfo = geoinfo;
		this.cover = cover;
		this.messhandler = messhandler;
		this.probar = probar;
	}
	
	public void apply() {
		LogUtil.v("TownRequest info: ", "geo screenpng: "+geoinfo.getScreenpng());
		//先上传照片
//		UpImage upimage = new UpImage(activity,this,localfilepath,cover);
//		upimage.start();
		String[] imagenames = new String[]{cover,geoinfo.getScreenpng()};
		mCurrTask = new MultiUpImage(this,this.messhandler, imagenames, probar);
		mCurrTask.upload();
	}
	
	public void cancelTask() {
		mCurrTask.cancelTask();
	}
	
	public void commitApplyinfo() {
		ApplyTown commitinfo = new ApplyTown();
		commitinfo.setPtoken(ptoken);		//在父类中
		commitinfo.setPtuserid(ptuserid);	//在父类中
		commitinfo.setCover(cover);
		commitinfo.setDescri(descri);
		commitinfo.setTownname(townname);
		commitinfo.setGeoinfo(geoinfo);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(commitinfo));
			Log.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/applytown"
				,jobj
				,new ApplyTownListener(activity,messhandler)
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
	 * 上传照片结束后回调此方法
	 * @param upimage
	 * @param activity
	 */
	public void onFinishUpImage(UpImage upimage) {
		LogUtil.d("GetRequest finish upimage", ""+upimage.getResult());
		this.hasUpload++;
		if(upimage.getResult() == false) {
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);	
			return;
		} else if (this.hasUpload < 2) {	//上传地址截图
			//地址截图路径
			String filepath = this.activity.getFilesDir()+"/image/" + geoinfo.getScreenpng();
			UpImage upscreen = new UpImage(activity,this,filepath,geoinfo.getScreenpng());
			upscreen.start();
		} else 
			commitApplyinfo();		
	}
	@Override
	public void onFinishUpImage(boolean iserror) {
		if (iserror) {
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);	
			return;
		} else
			commitApplyinfo();
	}
}