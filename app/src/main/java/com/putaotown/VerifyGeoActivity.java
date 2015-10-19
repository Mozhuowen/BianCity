package com.putaotown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapScreenShotListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.tools.CharacterUtil;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyGeoActivity extends AppCompatActivity implements OnMapScreenShotListener,OnMarkerClickListener,OnMarkerDragListener
{
	private AMap aMap;
	private MapView mapView;
	private EditText freeaddr;
	private TextView address_prefix;
	private static GeoInfo geoinfo;
	private SystemBarTintManager mTintManager;
	private double new_latitude;	//用户拖拽后的新坐标
	private double new_longtitude;	//同上
	private LatLng primaryPos;	//记录原始坐标点，用于判断手动移动marker是否超出范围
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtil.v("Verify info: ", geoinfo.getLatitude()+" "+geoinfo.getLongitude());
		Log.v("Verify info: ", geoinfo.getLatitude()+" "+geoinfo.getLongitude());
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_verifygeo);
		this.setActionBar();
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
		
		this.address_prefix = (TextView)findViewById(R.id.activity_verifygeo_address);
		if (geoinfo.getProvince() != null && geoinfo.getCity() != null)
			this.address_prefix.setText(geoinfo.getProvince()+geoinfo.getCity());
		else
			this.address_prefix.setText("");
		this.freeaddr = (EditText)findViewById(R.id.activity_verifygeo_freeaddr);
		mapView = (MapView) findViewById(R.id.activity_verifygeo_mapview);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		this.primaryPos = new LatLng(this.geoinfo.getLatitude(),this.geoinfo.getLongitude());
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}
	/**
	 * 对地图添加一个marker
	 */
	private void setUpMap() {
//		MarkerOptions maker = new MarkerOptions().position(new LatLng(geoinfo.getLatitude(),geoinfo.getLongitude()));
		//增加拖拽功能的maker
		aMap.addMarker(new MarkerOptions().position(new LatLng(geoinfo.getLatitude(),geoinfo.getLongitude())).draggable(true));
		aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoinfo.getLatitude(),geoinfo.getLongitude()),18), 1000, null);
	}
	
	public static void startAction(Context context,GeoInfo geo) {
		Intent intent = new Intent(context,VerifyGeoActivity.class);
		context.startActivity(intent);
		geoinfo = geo;
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 对地图进行截屏
	 */
	public void getMapScreenShot() {
		aMap.getMapScreenShot(this);
	}
	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		String screenPng = CharacterUtil.getRandomString2(16);
		if(null == bitmap){
			return;
		}
		try {
			String filepath = getFilesDir()+"/image/"+ screenPng;
			File file = new File(filepath);
			file.getParentFile().mkdir();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath);
			Log.v("Screen File address: ", getFilesDir()+"/image/"+ screenPng);
			boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b){
//				Toast.makeText(this, "截图成功", Toast.LENGTH_SHORT).show();
				geoinfo.setScreenpng(screenPng);
			}
			else {
//				Toast.makeText(this, "截图失败", Toast.LENGTH_SHORT).show();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}	
	public void backEvent(View source){
		finish();
	}
	public void ok(View view) {		
		String freeaddr = this.freeaddr.getText().toString();
		if (freeaddr == null || freeaddr.length() == 0) {
			Toast.makeText(this, "没写个性地址哦", Toast.LENGTH_SHORT).show();
			return;
		}
		this.getMapScreenShot();
		geoinfo.setFreeaddr(freeaddr);
		//start next activity
		CreateTownActivity.startAction(this,geoinfo);
		this.finish();
	}
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (aMap != null) {
			jumpPoint(marker);
		}
		return false;
	}
	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		this.new_latitude = marker.getPosition().latitude;
		this.new_longtitude = marker.getPosition().longitude;
		
		LatLng newpos = new LatLng(new_latitude,new_longtitude);
		LatLng lastpos = new LatLng(this.geoinfo.getLatitude(),this.geoinfo.getLongitude());
		float distan = AMapUtils.calculateLineDistance(newpos,primaryPos);
		//手动纠正只能在原始坐标200米范围内
		if (distan > 200 /*&& false*/) {
			marker.setPosition(lastpos);
			Toast.makeText(this, "超出范围了哦", Toast.LENGTH_SHORT).show();
		} else {		
			this.geoinfo.setLatitude(new_latitude);
			this.geoinfo.setLongitude(new_longtitude);
		}
	}
	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/** marker点击时跳动一下*/
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		final LatLng point = new LatLng(geoinfo.getLatitude(),geoinfo.getLongitude());
		Point startPoint = proj.toScreenLocation(point);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1000;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * point.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * point.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				aMap.invalidate();// 刷新地图
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});

	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_okview, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		View okv = v.findViewById(R.id.actionbar_ok);
		okv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ok(null);
			}
		});
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