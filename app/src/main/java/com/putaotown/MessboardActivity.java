package com.putaotown;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.GetPutaosListener;
import com.putaotown.net.GoodUtil;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.SubmitMessListener;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.net.objects.ModelMessboard;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessboardActivity extends Activity implements IXListViewListener,AMapLocationListener,BaseActivity
{
	private String mReplyContent = "";
	private final int DISTANCE = 50;	//规定距离内才能留言
	public static GeoInfo geoinfo;
	private XListView mXListView;
	private LayoutInflater mLayoutInflater;
	private TextView mTophint;
	private TextView mTexthint;
	private TextView bottomhint;
	private View mEditView;
	private EditText mEditcontent;
	private TextView mSubmit;
	private TextView mCurrGoodview;	//当前点赞的view
	private View hintview;
	private boolean refreshcomment = false;
	private boolean isGooding = false;
	
	/**xlistview的adapter*/
	private MyListAdapter mAdapter;
	/**上面adapter的存储view的list*/
	private List<View> mAdapterList = new ArrayList<View>();
	
	Handler messhandler;
	Map<View, Object> recycleResource = new HashMap<View, Object>(); // 需要回收内存的view及其资源装载以便回收
	
	private GeoInfo geo = new GeoInfo();
	private static ApplyTown mTown;
	private LocationManagerProxy mLocationManagerProxy;
	private SystemBarTintManager mTintManager;
	InputMethodManager imm;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_messboard);
		//设置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
  		mTintManager = new SystemBarTintManager(this);
  		mTintManager.setStatusBarTintEnabled(true);
  		mTintManager.setNavigationBarTintEnabled(true);
  		mTintManager.setTintColor(this.getResources().getColor(R.color.basecolor));
		
		initView();
		loadMess();
		initLocation();
	}
	
	public void initView(){
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mLayoutInflater = LayoutInflater.from(this);
		this.mXListView = (XListView)this.findViewById(R.id.activity_messboard_xlist);
		this.mXListView.setPullLoadEnable(false);
		this.mXListView.setPullRefreshEnable(true);
		this.mXListView.setXListViewListener(this);
		
		this.mTophint = (TextView)findViewById(R.id.view_messboard_hint);
		this.mEditView = findViewById(R.id.activity_messboard_messedit);
		this.mEditcontent = (EditText)findViewById(R.id.activity_messboard_messcontent);
		//first set editview invisible
		this.mEditView.setVisibility(View.INVISIBLE);
		this.mSubmit = (TextView)findViewById(R.id.activity_messboard_submitmess);
		
		hintview = mLayoutInflater.inflate(R.layout.view_messboard_hint, null);
		this.mTexthint = (TextView)hintview.findViewById(R.id.view_messboard_hint);
		this.mTexthint.setText(getString(R.string.messboard_no));
		this.mAdapterList.add(hintview);
		this.mAdapter = new MyListAdapter(this,mAdapterList);
		
		this.mXListView.setAdapter(mAdapter);
		
		//bind accident
		this.mSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MessboardActivity.this.refreshcomment = true;
				String content = MessboardActivity.this.mEditcontent.getText().toString();
				if (content == null || content.length()==0)
					return;
				else 
					content = content + mReplyContent;
				//隐藏输入法    
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
				//post request
				int townid = MessboardActivity.this.mTown.getTownid();
				ModelMessboard req = new ModelMessboard();
				req.setContent(content);
				req.setTownid(townid);
				req.setGeo(geo);
				//post request
				RequestUtil.getInstance().post("submitmess", req, new SubmitMessListener(MessboardActivity.this,messhandler)
				,new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						if(error!=null){
							Log.e("TAG volleyResponseError", error.toString());
							Message mess = new Message();
							mess.what = 200;
							messhandler.sendMessage(mess);
						}
					}
				});
				//重置编辑框
				MessboardActivity.this.mEditcontent.setText("");
				MessboardActivity.this.mEditcontent.setHint("  说点什么吧");
				mReplyContent = "";
			}
			
		});
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(MessboardActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(MessboardActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	public void loadMess(){
		//post request
		int townid = MessboardActivity.this.mTown.getTownid();
		ModelMessboard req = new ModelMessboard();
		req.setTownid(townid);
		req.setMessposition(0);
		//post request
		RequestUtil.getInstance().post("getmess", req, new SubmitMessListener(MessboardActivity.this,messhandler)
		,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					Log.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});

	}
	/**
	 * 计算距离
	 */
	public void calculateDistance(Double lati,Double logti) {
		LatLng startpos = new LatLng(mTown.getGeoinfo().getLatitude(),mTown.getGeoinfo().getLongitude());
		LatLng endpos = new LatLng(lati,logti);
		float distan = AMapUtils.calculateLineDistance(startpos,endpos);
		if ( distan < DISTANCE) {
			LogUtil.v("MessboardActivity info: ", "distance is in 50!");
			this.mEditView.setVisibility(View.VISIBLE);
			this.mTophint.setText(getString(R.string.messboard_canmess));
			//stop location
			this.stopLocation();
		} else {
			LogUtil.v("MessboardActivity info: ", "distance is out of 50!");
			this.mTophint.setText(getString(R.string.messboard_showdistan,(int)distan));
		}
	}
	
	public void initLocation() {
		LogUtil.v("MessboardActivity info: ", "enter initLocation()!");
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		 
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 5*1000, 5, this);
	}
	/**网络返回葡萄*/
	public void onFinishNetwork(List<ModelMessboard> mess) {
		onLoad();
		if (mess.size() == 0 ) {
			this.mXListView.setPullLoadEnable(false);
			return;
		}
		List<View> listview = new ArrayList<View>();
		for (int i=0;i<mess.size();i++) {
			ListItemMessboard item = new ListItemMessboard(this,mess.get(i));
			View view = item.makeItemView();
			listview.add(view);
			//add to recycle
			recycleResource.put(item.imagev,"");
		}
		LogUtil.v("MessboardActivity info: ", " mAdapterList size: "+this.mAdapterList.size());
		if (this.mAdapterList.size() == 1 ) { 	//第一次加载			
			this.mAdapterList.remove(0);
			this.mAdapterList.addAll(listview);
		} else if(this.refreshcomment) {		//refresh
			this.mAdapterList.removeAll(mAdapterList);
			this.mAdapterList.addAll(listview);
		} else {								//load more
			this.mAdapterList.addAll(listview);
		}
		
		this.mAdapter.notifyDataSetChanged();
		if (this.mAdapterList.size() < 10 )
			this.mXListView.setPullLoadEnable(false);
		else
			this.mXListView.setPullLoadEnable(true);
		
		if (refreshcomment) {
			//滚动到顶端
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					mXListView.setSelection(1);
				}
				
			}, 200);
		}
	}
	/**点赞返回*/
	@Override
	public void onGood(ModelGood good){
		this.isGooding = false;
		//设置拇指图标
		Drawable thumb = this.getResources().getDrawable(R.drawable.ic_list_thumb);
		Drawable thumbup = this.getResources().getDrawable(R.drawable.ic_list_thumbup);
		thumb.setBounds(0, 0, thumb.getMinimumWidth(), thumb.getMinimumHeight());
		thumbup.setBounds(0, 0, thumbup.getMinimumWidth(), thumbup.getMinimumHeight());
		this.mCurrGoodview.setText(""+good.getGoods());
		if (good.getDogood())
			this.mCurrGoodview.setCompoundDrawables(null, null, thumbup, null);
		else
			this.mCurrGoodview.setCompoundDrawables(null, null, thumb, null);
		
	}

	public void onNetworkFail() {
		
	}		

	private void onLoad() {
		this.mXListView.stopRefresh();
		this.mXListView.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXListView.setRefreshTime(datestr);
	}
	@Override
	public void onRefresh() {
		this.mXListView.setPullLoadEnable(true);
		this.refreshcomment = true;
		this.loadMess();
	}

	@Override
	public void onLoadMore() {
		this.refreshcomment = false;
		//post request
		int townid = MessboardActivity.this.mTown.getTownid();
		ModelMessboard req = new ModelMessboard();
		req.setTownid(townid);
		req.setMessposition(this.mAdapterList.size());
		//post request
		RequestUtil.getInstance().post("getmess", req, new SubmitMessListener(MessboardActivity.this,messhandler)
		,new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				if(error!=null){
					Log.e("TAG volleyResponseError", error.toString());
					Message mess = new Message();
					mess.what = 200;
					messhandler.sendMessage(mess);
				}
			}
		});
	}
	
	/**评论点赞监听*/
	public class GoodListener implements OnClickListener
	{
		private TextView goodview;
		private int messid;
		private boolean actionGood;
		
		public GoodListener(TextView t,int messid,boolean actiongood) {
			this.goodview = t;
			this.messid = messid;
			this.actionGood = actiongood;
		}
		@Override
		public void onClick(View v) {
			if (!isGooding){
				isGooding = true;
				mCurrGoodview = goodview;
				if (actionGood){
					GoodUtil.doGood(MessboardActivity.this, 3, messid,0);
					actionGood = !actionGood;
				} else {
					GoodUtil.doGood(MessboardActivity.this, 3, messid,1);
					actionGood = !actionGood;
				}
			}
		}
		
	}	
	/**
	 * 设置回复监听事件
	 * @author awen
	 *
	 */
	public class OnReply implements OnClickListener
	{
		String username = null;
		String content = null;
		public OnReply(String username,String beRplycontent) {
			content = "//<font color='#1E90FF'>@" + username + "</font>:" + beRplycontent;
			this.username = username;
		}
		@Override
		public void onClick(View v) {
			mReplyContent = content;
//			MessboardActivity.this.mEditcontent.setText(Html.fromHtml(targetcontent));		
			MessboardActivity.this.mEditcontent.setHint("回复: "+this.username);
			//弹出输入法
			MessboardActivity.this.mEditcontent.setFocusable(true);
			MessboardActivity.this.mEditcontent.setSelection(0);
			MessboardActivity.this.mEditcontent.requestFocus();
			//强制显示键盘
			imm.showSoftInput(mEditcontent, InputMethodManager.SHOW_FORCED, null);
		}
		
	}
	/**点击头像监听事件*/
	public class OnOpenUser implements OnClickListener
	{
		ModelUser user;
		public OnOpenUser(ModelUser mu) {
			this.user = mu;
		}
		
		@Override
		public void onClick(View v) {
			if (user != null)
				UserActivity.startAction(MessboardActivity.this, user);
		}
		
	}
	
	public static void startAction(Context context,ApplyTown town) {
		mTown = town;
		Intent intent = new Intent(context,MessboardActivity.class);
		context.startActivity(intent);
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
	public void onPause() {
		super.onPause();
		stopLocation();
	}
	
	public void backEvent(View source){
		finish();
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		LogUtil.v("MessboardActivity info: ", "enter onLocationChanged()!" + amapLocation.getLatitude());
		if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
			LogUtil.v("MessboardActivity info: ", "enter onLocationChanged()!" + " enter IF!");
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
			this.calculateDistance(lati, logti);
        } else {
        	LogUtil.v("MessboardActivity info: ", "enter onLocationChanged() + ERROR!");
			Log.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
		
	}
	private void stopLocation() {
	    if (mLocationManagerProxy != null) {
	    	mLocationManagerProxy.removeUpdates(this);
	    	mLocationManagerProxy.destroy();
	    }
	    mLocationManagerProxy = null;
	}
	/**获取订阅情况返回,此处用不上*/
	public void onSubscri(ModelSubscriTown subscri){
		
	}

	@Override
	public void onFavorite(ModelFavorite favorite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDelete(ResponseSimple res) {
		// TODO Auto-generated method stub
		
	}
}