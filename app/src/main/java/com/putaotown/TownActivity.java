package com.putaotown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.community.Community;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.DeleteUtil;
import com.putaotown.net.FansUtil;
import com.putaotown.net.GetPutaosListener;
import com.putaotown.net.GoodUtil;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.SubscriUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.net.objects.RequestGetputao;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class TownActivity extends AppCompatActivity implements IXListViewListener,BaseActivity,OnClickListener
{
	private static ApplyTown mTown;
	private static ModelUser mOwner;
	public static boolean mIsEditModel = false;	//判断是否进入编辑模式界面
	//处理宽度
	private DisplayMetrics  dm = new DisplayMetrics();
	public static final int STARTACTION_PUTAO = 200;
	private XListView mXListView;
	private LayoutInflater mLayoutInflater;
	/**上部小镇属性大view*/
	private View mMaincontent;
	/**xlistview的adapter*/
	private MultiAdapter mMaincontentAdapter;
	/**上面adapter的存储view的list*/
	private List<View> mAdapterList = new ArrayList<View>();
	private List<View> mPutaoList = new ArrayList<View>();
	private List<PackagePutao> mToShowData = new ArrayList<PackagePutao>();
	
	private ImageView mCoverView;
	private TextView mTownName;
	private TextView mGood;
	private ImageView mGoodImage;
	private TextView mDescription;
	private ImageView mUserCover;
	private TextView mUserName;
	private TextView mUserFans;
	private TextView mAddress;
	private TextView mSubcribe;
	private TextView mLeaveWord;
	private ImageView mGeoImage;
	private ImageView mActionCrate;
	private TextView mTitle;
	private TextView mStoryCount;
	private View mDelete;
	private View mBottomhint;
	private View mCreateActionView;
	private Boolean isGood = true;	//控制是否处于点赞状态
	private boolean isGooding = false;	//控制是否正在等待网络响应	
	private Dialog dialog2;
	private Builder builder;
	private SystemBarTintManager mTintManager;
	/**失败相关提示*/
	final Handler messhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {	//网络活动失败
				onNetworkFail();
				Toast.makeText(TownActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 300){
				onNetworkFail();
			} else {
				String mess = (String)msg.obj;
				Toast.makeText(TownActivity.this, mess, Toast.LENGTH_SHORT).show();
			}
		}
	};
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("tonwobj", mTown);
		savedInstanceState.putSerializable("ownerobj", mOwner);
		savedInstanceState.putSerializable("mIsEditModel", mIsEditModel);
	}
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		LogUtil.v("TownActivity info: ", "onCreate!");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null ) {
        	mTown = (ApplyTown)savedInstanceState.getSerializable("tonwobj");
        	mOwner = (ModelUser)savedInstanceState.getSerializable("ownerobj");
        }
        setContentView(R.layout.activity_town);

		//被回收后恢复
		if (savedInstanceState != null ){
			mTown = (ApplyTown) savedInstanceState.getSerializable("townobj");
			mOwner = (ModelUser) savedInstanceState.getSerializable("ownerobj");
			mIsEditModel = (Boolean) savedInstanceState.getSerializable("mIsEditModel");
		}
        initView();
    }

	private void initView(){
		this.setActionBar();
		mLayoutInflater = LayoutInflater.from(this);
		this.mXListView = (XListView)findViewById(R.id.activity_town_xlist);
		this.mXListView.setPullLoadEnable(false);
		this.mXListView.setPullRefreshEnable(true);
		this.mXListView.setXListViewListener(this);
		//get views
		this.mMaincontent = this.mLayoutInflater.inflate(R.layout.view_town_maincontent, null);
		this.mCoverView = (ImageView)this.mMaincontent.findViewById(R.id.view_town_maincontent_cover);
		this.mTownName = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_towname);
		this.mGood = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_good);
        this.mGoodImage = (ImageView) this.mMaincontent.findViewById(R.id.view_town_maincontent_good_image);
		this.mDescription = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_description);
		this.mUserCover = (ImageView)this.mMaincontent.findViewById(R.id.view_town_maincontent_usercover);
		this.mUserName = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_username);
		this.mUserFans = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_fans);
		this.mAddress = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_address);
		this.mLeaveWord = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_leaveword);
		this.mGeoImage = (ImageView)this.mMaincontent.findViewById(R.id.view_town_maincontent_geoimage);
//		this.mActionCrate = (ImageView)this.mMaincontent.findViewById(R.id.view_town_maincontent_actioncreate);
		this.mBottomhint = this.mMaincontent.findViewById(R.id.view_town_hint);
		this.mSubcribe = (TextView) this.mMaincontent.findViewById(R.id.view_town_maincontent_subscribe);
