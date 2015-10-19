package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import com.putaotown.community.models.ModelCommunity;
import com.putaotown.net.community.GetJoinCommunity;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import me.maxwin.view.XListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CommunitylistActivity extends AppCompatActivity
{
	private LayoutInflater mLayoutInflater;
	private XListView mXList;
	private TextView mTitle;
	private TextView mTexthint;
	private List<View> mAdapterList = new ArrayList<View>();
	private MyListAdapter mAdapter;
	
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_communitylist);
		//设置状态栏
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
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
		
		initView();
		this.setActionBar();
	}
	
	public void initView() {
		mLayoutInflater = LayoutInflater.from(this);
//		this.mTitle = (TextView)findViewById(R.id.activity_communitylist_title);
		this.mXList = (XListView)findViewById(R.id.activity_communitylist_xlistview);
		this.mXList.setPullLoadEnable(false);
		this.mXList.setPullRefreshEnable(false);
		
//		this.mTitle.setText("社区");
		View hintview = mLayoutInflater.inflate(R.layout.view_messboard_hint, null);
		mTexthint = (TextView)hintview.findViewById(R.id.view_messboard_hint);
		mTexthint.setText(getString(R.string.xlistview_header_hint_loading));
		
		this.mAdapterList.add(hintview);
		this.mAdapter = new MyListAdapter(this,this.mAdapterList);
		this.mXList.setAdapter(mAdapter);

		loadData();
	}
	
	public void loadData() {
		GetJoinCommunity.submit(this, 0);
	}
	
	public void onNetWorkFinish(List<ModelCommunity> data) {
		if (data.size() == 0){
			mTexthint.setText(getString(R.string.nomorehint));
			return;
		}
		this.mAdapterList.remove(0);
		ArrayList<View> listview = new ArrayList<View>();
		for (int i=0;i<data.size();i++) {
			ListItemCommunity item = new ListItemCommunity(this,data.get(i));
			View view = item.makeItemView();
			listview.add(view);
		}
		this.mAdapterList.addAll(listview);
//		LogUtil.v("SublistActivity info: ", "adapter size: "+this.mAdapterList.size());
		this.mAdapter.notifyDataSetChanged();
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,CommunitylistActivity.class);
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