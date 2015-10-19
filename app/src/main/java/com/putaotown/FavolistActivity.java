package com.putaotown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.putaotown.net.FavoriteUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavolistActivity extends AppCompatActivity
{
	private LayoutInflater mLayoutInflater;
	private XListView mXList;
	private TextView mTitle;
	private TextView mTexthint;
	private List<View> mAdapterList = new ArrayList<View>();
	private MyListAdapter mAdapter;
	
	/**需要回收内存的view及其资源装载以便回收*/
	Map<View, ImgRecy> mRecycleResource = new HashMap<View, ImgRecy>();
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_subscrilist);
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
//		this.mTitle = (TextView)findViewById(R.id.activity_subscrilist_title);
		this.mXList = (XListView)findViewById(R.id.activity_subscrilist_xlistview);
		this.mXList.setPullLoadEnable(false);
		this.mXList.setPullRefreshEnable(false);
		
//		this.mTitle.setText("收藏列表");
		View hintview = mLayoutInflater.inflate(R.layout.view_messboard_hint, null);
		mTexthint = (TextView)hintview.findViewById(R.id.view_messboard_hint);
		mTexthint.setText(getString(R.string.xlistview_header_hint_loading));
		
		this.mAdapterList.add(hintview);
		this.mAdapter = new MyListAdapter(this,this.mAdapterList);
		this.mXList.setAdapter(mAdapter);

		loadFavorite();
	}
	
	public void loadFavorite() {
		FavoriteUtil.getFavolist(this, 0);
	}
	
	/**加载葡萄网络返回*/
	public void onNetworkFinish(List<PackagePutao> p) {		
		if (p.size() == 0){
			mTexthint.setText(getString(R.string.nomorehint));
			return;
		}
		List<View> listviews = new ArrayList<View>();
		this.mAdapterList.remove(0);
		for (int i=0;i<p.size();i++) {
			ListItemPutao item = new ListItemPutao(this,this,p.get(i));
			View view = item.makeItemView();
			listviews.add(view);
			//add to recycle
			this.mRecycleResource.put(item.imagev, new ImgRecy(p.get(i).getCover(),90));
		}	
		
		this.mAdapterList.addAll(listviews);
		this.mAdapter.notifyDataSetChanged();
	}
	
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context){
		Intent intent = new Intent(context,FavolistActivity.class);
		context.startActivity(intent);
	}
	/**资源回收*/
	@Override
	public void onDestroy() {
		super.onDestroy();
//		Set<View> set = mRecycleResource.keySet();
//		for (Iterator<View> it = set.iterator();it.hasNext();) {
//			View view = it.next();
//			if (view instanceof CircleImageView) {
//				LogUtil.v("UserActivity info: ", "Recycle one CircleImageView resource!");
//				((ImageView)view).setImageBitmap(null);
//			} else if (view instanceof ImageView) {
//				((ImageView)view).setImageBitmap(null);
//				ImgRecy ir = mRecycleResource.get(view);
//				SLoadImage.getInstance().delBitmapFromMemoryCache(ir.getRecyStr());
//			} 
//		}
		this.setContentView(R.layout.view_null);
		System.gc();
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