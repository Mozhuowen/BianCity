package com.putaotown;

import java.util.HashMap;
import java.util.Map;

/*import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.putaotown.geographic.GeoInfo;*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class GetCoordinateActivity extends Activity
{/*
	private Context context;
	*//**
	 * 经度view
	 *//*
	private TextView longitudeview;
	*//**
	 * 纬度view
	 *//*
	private TextView latitudeview;
	*//**
	 * 地图显示区
	 *//*
	private ViewGroup mapviewgroup;
	*//**
	 * 定位客户端
	 *//*
	public LocationClient mLocationClient = null;
	*//**
	 * 地址信息监听器
	 *//*
	public BDLocationListener myListener;
	 *//**
	  * baiduMap View   
	  *//*
	 MapView mMapView = null;
	 *//**
	  * 地理信息
	  *//*
	 public static GeoInfo geoinfo = new GeoInfo();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_getcoordinate);
        
        initViews();
        
        //获取地图控件引用  使用最大放大级别19
		mMapView = new MapView(this,
				new BaiduMapOptions().mapStatus(new MapStatus.Builder()
				.zoom(19).build()));
		mapviewgroup.addView(mMapView);
		//获取图层
        BaiduMap mBaiduMap = mMapView.getMap();
        ////////百度定位代码///////////////
		myListener = new MyLocationListener(longitudeview,latitudeview,mBaiduMap,geoinfo);
		
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );    //注册监听函数
	    
	    LocationClientOption myoption = new LocationClientOption();
	    myoption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //高精度
	    myoption.setOpenGps(true);	//打开GPS
	    myoption.setCoorType("bd09ll");//返回的定位结果是百度经纬度
	    myoption.setScanSpan(2000);//设置发起定位请求的间隔时间
	    myoption.setIsNeedAddress(true);//返回的定位结果包含地址信息
	    mLocationClient.setLocOption(myoption);
	    //启动定位SDK
	    mLocationClient.start();
	    //发起定位，在监听对象中返回
	    if (mLocationClient != null && mLocationClient.isStarted())
	    	mLocationClient.requestLocation();
	    else 
	    	Log.d("LocSDK5", "locClient is null or not started");
	}
	
	private void initViews() {
		this.context = this.getApplicationContext();
		this.latitudeview = (TextView)findViewById(R.id.activity_getcoordinate_latitude);
		this.longitudeview = (TextView)findViewById(R.id.activity_getcoordinate_longitude);
		this.mapviewgroup = (ViewGroup)findViewById(R.id.activity_getcoordinate_mapview);
	}
	
	public void ok(View view) {
		Intent intent = new Intent();
		intent.putExtra("geoinfo", geoinfo);
		this.setResult(RESULT_OK, intent);
		mLocationClient.stop();
		mMapView.onPause();
		Log.d("getcoordinate result intent:", ""+intent);
		finish();
	}

	@Override
	protected void onPause(){
		mLocationClient.stop();
		mMapView.onPause();
		Log.d("onPause","onPause");
		super.onPause();
	}
	
	*//**
	 * 返回事件，可直接在标签绑定
	 *//*
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,GetCoordinateActivity.class);
		((Activity)context).startActivityForResult(intent,MainActivity.STARTACTION_GETCOORDINATE);
	}

}

class MyLocationListener implements BDLocationListener {
	boolean first = true;
	BaiduMap mBaiduMap;
	TextView txtLat;
	TextView lonview;
	TextView laview;
	GeoInfo geoinfo;
	MyLocationListener(TextView txtLat,TextView lonview,TextView laview,BaiduMap mBaiduMap,GeoInfo gi){
		this.mBaiduMap = mBaiduMap;
		this.laview = laview;
		this.lonview = lonview;
		this.geoinfo = gi;
//		this.txtLat = txtLat;
	}
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
	            return ;
		StringBuffer sb = new StringBuffer(256);
		sb.append("百度bd09ll坐标信息 :\n");
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\n纬度 : ");
		sb.append(location.getLatitude());
		sb.append(" "+getDufenmiao(location.getLatitude()));
		sb.append("\n经度 : ");
		sb.append(location.getLongitude());
		sb.append(" "+getDufenmiao(location.getLongitude()));
		sb.append("\n精度 : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\n卫星数量 : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		} 
		//拾取地理信息
		geoinfo.setLongitude(location.getLongitude());
		geoinfo.setLatitude(location.getLatitude());
		geoinfo.setAddress(location.getAddrStr());
		geoinfo.setProvince(location.getProvince());
		geoinfo.setCity(location.getCity());
		geoinfo.setDistrict(location.getDistrict());
		Map lonmap = getDufenmiao(location.getLongitude());
		geoinfo.setLon_degree((Integer)lonmap.get("degree"));
		geoinfo.setLon_minute((Integer)lonmap.get("minute"));
		geoinfo.setLon_second((Double)lonmap.get("second"));
		Map latmap = getDufenmiao(location.getLatitude());
		geoinfo.setLat_degree((Integer)latmap.get("degree"));
		geoinfo.setLat_minute((Integer)latmap.get("minute"));
		geoinfo.setLat_second((Double)latmap.get("second"));
		
		GetCoordinateActivity.geoinfo = geoinfo;
		
		this.lonview.setText(geoinfo.getLon_degree()+"°"+geoinfo.getLon_minute()+"'"+geoinfo.getLon_second()+"''");
		this.laview.setText(geoinfo.getLat_degree()+"°"+geoinfo.getLat_minute()+"'"+geoinfo.getLat_second()+"''"
				+"\n"+geoinfo.getAddress()+"\n"+geoinfo.getProvince()+"\n"+geoinfo.getCity()+"\n"+geoinfo.getDistrict());
//		txtLat.setText("\n百度定位信息:\n纬度:" + sb.toString());
		//设置地图放大级别
		mBaiduMap.setMaxAndMinZoomLevel(19, 3);
		 // 开启定位图层  
        mBaiduMap.setMyLocationEnabled(true); 
		//在地图上设置定位目标地址
		// 构造定位数据  
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy(location.getRadius())  
		    // 此处设置开发者获取到的方向信息，顺时针0-360  
		    .direction(100).latitude(location.getLatitude())  
		    .longitude(location.getLongitude()).build();  
		// 设置定位数据  
		mBaiduMap.setMyLocationData(locData);  
		// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）  
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
		    .fromResource(R.drawable.icon_geo);  
		MyLocationConfiguration config = new MyLocationConfiguration(LocationMode.FOLLOWING, true, mCurrentMarker);  
		mBaiduMap.setMyLocationConfigeration(config);
		Log.d("Baidu LBS info: ",sb.toString());
	}
	//获取度分秒
		public Map getDufenmiao(double i){
			int x1 = (int)i;
			double x2t = ((i-x1)*60);
			int x2 = (int)x2t;
			double x3 = ((x2t-x2)*60);
			
			Map map = new HashMap();
			map.put("degree", x1);
			map.put("minute", x2);
			map.put("second", x3);
			return map;
		}
		*/
		
}