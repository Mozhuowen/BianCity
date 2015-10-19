package com.putaotown;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
/**
 * 显示当前位置activity
 * @author awen
 *
 */
public class GetGeoActivity extends AppCompatActivity implements LocationSource,
AMapLocationListener, OnCheckedChangeListener
{
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private GeoInfo geo;
	
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 this.setContentView(R.layout.activity_getgeo);
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
		 
		 mapView = (MapView) findViewById(R.id.activity_getgeo_mapview);
		 mapView.onCreate(savedInstanceState);// 此方法必须重写
		 
		 geo = new GeoInfo();
		 initView();		
	}
	
	private void initView() {
		this.setActionBar();
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		
	}
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.animateCamera(CameraUpdateFactory.zoomTo(20), 1000, null);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
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
		deactivate();
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
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//				aMap.animateCamera(CameraUpdateFactory.zoomTo(20), 1000, null);
				double lati = amapLocation.getLatitude();
				double logti = amapLocation.getLongitude();
				String address = amapLocation.getAddress();
				String province = amapLocation.getProvince();
				String country = amapLocation.getCountry();
				String city = amapLocation.getCity();
				String citycode = amapLocation.getCityCode();
				String district = amapLocation.getDistrict();
				String street = amapLocation.getStreet();
				float Accuracy = amapLocation.getAccuracy();
				String road = amapLocation.getRoad();
				geo.setLatitude(lati);
				geo.setLongitude(logti);
				geo.setAddress(address);
				geo.setProvince(province);
				geo.setCountry(country);
				geo.setCity(city);
				geo.setCitycode(citycode);
				geo.setDistrict(district);
				geo.setStreet(street);
				geo.setAccuracy(Accuracy);
				geo.setRoad(road);
				LogUtil.v("Location info: ", lati+" "+logti+" "+address+" "+province+" "+country+" "+city+" "+citycode+" "+district+" "+street+" "+Accuracy+" "+road);
			} else {
				LogUtil.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
			}
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 3 * 1000, 10, this);	//使用高德混合定位
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;		
	}
	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	public void ok(View view) {
		if (geo.getLatitude() > 0 && geo.getLongitude() > 0) {
			VerifyGeoActivity.startAction(this, geo);
			this.finish();
		} else {
			Toast.makeText(this, "未能获取当前位置", Toast.LENGTH_SHORT).show();
		}
	}
	public static void startAction(Context context) {
		Intent intent = new Intent(context,GetGeoActivity.class);
		context.startActivity(intent);
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