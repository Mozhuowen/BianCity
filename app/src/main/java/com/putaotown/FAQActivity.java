package com.putaotown;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FAQActivity extends AppCompatActivity
{
	private SystemBarTintManager mTintManager;
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_faq);
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
	}
	
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,FAQActivity.class);
		context.startActivity(intent);
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