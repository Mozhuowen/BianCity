package com.putaotown;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.putaotown.adapter.BCMainAdapter;
import com.putaotown.fragment.CreateDialogFragment;
import com.putaotown.fragment.HotFragment;
import com.putaotown.fragment.LoadingFragment;
import com.putaotown.fragment.MineFragmentx;
import com.putaotown.fragment.MsgFragment;
import com.putaotown.fragment.NearFragment;
import com.putaotown.fragment.PersonFragment;
import com.putaotown.fragment.UpdateDialogFragment;
import com.putaotown.localio.PushMessUtil;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UpdateUtil;
import com.putaotown.net.objects.ModelAppUpdate;
import com.putaotown.tools.LogUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.reflect.Field;

public class MainActivity extends android.support.v7.app.AppCompatActivity implements UpdateCallBack {

	public static boolean isInit = false;
	private Context context;
	private static int mCurrPos;
	private Dialog dialog2;
	private ModelAppUpdate m;	//升级信息
	public static final int STARTACTION_REGISTE = 200;
	public static final int STARTACTION_USER = 300;
	public static final int STARTACTION_LOGIN = 400;
	public static final int RESULTCODE_LOGIN_CREATETOWN = 300;
	public static final int STARTACTION_GETCOORDINATE = 500;
	public static final int EXIT = 600;

	private HotFragment hotFragment;
	private NearFragment nearFragment;
	private MineFragmentx mineFragment;
	private MsgFragment msgFragment;
	private PersonFragment personFragment;
	private View hotLayout;
	private View nearLayout;
	private View mineLayout;
	private View newsLayout;
	private FragmentManager fragmentManager;
	private TextView titleView;
	private LoadingFragment loading;
	private ImageView mImgHome;
	private ImageView mImgNearby;
	private ImageView mImgMessage;
	private ImageView mImgAccount;
	private static View mNewRedPos;
	private View mSearchView;
	private View mCreateView;
	
	private long mLastBacktime = 0;	//记录上次点击返回键时间，用于判断是否退出
	//new
	private static com.putaotown.views.PagerSlidingTabStrip tabs;
	private DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
  		//注册小米推送alias
  		MiPushClient.setAlias(MainActivity.this, String.valueOf(UserPreUtil.getUserid()), null);
  		this.setUpTabView();
		
		// 初始化布局元素
		initViews();
		//检查消息
		this.checkUnreadMess();
		
		//检查升级,延迟
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				checkUpdate();
			}		
		},5000);
		
		isInit = true;
	}
	/**setup tab view*/
	public void setUpTabView() {
		dm = getResources().getDisplayMetrics();
		ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);
		tabs = (com.putaotown.views.PagerSlidingTabStrip) findViewById(R.id.activity_main_tabs);
		pager.setAdapter(new BCMainAdapter(this.getSupportFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();
	}
	/**设置PagerSlidingTabStrip各项属性*/
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 0, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 0, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
//		tabs.setBackgroundResource(R.color.fb_blue);
	}

	/** 获取控件实例，绑定事件*/
	protected void initViews() {
		this.mCreateView = findViewById(R.id.activity_main_create);
		this.mSearchView = findViewById(R.id.activity_main_search);
		this.mSearchView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				searchActivity.startAction(MainActivity.this);
			}
			
		});
		this.mCreateView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FragmentManager fm = MainActivity.this.getSupportFragmentManager();
				CreateDialogFragment dialog = new CreateDialogFragment();
				dialog.show(fm, "date");
			}
			
		});
		UserPreUtil.updateHasTown();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("Main Activity", "start activity return!");
		switch (requestCode) {
		case 200:
			LogUtil.v("MainActivity :", "200 return!");
			mineFragment.onActivityResult(requestCode, resultCode, data);
			break;
		case 300:
			LogUtil.v("MainActivity :", "300 return!");
			mineFragment.onActivityResult(requestCode, resultCode, data);
			break;
		}
		switch(resultCode) {
		case EXIT:
			WelcomeActivity.startAction(this);
			this.finish();
			break;
		case 400:
			mineFragment.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {   
        	if (System.currentTimeMillis() - this.mLastBacktime < 2000){
        		LogUtil.v("MainActivity info: ", "System time: "+System.currentTimeMillis()+ " lastbacktime: "+this.mLastBacktime);
        		this.finish();
        		return false;
        	}
        	else{
        		LogUtil.v("MainActivity info: ", "System time: "+System.currentTimeMillis()+ " lastbacktime: "+this.mLastBacktime);
        		Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
        		this.mLastBacktime = System.currentTimeMillis();
        		return true;
        	}
        } else           
        	return false;           
    }  

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.setContentView(R.layout.view_null);
		System.gc();
	}

	/**检查升级网络返回
	 * @throws NameNotFoundException */
	@Override
	public void onReceive(ModelAppUpdate m) {
		int currVersionCode = 0;
		try {
			currVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(currVersionCode > 0 && m.getVersioncode() > currVersionCode) {
			this.m = m;
			FragmentManager fm = this.getSupportFragmentManager();
			UpdateDialogFragment dialog = new UpdateDialogFragment(this.m);
			dialog.show(fm, "updatedialog");
		} else {
			//已经是最近版本不用处理
//			Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
		}
	}
	/**检查升级*/
	public void checkUpdate() {
		try {
			UpdateUtil.checkUpdate(this);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**检查未读消息并提示*/
	public void checkUnreadMess() {
		int i = PushMessUtil.getAllUnreadCou();
		if ( i > 0 )
			this.tabs.showMessPos();
	}
	/**12版本bugfix，判断不为空，系统回收资源时很可能会变成空置*/
	public static void postMess(int type,Object object) {
		if ( tabs != null && tabs.selectedPosition != 2)
			tabs.showMessPos();
		if (MsgFragment.isShow)
			MsgFragment.addMess(type);
	}
	/**处理Actionbar*/
	public void BianCityActionbar() {
		ActionBar actionbar = this.getSupportActionBar();
//		ActionBar actionbar = getActionBar();
//		actionbar.setDisplayShowHomeEnabled(false);
//		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
//		View v = this.getLayoutInflater().inflate(R.layout.actionbar_search, null);
//		actionbar.setCustomView(R.layout.actionbar_search);
//		actionbar.setBackgroundDrawable(this.getResources().getDrawable(R.color.fb_blue));
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		LogUtil.v("MainActiity info: ", "actionbar heght: "+actionBarHeight + " "+tv.data);
		final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
            new int[] { android.R.attr.actionBarSize });
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		LogUtil.v("MainActiity info: ", "actionbar heght: "+actionBarHeight + " "+tv.data);
	}
	/**创建菜单*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/*设置隐藏菜单按钮总是显示*/
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
