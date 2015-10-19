package com.putaotown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.putaotown.localio.PushMessUtil;
import com.putaotown.net.objects.ModelPushSys;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MsgShowSysActivity extends AppCompatActivity
{
	private SystemBarTintManager mTintManager;
	private List<ModelPushSys> sysmess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_sysmsg);
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
  		sysmess = PushMessUtil.getSys();
  		setSimpleListView();
  		
	}
	
	public void setSimpleListView() {
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		
		for (int i=0;i<sysmess.size();i++) {
			Date date = new Date(sysmess.get(i).getTime());
			SimpleDateFormat format = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestr = format.format(date);
			Map<String,Object> listitem = new HashMap<String,Object>();
			listitem.put("content", sysmess.get(i).getContent());			
			listitem.put("time", timestr);
			listItems.add(listitem);
		}
		SimpleAdapter simpleadapter = new SimpleAdapter(this
				,listItems
				,R.layout.listitem_msg_system
				,new String[]{"content","time"}
				,new int[]{R.id.listitem_msg_system_content,R.id.listitem_msg_system_time});
		
		ListView list = (ListView)findViewById(R.id.listview_sysmsg_xlistview);
		list.setAdapter(simpleadapter);
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,MsgShowSysActivity.class);
		context.startActivity(intent);
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