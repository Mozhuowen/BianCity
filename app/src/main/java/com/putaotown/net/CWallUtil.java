package com.putaotown.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.fragment.MineFragmentx;
import com.putaotown.net.objects.ModelCWall;
import com.putaotown.net.objects.ResponseHotTown;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;

/**修改墙纸的网络工具类*/
public class CWallUtil extends PutaoBaseNetwork
{
	Handler messhandler;
	MineFragmentx mine;
	String wall;
	ProgressBar probar;
	private MultiUpImage mCurrTask;
	
	public CWallUtil(MineFragmentx m,ProgressBar probar,Handler messhandler,String image) {
		this.messhandler = messhandler;
		this.mine = m;
		this.wall = image;
		this.probar = probar;
	}
	
	public void doaction() {
		//上传照片
		String[] imagenames = new String[]{wall};
		mCurrTask = new MultiUpImage(this,this.messhandler, imagenames, probar);
		mCurrTask.upload();
	}
	
	public void commitInfo() {
		ModelCWall req = new ModelCWall();
		req.setWallimage(wall);
		
		RequestUtil.getInstance().post("cwall", req
				, new Response.Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.v("LoadTownUtil info: ", response.toString());
						try {
							boolean stat = response.getBoolean("stat");
							if (!stat) {										//失败
//								Toast.makeText(PutaoApplication.getContext(), "网络不给力", Toast.LENGTH_LONG).show();
								int errorcode = response.getInt("errcode");		//获取失败码
								NetFailToast.show(errorcode);		//提示失败原因
							} else {											//成功
								Gson gson = new Gson();
								ResponseSimple res = gson.fromJson(response.toString(), ResponseSimple.class);				
								mine.onCWall(true);
							}
						} catch (JSONException e) {e.printStackTrace();}
					}		
				}
				, new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							LogUtil.e("TAG volleyResponseError", error.toString());		
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
		} else
			commitInfo();
	}
	
	public void cancelTask()
	{
		mCurrTask.cancelTask();
	}
}