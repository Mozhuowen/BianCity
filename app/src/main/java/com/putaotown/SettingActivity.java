package com.putaotown;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.UpdateUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelAppUpdate;
import com.putaotown.update.UpdateService;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiaomi.mipush.sdk.MiPushClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener,UpdateCallBack
{
	private Dialog dialog2;
	private ImageView mCover;
	private TextView mUsername;
	private ModelAppUpdate m;	//升级信息
	
	private SystemBarTintManager mTintManager;
	
	@Override
	public void onStart() {
		super.onStart();
		loadUserCover();
		this.mUsername.setText(UserPreUtil.getUsername());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        //设置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
  		mTintManager = new SystemBarTintManager(this);
  		mTintManager.setStatusBarTintEnabled(true);
  		mTintManager.setNavigationBarTintEnabled(true);
  		mTintManager.setTintColor(this.getResources().getColor(R.color.basecolor));
        
        initViews();
	}
	
	public void initViews() {
		this.mCover = (ImageView)findViewById(R.id.activity_setting_usercover);
		this.mUsername = (TextView)findViewById(R.id.activity_setting_username);

		loadUserCover();	
	}
	
	public void openDraft(View v) {
		DraftActivity.startAction(this);
	}
	
	/**加载用户头像*/
	private void loadUserCover() {
		String cover = UserPreUtil.getCover();
		BianImageLoader.getInstance().loadImage(this.mCover, cover, 90);
//		if (cover.contains("http")) {
////			SLoadImage.getInstance().loadImage(this.mCover, cover, 90, 90,true);
//			
//		} else {
//			ImgRecy ir = new ImgRecy(cover,90);
//			SLoadImage.getInstance().loadImage(this.mCover, cover, ir.size,ir.size,ir.irank);
//		}
			
	}
	/**打开编辑用户资料界面*/
	public void openEditUser(View v) {
		EditUserActivity.startAction(this);
	}
	
	public static void startAction(Activity context) {
		Intent intent = new Intent(context,SettingActivity.class);
		context.startActivityForResult(intent, 600);
	}
	
	public void backEvent(View source){
		finish();
	}
	/**退出登录*/
	public void onExist(View v) {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null);
		View exitok = view.findViewById(R.id.dialog_exit_ok);
		View exitcancel = view.findViewById(R.id.dialog_exit_cancel);
		exitok.setOnClickListener(this);
		exitcancel.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();	
	}
	/**检查升级网络返回
	 * @throws NameNotFoundException */
	@Override
	public void onReceive(ModelAppUpdate m) {
		int currVersionCode = 0;
		try {
			currVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(currVersionCode > 0 && m.getVersioncode() > currVersionCode) {
			this.m = m;
			alterUpdateDialog();
		} else {
			Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.dialog_exit_ok:
			dialog2.dismiss();
			UserPreUtil.logout();
			//注销小米推送alias
	  		MiPushClient.setAlias(this, "0", null);
			this.setResult(600);
			this.finish();
			break;
		case R.id.dialog_exit_cancel:
			dialog2.dismiss();
			break;
		case R.id.dialog_update_cancel:
			dialog2.dismiss();
			break;
		case R.id.dialog_update_update:
			dialog2.dismiss();
			Intent intent = new Intent(this,UpdateService.class);
			intent.putExtra("Key_App_Name","边城");
			intent.putExtra("Key_Down_Url",m.getDownloadurl());						
			startService(intent);
			break;
		}
	}
	/**弹出升级对话框*/
	public void alterUpdateDialog() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
		View update = view.findViewById(R.id.dialog_update_update);
		View cancel = view.findViewById(R.id.dialog_update_cancel);
		TextView info = (TextView)view.findViewById(R.id.dialog_update_info);
		info.setText(m.getUpdateinfo());
		update.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();	
	}
	/**检查升级*/
	public void checkUpdate(View v) {
		try {
			UpdateUtil.checkUpdate(this);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, "网络不给力", Toast.LENGTH_SHORT).show();
		}
	}
	/**打开关于我们*/
	public void openAbout(View v) {
		AboutActivity.startAction(this);
	}
	/**打开常见问题*/
	public void openFAQ(View v) {
		FAQActivity.startAction(this);
	}
	
}