package com.putaotown.net;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.putaotown.net.objects.Image;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

public class PutaoRequest extends PutaoBaseNetwork
{
	final Activity activity;
	private int townid;
	private String title;
	private String content;
	private String cover;
	private List<Image> images;
	private int hasUpload = 0;
	Handler messhandler;
	ProgressBar progressbar;
	MultiUpImage mTask;
	
	private BCRequest jsonObjectRequest;
	private UpImage upimage;
	
	public PutaoRequest(Activity ac,Handler messhandler,ProgressBar progressbar,int townid,String title,String content,String cover,List<Image> images) {
		this.activity = ac;
		this.title = title;
		this.content = content;
		this.cover = cover;
		this.images = images;
		this.messhandler = messhandler;
		this.townid = townid;
		this.progressbar = progressbar;
	}
	
	public void createPutao() {
//		String localfilepath = context.getFilesDir() + "/image/" + cover;
//		//先上传封面照片
//		upimage = new UpImage(activity,this,localfilepath,cover);
//		upimage.start();	
		String[] imagenames = new String[images.size()+1];
		imagenames[0] = cover;
		for (int i=1;i<imagenames.length;i++) {
			imagenames[i] = images.get(i-1).getImagename();
		}
		mTask = new MultiUpImage(this,this.messhandler, imagenames, progressbar);
		mTask.upload();
	}
	
	public void cancelTask() {
		mTask.cancelTask();
	}
	
	public void commitCreateInfo() {
		PackagePutao putao = new PackagePutao();
		putao.setTownid(townid);
		putao.setCover(cover);
		putao.setTitle(title);
		putao.setContent(content);
		putao.setImages(images);
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(new Gson().toJson(putao));
			LogUtil.d("request obj info", jobj.toString());
		} catch (JSONException e) {e.printStackTrace();}
		//构造请求
		jsonObjectRequest = new PutaoJsonRequest(Method.POST,host + "/createputao"
				,jobj
				,new CreatePutaoListener(activity,messhandler)
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
	/**
	 * 上传照片结束后回调此方法,直到照片全部上传完毕
	 * @param upimage
	 * @param activity
	 */
	public void onFinishUpImage(UpImage upimage) {
		Log.d("PutaoRequest finish upimage", ""+upimage.getResult());
		hasUpload++;
		if ( upimage.getResult() == false){
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);
		} else if (images.size()>0 && hasUpload<=images.size()) {
			Image oneimage = images.get(hasUpload-1);
			String localfilepath = context.getFilesDir() + "/image/" + oneimage.getImagename();
			upimage = new UpImage(activity,this,localfilepath,oneimage.getImagename());
			upimage.start();
		} else {
			commitCreateInfo();
		}
	}
	@Override
	public void onFinishUpImage(boolean iserror) {
		if (iserror) {
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);	
			return;
		} else
			commitCreateInfo();
	}
}