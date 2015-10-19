package com.putaotown;

import java.util.List;

import com.putaotown.localio.PushMessUtil;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelPushGood;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

public class MsgShowGoodActivity extends AppCompatActivity
{
	private ListView mListview;
	private TextView hintview;
	private List<ModelPushGood> mGoodList;
	
	InputMethodManager imm ;	//全局输入法控制
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msgshow);
		//设置状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//		{
//			Window win = getWindow();
//			WindowManager.LayoutParams winParams = win.getAttributes();
//			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//			winParams.flags |= bits;
//			win.setAttributes(winParams);
//		}
//  		mTintManager = new SystemBarTintManager(this);
//  		mTintManager.setStatusBarTintEnabled(true);
//  		mTintManager.setNavigationBarTintEnabled(true);
//  		mTintManager.setTintColor(this.getResources().getColor(R.color.basecolor));
  		this.setActionBar();
  		mGoodList = PushMessUtil.getGoods();
  		LogUtil.v("MsgShowGood info: mGoodList size: ", mGoodList.size()+"");
  		initViews();
	}
	
	public void initViews() {
		mListview = (ListView) findViewById(R.id.activity_msgshow_xlistview);
		this.hintview = (TextView)findViewById(R.id.activity_msgshow_hint);
		
		if (mGoodList.size() > 0)
			this.hintview.setVisibility(View.GONE);
		MsgShowGoodAdapter msgadapter = new MsgShowGoodAdapter(this,mGoodList);
		mListview.setAdapter(msgadapter);
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,MsgShowGoodActivity.class);
		context.startActivity(intent);
	}
	
	public static class MsgOpenTargetListener implements OnClickListener
	{
		private int type;
		private Object object;
		private Activity activity;
		
		public MsgOpenTargetListener(Activity activity,int type,Object object) {
			this.activity = activity;
			this.type = type;
			this.object = object;
		}
		@Override
		public void onClick(View v) {
			switch(type) {
			case 0:
				PackagePutao story = (PackagePutao)object;
				PutaoxActivity.startAction(activity, story, false);
				break;
			case 1:
				ApplyTown town = (ApplyTown)object;
				ModelUser u = new ModelUser();
				u.setUserid(town.getUserid());
				u.setName(town.getUsername());
				u.setCover(town.getUsercover());
				TownActivity.startAction(activity, town, u, false);
				break;
			case 2:
				PackagePutao story2 = (PackagePutao)object;
				PutaoxActivity.startAction(activity, story2, false);
				break;
			}
			
		}
		
	}
	
	public void backEvent(View source){
		finish();
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        finish(); 
	        return true; 
	        default:
	        	return true;
	    }  
	}
}