//		this.mCreateActionView = this.mMaincontent.findViewById(R.id.view_town_maincontent_actioncreate);
//		this.mTitle = (TextView)this.findViewById(R.id.activity_town_titletext);
//		mDelete = this.findViewById(R.id.activity_town_delete);
		this.mStoryCount = (TextView)this.mMaincontent.findViewById(R.id.view_town_maincontent_storycount);
		//设置删除图标的可见性
//		if (!mIsEditModel)
//			mDelete.setVisibility(View.GONE);
		//设置cover高度和宽度一致
		this.getWindowManager().getDefaultDisplay().getMetrics(dm); 

		float widthPixels= dm.widthPixels;
		LogUtil.v("TownActivity info: ", "set coverView layoutparams: "+widthPixels+" "+dm.heightPixels+" "+" url: "+mTown.getCover());
		this.mCoverView.setLayoutParams(new LinearLayout.LayoutParams((int)widthPixels,(int)widthPixels));
		//set data
		this.mTownName.setText(mTown.getTownname());
		this.mDescription.setText(getString(R.string.town_description,mTown.getDescri()));
		this.mUserName.setText(mOwner.getName());
		this.mUserFans.setText(getString(R.string.user_fans,mOwner.getFans()));
		this.mAddress.setText(mTown.getGeoinfo().getProvince()+mTown.getGeoinfo().getCity()+mTown.getGeoinfo().getFreeaddr());
		this.mStoryCount.setText(getString(R.string.story_count,mTown.getStorycount()));
		//加粗字体
		this.mTownName.getPaint().setFakeBoldText(true);
		//判断是否使用编辑模式
//		if (!this.mIsEditModel) {
//			this.mCreateActionView.setVisibility(View.GONE);
////			this.mTitle.setText("边城");
//		}
		BianImageLoader.getInstance().loadImage(this.mCoverView, mTown.getCover(),500);
		/**full url*/
		loadUserCover(this.mUserCover,mOwner.getCover());
		BianImageLoader.getInstance().loadImage(this.mGeoImage, mTown.getGeoinfo().getScreenpng(), 500);
	
		//bin accident
