package com.putaotown;

import com.basv.gifmoviewview.widget.GifMovieView;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UserRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class CNameActivity extends Activity
{
	private EditText username;
	private Dialog dialog2;
	private String newname;
	
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cname);
		
		initView();
	}
	
	private void initView() {
		this.username = (EditText)findViewById(R.id.activity_cname_username);
	}
	
	public void ok(View view) {
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);     
//		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		this.alerLoading();
		
		String name = this.username.getText().toString();
		if (name != null && name.length() > 0) {
			newname = name;
			int puid = UserPreUtil.getUserid();
			String token = UserPreUtil.getPtoken();
			UserRequest request = new UserRequest(this);
			request.cname(puid, token, name);
		}
	}
	
	public void onSuccess() {
		this.dialog2.dismiss();
		this.setResult(200);
		UserPreUtil.updateUserName(newname);
		this.finish();
	}
	public void onFail() {
		this.dialog2.dismiss();
		this.username.setText("");
		Toast.makeText(this, "用户名被别人抢了，换个吧", Toast.LENGTH_SHORT).show();
	}
	public void backEvent(View source){
		finish();
	}
	/**
	 * 弹出缓存等待对话框
	 */
	public void alerLoading() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
		final GifMovieView gif1 = (GifMovieView)view.findViewById(R.id.dialog_loading_gif);
		gif1.setMovieResource(R.drawable.loading_big);
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,CNameActivity.class);
		context.startActivity(intent);
	}
}