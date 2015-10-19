package com.putaotown.net.community;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.community.NewThemeActivity;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.net.MultiUpImage;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.PutaoBaseNetwork;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.objects.Image;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;

public class NewThemeUtil extends PutaoBaseNetwork
{
	private NewThemeActivity na;
	private Handler messhandler;
	private int communityid;
	private String title;
	private String content;
	private List<Image> images;
	public NewThemeUtil(final NewThemeActivity na,Handler messhandler,int communityid,String title,String content,List<Image> images) {
		this.na = na;
		this.messhandler = messhandler;
		this.communityid = communityid;
		this.title = title;
		this.content = content;
		this.images = images;
	}
	
	public void submit() {
		String[] imagenames = new String[images.size()];
		for (int i=0;i<images.size();i++) {
			imagenames[i] = images.get(i).getImagename();
		}
		new MultiUpImage(this,messhandler, imagenames, null).upload();	//最后一个参数废弃
	}
	
	/**提交信息到pt服务器*/
	public void newtheme() {
		ModelTieTheme req = new ModelTieTheme();
		req.setCommunityid(communityid);
		req.setTitle(title);
		req.setContent(content);
		req.setImages(images);
		
		RequestUtil.getInstance().post("submitieth", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("NewThemeUtil Response TAG", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
								na.onFinishSubmit(false);
							} else {											//成功
								na.onFinishSubmit(true);			
							}
						} catch (JSONException e) {e.printStackTrace();}
					}}					
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());	
							na.onFinishSubmit(false);
							Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
						}
					}
			});
		
	}
	@Override
	public void onFinishUpImage(boolean iserror) {
		if (iserror) {
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);	
			return;
		} else{
			LogUtil.v("NewThemeUtil info: ", "finished uploadImage!");
			newtheme();
		}
	}
}