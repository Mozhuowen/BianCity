package com.putaotown;

import java.util.List;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;

import me.maxwin.view.XListView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MyTownsListActivity extends AppCompatActivity
{
	private XListView mXList;
	private static List<ApplyTown> towns;
	private boolean isFirstEnter = true;
	private SearchTownAdapter searchadapter;
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
		setContentView(R.layout.activity_subscrilist);
		LogUtil.v("MyTownsListActivity info: ", "onCreate!");
		towns = UserPreUtil.getMyTowns();
		initViews();
		this.setActionBar();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (!isFirstEnter) {
			LogUtil.v("MyTownListActivity info: ", "enter onResume!");
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					towns = UserPreUtil.getMyTowns();
					searchadapter.changeDataSet(towns);
				}
				
			},500);	
		}
		this.isFirstEnter = false;
	}
	
	public void initViews() {
		this.mXList = (XListView) findViewById(R.id.activity_subscrilist_xlistview);
		this.mXList.setPullLoadEnable(false);
		this.mXList.setPullRefreshEnable(false);
		searchadapter = new SearchTownAdapter(this,towns,true);
		this.mXList.setAdapter(searchadapter);
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,MyTownsListActivity.class);
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