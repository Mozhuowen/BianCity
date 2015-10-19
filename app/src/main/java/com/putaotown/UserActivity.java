package com.putaotown;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.UserUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends Activity implements IXListViewListener,UserInterface
{
	static ModelUser mUser;
	private TextView mUserName;
	private ImageView mBgwall;
	private ImageView mCover;
	private TextView mSex;
	private TextView mLocation;
	private TextView mTowncount;
	private TextView mPutaocount;
	private TextView mFanscount;
	private TextView mSubscricount;
	private TextView mFavoritecount;
	private TextView mBegoodcount;
	private TextView mTitle;
	private XListView mXList;
	private View mMainContent;
	private View mBottomhint;
	private ImageView mUserWall;
	
	private LayoutInflater layoutInflater;
	private MultiAdapter mMainAdapter;
	private List<View> mAdapterList = new ArrayList<View>();
	private List<View> mOldtownlist = new ArrayList<View>();
	private List<ApplyTown> mToShowData = new ArrayList<ApplyTown>();
	/**需要回收内存的view及其资源装载以便回收*/
	Map<View, ImgRecy> mRecycleResource = new HashMap<View, ImgRecy>();
	private SystemBarTintManager mTintManager;
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user);
		
		initViews();
	}
	
	private void initViews() {
		layoutInflater = LayoutInflater.from(this);
		this.mMainContent = layoutInflater.inflate(R.layout.view_user_maincontent, null);
		this.mXList = (XListView)findViewById(R.id.activity_user_xlistview);
		this.mXList.setPullRefreshEnable(true);
		this.mXList.setPullLoadEnable(false);
		this.mXList.setXListViewListener(this);
		
		//get views
//		this.mTitle = (TextView)findViewById(R.id.activity_user_title);
		this.mUserName = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_username);
		this.mBgwall = (ImageView)this.mMainContent.findViewById(R.id.view_user_maincontent_wall);
		this.mCover = (ImageView)this.mMainContent.findViewById(R.id.view_user_maincontent_cover);
		this.mSex = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_usersex);
		this.mLocation = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_userlocation);
		this.mTowncount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_towncount);
		this.mPutaocount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_putaocount);
		this.mFanscount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_fanscount);
		this.mSubscricount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_subscicount);
		this.mFavoritecount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_favoritecount);
		this.mBegoodcount = (TextView)this.mMainContent.findViewById(R.id.view_user_maincontent_bbscount);
		this.mBottomhint = this.mMainContent.findViewById(R.id.view_user_maincontent_hint);
		//set some data
//		this.mTitle.setText(mUser.getName());
		this.mUserName.setText(mUser.getName());
		loadUserCover(mCover,mUser.getCover());
		
		this.mMainAdapter = new MultiAdapter(this,this.mMainContent,this.mToShowData,MultiAdapter.SHOWTYPE_TOWN);
		this.mXList.setAdapter(mMainAdapter);
		this.mXList.setOnScrollListener(mMainAdapter);
		
		getUserInfo();
	}
	
	public void getUserInfo() {
		UserUtil.getUserInfo(this, mUser);
	}
	@Override
	public void onNetWorkFinish(ModelUser user) {
		onLoad();
//		this.mUser = null;
//		this.mUser = user;
		//set data
		this.mUserName.setText(user.getName());
		this.mLocation.setText(user.getLocation());
		this.mTowncount.setText(user.getTowncount()+"");
		this.mPutaocount.setText(user.getPutaocount()+"");
		this.mFanscount.setText(user.getFans()+"");
		this.mSubscricount.setText(user.getSubscricount()+"");
		this.mFavoritecount.setText(user.getFavoritecount()+"");
		this.mBegoodcount.setText(user.getBbscount()+"");
		if ("m".equals(user.getSex())) {
			this.mSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sex_boy, 0, 0, 0);
			this.mSex.setText("男");
		} else {
			this.mSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sex_girl, 0, 0, 0);
			this.mSex.setText("女");
		}
		
		if (user.getWallimage() != null && user.getWallimage().length() > 0)
//			SLoadImage.getInstance().loadImage(this.mBgwall, user.getWallimage(), 500, 500, new ImgRecy(user.getWallimage(),500).irank);
			BianImageLoader.getInstance().loadImage(this.mBgwall, user.getWallimage(), 500);
		List<View> townviewlist = new ArrayList<View>();
		List<ApplyTown> list = user.getMytowns();
		if (list.size() > 0 )
			this.mBottomhint.setVisibility(View.GONE);
		this.mToShowData.addAll(list);
		this.mMainAdapter.isFirstEnter = true;
		this.mMainAdapter.notifyDataSetChanged();
	}
	/**加载用户头像*/
	private void loadUserCover(ImageView mCover,String cover) {
		BianImageLoader.getInstance().loadImage(mCover, cover, 90);
//		if (cover.contains("http")) {
//			SLoadImage.getInstance().loadImage(mCover, cover, 90, 90,true);			
//		} else {
//			ImgRecy ir = new ImgRecy(cover,90);
//			SLoadImage.getInstance().loadImage(mCover, cover, ir.size,ir.size,ir.irank);
//		}
			
	}
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context,ModelUser user) {
		mUser = user;
		Intent intent = new Intent(context,UserActivity.class);
		context.startActivity(intent);
	}
	
	private void onLoad() {
		this.mXList.stopRefresh();
		this.mXList.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXList.setRefreshTime(datestr);
	}

	@Override
	public void onRefresh() {
		getUserInfo();		
		this.mToShowData.removeAll(mToShowData);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
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
		this.mBgwall.setBackgroundResource(0);
		this.setContentView(R.layout.view_null);
		System.gc();
	}
}