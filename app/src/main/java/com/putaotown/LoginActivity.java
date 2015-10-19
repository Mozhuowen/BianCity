package com.putaotown;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UserRequest;
import com.putaotown.net.objects.ResponseLogin;
import com.putaotown.tools.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	private Context context;
	private EditText username;
	private EditText password;
	private Button registe;
	
	private Dialog dialog2;
	private Builder builder;
	/**
	 * 返回前一个Activity的resultcode
	 */
	private static int resultcode = RESULT_OK;
	
	private Handler messhandler;
	
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		initViews();
		
	}
	
	public void ok(View view) {
		alerLoading();
		String username = this.username.getText().toString();
		String password = MD5Util.getMD5(this.password.getText().toString());
		
		Log.d("LoginActivity login info: ", username+" "+password);
		//发起网络处理逻辑
		UserRequest netrequest = new UserRequest(this,messhandler,username,password,"");
		netrequest.login();
		
	}
	
	public void alerLoading() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);

		builder.setView(view);
		dialog2 = builder.create();
		dialog2.show();
	}
	
	/**
	 * 网络请求成功，返回前一个activity
	 * @param name
	 * @param token
	 * @param cover
	 */
	public void onFinishNetwork(int userid,String name,String token,String cover,String email) {
		dialog2.dismiss();
		UserPreUtil.updateUser(userid,name, token,cover,email);	//更新preference
		
		setResult(resultcode,new Intent());
		finish();
	}
	public void onFinishNetwork(ResponseLogin reslogin) {
		dialog2.dismiss();
//		UserPreUtil.updateUser(reslogin.getUserid(), reslogin.getName(), reslogin.getToken(), reslogin.getCover(), reslogin.getEmail());
//		UserPreUtil.updateAllMyTowns(reslogin.getMytowns());
		setResult(resultcode,new Intent());
		finish();
	}
	
	public void onNetworkFail() {
		dialog2.dismiss();
	}
	
	public void initViews() {
		context = this.getApplicationContext();
		this.username = (EditText)findViewById(R.id.activity_login_name);
		this.password = (EditText)findViewById(R.id.activity_login_password);
		this.registe = (Button)findViewById(R.id.activity_login_registe);
		
		this.registe.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				RegisteActivity.startAction(LoginActivity.this);
			}
			
		});
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(context, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		default:
			setResult(RESULT_OK,new Intent());
			finish();
			break;
		}
	}
	
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context1,int res) {
		resultcode = res;
		Intent intent = new Intent(context1,LoginActivity.class);
		((Activity)context1).startActivityForResult(intent,MainActivity.STARTACTION_LOGIN);
	}
}