//		this.mActionCrate.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				CreatePutaoActivity.startAction(TownActivity.this, mTown.getTownid(), STARTACTION_PUTAO);
//			}
//			
//		});
		this.mGeoImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MapActivity.startAction(TownActivity.this, mTown.getGeoinfo());
			}
			
		});
		this.mLeaveWord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				MessboardActivity.startAction(TownActivity.this, mTown);
				Community.startAction(TownActivity.this,mTown.getTownid());
			}
			
		});	
		//set adapter
		this.mMaincontentAdapter = new MultiAdapter(this,mMaincontent,this.mToShowData,MultiAdapter.SHOWTYPE_PUTAO);
		this.mXListView.setAdapter(mMaincontentAdapter);
		this.mXListView.setOnScrollListener(mMaincontentAdapter);
		
		//load putao
		loadPutao();
		//load good
		loadGood();
		//load subscri
		loadSubscri();
		//load fans
		loadFans();
		
	}
	/**加载葡萄网络请求*/
	private void loadPutao() {
		int townid = mTown.getTownid();
		RequestGetputao req = new RequestGetputao();
		req.setTownid(townid);
		//get putao from net
		RequestUtil.getInstance().post("getputao", req, new GetPutaosListener(this,messhandler)
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
	/**加载赞*/
	private void loadGood() {
		GoodUtil.getGoods(this, 0, mTown.getTownid());
	}
	/**加载订阅情况*/
	private void loadSubscri() {
		SubscriUtil.getSubscri(this, mTown.getTownid());
	}
	/**加载粉丝*/
	private void loadFans() {
		FansUtil.getFans(this, mOwner.getUserid());
	}
	/**加载葡萄网络返回*/
	public void onFinishNetwork(List<PackagePutao> p) {
		if (p.size() > 0)
			mBottomhint.setVisibility(View.GONE);
		if (mIsEditModel) {
			UserPreUtil.updateStoryCount(mTown.getTownid(), p.size());
			mTown.setStorycount(p.size());
			this.mStoryCount.setText(getString(R.string.story_count,mTown.getStorycount()));
		}
		this.mToShowData.addAll(p);		
		this.mMaincontentAdapter.isFirstEnter = true;
		this.mMaincontentAdapter.notifyDataSetChanged();
		
		if ( p.size() < 10 )
			this.mXListView.setPullLoadEnable(false);
		else
			this.mXListView.setPullLoadEnable(true);
		
		onLoad();
	}
	/**点赞网络信息返回*/
	@Override
	public void onGood(ModelGood good){
		LogUtil.v("TownActivity info: ", "onGood return!");
		this.isGooding = false;
		this.mGood.setText(""+good.getGoods());
		if (good.getDogood()) {
            this.mGoodImage.setImageResource(R.drawable.ic_list_thumbup);
			this.isGood = true;
			//bind good accident
			this.mGoodImage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (!isGooding){
						isGooding = true;
						GoodUtil.doGood(TownActivity.this, 0, mTown.getTownid(),1);
					}
				}				
			});
		} else {
            this.mGoodImage.setImageResource(R.drawable.ic_list_thumb);
			this.isGood = Boolean.valueOf(false);
			//bind good accident
			this.mGoodImage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (!isGooding){
						isGooding = true;
						GoodUtil.doGood(TownActivity.this, 0, mTown.getTownid(),0);
					}
				}				
			});
		}
	}
	/**获取订阅情况返回*/
	public void onSubscri(ModelSubscriTown subscri){
		LogUtil.v("TownActivity info: ", "onSubscri return!");
		if (subscri.getDosubscri()) {
			this.mSubcribe.setText(getString(R.string.subscri_no));
			this.mSubcribe.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SubscriUtil.doSubscri(TownActivity.this, mTown.getTownid(), 1);
				}
				
			});
		} else {
			this.mSubcribe.setText(getString(R.string.subscri_yes));
			this.mSubcribe.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SubscriUtil.doSubscri(TownActivity.this, mTown.getTownid(), 0);
				}
				
			});
		}
	}
	/**获取粉丝返回*/
	public void onFans(ModelUser mu) {
		this.mUserFans.setText(getString(R.string.user_fans,mu.getFans()));
	}
	/**网络不给力*/
	public static void onNetworkFail() {
		
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case STARTACTION_PUTAO:
			switch(resultCode){
			case RESULT_OK:	//成功，更新putao列表
				onRefresh();
				break;
			}
			break;
		}
	}
	/**加载用户头像*/
	private void loadUserCover(ImageView mCover,String cover) {
		BianImageLoader.getInstance().loadImage(mCover, cover,90);
	}
	
	public static void startAction(Context context1,ApplyTown town) {
		mTown = town;
		Intent intent = new Intent(context1,TownActivity.class);
		context1.startActivity(intent);
	}
	public static void startAction(Context context1,ApplyTown town,ModelUser u) {
		mIsEditModel = false;
		mTown = town;
		mOwner = u;
		Intent intent = new Intent(context1,TownActivity.class);
		context1.startActivity(intent);
	}
	public static void startAction(Activity context1,ApplyTown town,ModelUser u,boolean isedit) {
		mTown = town;
		mOwner = u;
		mIsEditModel = isedit;
		Intent intent = new Intent(context1,TownActivity.class);
		context1.startActivityForResult(intent, 400);
	}
	public void backEvent(View source){
		finish();
	}
	/**结束下拉上拉动画*/
	private void onLoad() {
		this.mXListView.stopRefresh();
		this.mXListView.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXListView.setRefreshTime(datestr);
	}
	/**下拉刷新事件*/
	@Override
	public void onRefresh() {
		this.mXListView.setPullLoadEnable(true);
		this.mToShowData.removeAll(mToShowData);
		this.loadPutao();
	}
	/**上拉LoadMore*/
	@Override
	public void onLoadMore() {
		this.mMaincontentAdapter.isFirstEnter = true;
		
		int townid = mTown.getTownid();
		RequestGetputao req = new RequestGetputao();
		req.setTownid(townid);
		req.setPosition(this.mToShowData.size());
		//get putao from net
		RequestUtil.getInstance().post("getputao", req, new GetPutaosListener(this,messhandler)
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
	/**xml绑定的头像点击事件*/
	public void openUser(View v) {
		UserActivity.startAction(this, mOwner);
	}

	@Override
	public void onFavorite(ModelFavorite favorite) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDelete(ResponseSimple res) {
		if (res.isStat()) {
			Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
			UserPreUtil.deleteOneTown(mTown.getTownid());
			this.setResult(400);
			finish();
		} else 
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
//		case R.id.activity_town_delete:
//			alerDeleteDialog();
//			break;
		case R.id.dialog_delete_delete:
			dialog2.dismiss();
			DeleteUtil.delete(this,0, mTown.getTownid());
			break;
		case R.id.dialog_delete_cancel:
			dialog2.dismiss();			
			break;
		}
	}
	
	public void alerDeleteDialog() {
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
		View delete = view.findViewById(R.id.dialog_delete_delete);
		View cancel = view.findViewById(R.id.dialog_delete_cancel);
		delete.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();	
	}
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mIsEditModel)
			getMenuInflater().inflate(R.menu.mytown, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        finish(); 
	        return true; 
	    case R.id.actionbar_town_delete:
	    	this.alerDeleteDialog();
	    	return true;
	    case R.id.actionbar_town_create:
	    	CreatePutaoActivity.startAction(TownActivity.this, mTown.getTownid(), STARTACTION_PUTAO);
	    	return true;
	        default:
	        	return true;
	    }  
	}
}