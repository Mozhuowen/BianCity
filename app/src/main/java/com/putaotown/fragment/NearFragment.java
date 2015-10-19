package com.putaotown.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.putaotown.BaseTownShow;
import com.putaotown.MyListAdapter;
import com.putaotown.R;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.LoadTownUtil;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class NearFragment extends Fragment implements BaseTownShow,IXListViewListener,AMapLocationListener,OnClickListener
{
	private View mainview;
	private GeoInfo geo = new GeoInfo();
	private TextView mTexthint;
	private LinearLayout mMainLayout;
	private XListView mXList;
	private MyListAdapter mMainAdapter;
	private List<View> mAdapterList = new ArrayList<View>();
	private List<View> mTownviewlist = new ArrayList<View>();
	private List<ApplyTown> towndata = new ArrayList<ApplyTown>();
	private List<MyListAdapter.ImgThing> mImgThings = new ArrayList<MyListAdapter.ImgThing>();	
	private List<Integer> mRejectids= new ArrayList<Integer>();
	private boolean isRefresh = false;
	private LocationManagerProxy mLocationManagerProxy;
	
	private int requestCount = 0;	//发起定位请求次数，超过一定次数仍未能定位则放弃，以防一直耗电
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mainview == null ){
			mainview = inflater.inflate(R.layout.fragment_near, container,false);
			this.mMainLayout = (LinearLayout)mainview.findViewById(R.id.fragment_near_mainlayout);
			this.mTexthint = (TextView)mainview.findViewById(R.id.fragment_near_texthint);
			this.mTexthint.setOnClickListener(this);
			this.mTexthint.setClickable(false);
			mXList = (XListView)mainview.findViewById(R.id.fragment_near_listv);
			mXList.setPullLoadEnable(true);
			mXList.setPullRefreshEnable(true);
			mXList.setXListViewListener(this);
	
			this.mMainAdapter = new MyListAdapter(this.getActivity(),this.towndata);
			mXList.setOnScrollListener(mMainAdapter);
			this.mXList.setAdapter(mMainAdapter);
			
			initLocation();
		}
		return mainview;
	}
	
	private void loadNear() {
		//成功获取坐标
		if (this.geo.getLatitude() != 0 && this.geo.getLongitude() != 0) {
			this.stopLocation();
			this.loadNear(geo, mRejectids);
		} else {	//获取坐标失败，提示信息关闭定位
			this.stopLocation();
			onLoad();
			this.mTexthint.setClickable(true);
			this.mTexthint.setText(getString(R.string.get_no_position));
		}
	}
	
	public void initLocation() {
		LogUtil.v("NearFragment info: ", "enter initLocation()!");
		mLocationManagerProxy = LocationManagerProxy.getInstance(this.getActivity());		 
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 3*1000, 5, this);
	}

	@Override
	public void onRefresh() {	
		this.mXList.setPullLoadEnable(false);
		initLocation();
		this.towndata.removeAll(towndata);
		this.mRejectids.removeAll(mRejectids);
		this.isRefresh = true;
		
	}

	@Override
	public void onLoadMore() {
		this.isRefresh = false;
		loadNear();
	}
	private void onLoad() {
		this.mXList.stopRefresh();
		this.mXList.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXList.setRefreshTime(datestr);
	}

	@Override
	public void loadHot(List<Integer> rejectid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadNear(GeoInfo geo,List<Integer> rejectid) {
		LoadTownUtil.loadNear(this, geo, mRejectids);
	}

	@Override
	public void onReceive(List<ApplyTown> towns) {
		LogUtil.v("HotFragment info: ", "onReceive!");		
		//停止缓冲动画
		onLoad();
		if (towns.size() == 0) {
			this.mTexthint.setText(getString(R.string.near_none));
			return;
		}
		//set pull load more enable
		this.mXList.setPullLoadEnable(true);
		//add to reject list
		for (int i=0;i<towns.size();i++) {
			this.mRejectids.add(towns.get(i).getTownid());
		}
		this.mMainLayout.removeView(mTexthint);
		this.mXList.setVisibility(View.VISIBLE);
		
		this.towndata.addAll(towns);
		
		this.mMainAdapter.notifyDataSetChanged();
		this.mMainAdapter.isFirstEnter = true;		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
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
	public void onLocationChanged(AMapLocation amapLocation) {
		if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
            //get geoinfo
            Double lati = amapLocation.getLatitude();
			Double logti = amapLocation.getLongitude();
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
			loadNear();
		} else {
			this.stopLocation();
			onLoad();
			this.mTexthint.setClickable(true);
			this.mTexthint.setText(getString(R.string.get_no_position));
        	LogUtil.v("NearFragment info: ", "enter onLocationChanged() + ERROR!");
			LogUtil.v("NearFragment info : AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
		
	}
	private void stopLocation() {
	    if (mLocationManagerProxy != null) {
	    	mLocationManagerProxy.removeUpdates(this);
	    	mLocationManagerProxy.destroy();
	    }
	    mLocationManagerProxy = null;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.fragment_near_texthint:
			this.mTexthint.setText(getString(R.string.xlistview_header_hint_loading));
			initLocation();
			this.mTexthint.setClickable(false);
			break;
		}
	}

}