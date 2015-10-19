package com.putaotown.net.community;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.PutaoApplication;
import com.putaotown.community.ThemeTieActivity;
import com.putaotown.community.models.ModelTie;
import com.putaotown.net.MultiUpImage;
import com.putaotown.net.NetFailToast;
import com.putaotown.net.PutaoBaseNetwork;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.objects.Image;
import com.putaotown.tools.LogUtil;

public class NewTieUtil extends PutaoBaseNetwork
{
	private ThemeTieActivity context;
	private Handler messhandler;
	private int communityid;
	private int zhulouid;
	private String content;
	private List<Image> images;
	public NewTieUtil(ThemeTieActivity c,Handler handler,int commuid,int zhulouid,String content,List<Image> images) {
		this.context = c;
		this.messhandler = handler;
		this.communityid = commuid;
		this.zhulouid = zhulouid;
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
	
	@Override
	public void onFinishUpImage(boolean iserror) {
		if (iserror) {
			Message mess = new Message();
			mess.what = 200;
			messhandler.sendMessage(mess);	
			return;
		} else{
			LogUtil.v("NewThemeUtil info: ", "finished uploadImage!");
			newtie();
		}
	}
	
	public void newtie() {
		ModelTie req = new ModelTie();
		req.setCommunityid(communityid);
		req.setZhulouid(zhulouid);
		req.setContent(content);
		req.setImages(images);
		
		RequestUtil.getInstance().post("submitie", req
				,new Response.Listener<JSONObject>(){
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.v("NewTieUtil Response TAG", response.toString());
				try {
					boolean stat = response.getBoolean("stat");
					if (!stat) {										//失败
						int errorcode = response.getInt("errcode");		//获取失败码
						NetFailToast.show(errorcode);		//提示失败原因
						context.onFinishReply(false);
					} else {											//成功
						context.onFinishReply(true);			
					}
				} catch (JSONException e) {e.printStackTrace();}
			}}					
		, new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					LogUtil.e("TAG volleyResponseError", error.toString());	
					context.onFinishReply(false);
					Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
				}
			}
	});
	}
	
}