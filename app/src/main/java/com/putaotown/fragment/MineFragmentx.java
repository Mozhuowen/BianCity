package com.putaotown.fragment;

import java.io.File;
import java.io.IOException;
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

import com.putaotown.CommunitylistActivity;
import com.putaotown.FavolistActivity;
import com.putaotown.GetGeoActivity;
import com.putaotown.ListItemFactory;
import com.putaotown.ListItemTown;
import com.putaotown.MultiAdapter;
import com.putaotown.MyListAdapter;
import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.SettingActivity;
import com.putaotown.SublistActivity;
import com.putaotown.UserInterface;
import com.putaotown.localio.FileIO;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.CWallUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.TownRequest;
import com.putaotown.net.UserUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.CharacterUtil;
import com.putaotown.tools.LogUtil;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MineFragmentx extends Fragment implements IXListViewListener,UserInterface,OnClickListener
{
	private View mainview;
	private boolean isFirstEnter = true;
	private String userwall;
	int mTownSize = 0;	//记录usrpre中有几个，如果没有变化则不更新列表
	LayoutInflater layoutInflater;
	Activity mContext;
	Activity mThisactivity;
	private View maincontent;
	private XListView mXListView;
	private MultiAdapter maincontentAdapter;
	private List<View> adapterList = new ArrayList<View>();
	private List<View> townlistviews = new ArrayList<View>();
	private List<MyListAdapter.ImgThing> mImgThings = new ArrayList<MyListAdapter.ImgThing>();
	private List<ApplyTown> mToShowData = new ArrayList<ApplyTown>();
	private View mBottomhint;
	private TextView username;
	private TextView usersex;
	private TextView userlocation;
	private ImageView usercover;
	private ImageView mAddtown;
	private View subslistlayout;	//需要绑定点击事件
	private View favolistlayout;	//需要绑定点击事件
	private View bbslayout;			//社区点击需要绑定事件
	private ImageView mUserWall;	//用户自定义墙纸
	private View mSetting;		//设置图标
	//red point
	private View subredpo;
	private View favoritepo;
	private View bbspo;
	/**弹出选择相片对话框*/
	Dialog dialog2;
	private Uri imageUri = null;
	/**事件判断*/
	public final int CROP_PIC = 200;
	public final int TAKE_PIC = 300;
	public final int DELE_TOWN = 400;
	/**上传对话框的进度条*/
	ProgressBar progressbar;	
	private Builder builder;
	private String imagewallpath;
	private static CWallUtil mCurrReq;
	private Handler messhandler;
	private String IMAGE_FILE_LOCATION;//temp file
	//计数
	private TextView mTowncount;
	private TextView mPutaocount;
	private TextView mFanscount;
	private TextView mSubscricount;
	private TextView mFavoritecount;
	private TextView mBBScount;
	/**需要回收内存的view及其资源装载以便回收*/
	Map<View, ImgRecy> mRecycleResource = new HashMap<View, ImgRecy>();
	
	/**此处重新从pre加载数据并刷新*/
	@Override
	public void onStart() {
		super.onStart();
		LogUtil.v("MineFragmentx info: ", "Resume!");
		//用户信息在这里设置
		loadUserCover();
		this.username.setText(UserPreUtil.getUsername());
		if ("m".equals(UserPreUtil.getSex())) {
			this.usersex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sex_boy, 0, 0, 0);
			this.usersex.setText("男");
		} else {
			this.usersex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sex_girl, 0, 0, 0);
			this.usersex.setText("女");
		}
		if (UserPreUtil.getLocation() != null)
			this.userlocation.setText(UserPreUtil.getLocation());
		else
			this.userlocation.setText("未知");
		
		if (!isFirstEnter) {
			this.mToShowData.removeAll(mToShowData);
			loadTownFromUserPre();
		}
		isFirstEnter = false;
	}
	/**设置图片内容为null*/
	@Override
	public void onStop() {
		LogUtil.v("TownActivity info:", "onStop! start to recycle image bitmap");
		super.onStop();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtil.v("MineFragmentx info: ", "onCreateView!");
		if (mainview == null ) {
			this.mContext = this.getActivity();
			layoutInflater = LayoutInflater.from(mContext);
			mainview = layoutInflater.inflate(R.layout.fragment_mine_xlist,container,false);
			this.mThisactivity = this.getActivity();
			//获取XListview并设置属性
			this.mXListView = (XListView)mainview.findViewById(R.id.fragment_mine_xlist_xlistview);
			this.mXListView.setPullLoadEnable(false);
			this.mXListView.setPullRefreshEnable(true);
			this.mXListView.setXListViewListener(this);
			//get view
			this.maincontent = layoutInflater.inflate(R.layout.view_mine_maincontent, null);
			this.usercover = (ImageView)maincontent.findViewById(R.id.view_mine_maincontent_cover);
			this.username = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_username);
			this.usersex = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_usersex);
			this.userlocation = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_userlocation);
			this.mAddtown = (ImageView)maincontent.findViewById(R.id.view_mine_maincontent_addtown);
			this.mTowncount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_towncount);
			this.mPutaocount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_putaocount);
			this.mFanscount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_fanscount);
			this.mSubscricount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_subscicount);
			this.mFavoritecount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_favoritecount);
			this.mBBScount = (TextView)maincontent.findViewById(R.id.view_mine_maincontent_bbscount);
			this.subslistlayout = maincontent.findViewById(R.id.view_mine_maincontent_subslayout);
			this.favolistlayout = maincontent.findViewById(R.id.view_mine_maincontent_favolayout);
			this.bbslayout = maincontent.findViewById(R.id.view_mine_maincontent_bbslayout);
			this.mBottomhint = maincontent.findViewById(R.id.view_mine_maincontent_hint);
			this.mUserWall = (ImageView)maincontent.findViewById(R.id.view_mine_maincontent_userwall);
			this.mSetting = maincontent.findViewById(R.id.view_mine_maincontent_setting);
			this.subredpo = maincontent.findViewById(R.id.view_mine_maincontent_subsciredpos);
			this.favoritepo = maincontent.findViewById(R.id.view_mine_maincontent_favoriteredpos);
			this.bbspo = maincontent.findViewById(R.id.view_mine_maincontent_bbsredpos);
			//set data		
			this.mAddtown.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					GetGeoActivity.startAction(mThisactivity);
				}
				
			});
			this.subslistlayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					subredpo.setVisibility(View.GONE);
					SublistActivity.startAction(getActivity());
				}
				
			});
			this.favolistlayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					favoritepo.setVisibility(View.GONE);
					FavolistActivity.startAction(getActivity());
				}
				
			});
			this.bbslayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bbspo.setVisibility(View.GONE);
					CommunitylistActivity.startAction(getActivity());
				}
				
			});
			this.mSetting.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SettingActivity.startAction(MineFragmentx.this.getActivity());
				}
				
			});
			this.mUserWall.setOnClickListener(this);
			
			
			//add to adapter
			this.adapterList.add(maincontent);
	
			this.maincontentAdapter = new MultiAdapter(this.getActivity(),maincontent,this.mToShowData,MultiAdapter.SHOWTYPE_MYTOWN);
			//set adapter
			this.mXListView.setAdapter(maincontentAdapter);
			//set OnScrollListener
			this.mXListView.setOnScrollListener(maincontentAdapter);
			
			this.loadTownFromUserPre();
			
			this.loadStatis();
			
			//handler吐司处理
			messhandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 200) {	//网络活动失败
						onNetworkFail();
						Toast.makeText(MineFragmentx.this.getActivity(), "网络不给力", Toast.LENGTH_SHORT).show();
					} else if (msg.what == 300){
						onNetworkFail();
					} else if (msg.what == 400){	//更新进度条
						int progress = (int)msg.arg1;
						progressbar.setProgress(progress);
					} else {
						onNetworkFail();
						String mess = (String)msg.obj;
						Toast.makeText(MineFragmentx.this.getActivity(), mess, Toast.LENGTH_SHORT).show();
					}
				}
			};
		}
		
		return mainview;
	}
	
	private void initView() {
		
	}
	/**重新加载图片*/
	public void reloadImage() {		
		Set<View> set = mRecycleResource.keySet();
		for (Iterator<View> it = set.iterator();it.hasNext();) {
			View view = it.next();
			ImgRecy ir = mRecycleResource.get(view);
			if (view instanceof CircleImageView && ir.imgname != null) {
//				SLoadImage.getInstance().loadImage((CircleImageView)view, ir.imgname, ir.size, ir.size, true);
				
			} else if (view instanceof ImageView && ir.imgname != null) {		
//				SLoadImage.getInstance().loadImage((ImageView)view, ir.imgname, ir.size, ir.size,ir.irank);
			} 
		}
	}
	/**加载用户头像*/
	private void loadUserCover() {
		String cover = UserPreUtil.getCover();
//		if (cover.contains("http")) {
//			SLoadImage.getInstance().loadImage(this.usercover, cover, 90, 90,true);			
//		} else {
//			ImgRecy ir = new ImgRecy(cover,90);
//			SLoadImage.getInstance().loadImage(this.usercover, cover, ir.size,ir.size,ir.irank);
//		}
		BianImageLoader.getInstance().loadImage(this.usercover, cover,90);
			
	}
	/**从本地加载用户信息*/
	private void loadTownFromUserPre() {
		List<ApplyTown> townlist = UserPreUtil.getMyTowns();
		if (townlist.size() > 0)
			this.mBottomhint.setVisibility(View.GONE);
		this.mToShowData.addAll(townlist);
		this.maincontentAdapter.isFirstEnter = true;
		this.maincontentAdapter.notifyDataSetChanged();
		
	}
	/**从网络获取town信息*/
	private void loadUserinfo() {
		ModelUser mu = new ModelUser();
		mu.setUserid(UserPreUtil.getUserid());
		mu.setOnlystatis(false);
		UserUtil.getUserInfo(this, mu);
	}
	/**获取统计数据*/
	private void loadStatis() {
		ModelUser mu = new ModelUser();
		mu.setUserid(UserPreUtil.getUserid());
		mu.setOnlystatis(true);
		UserUtil.getUserInfo(this, mu);
	}
	@Override
	public void onNetWorkFinish(ModelUser user) {
		//取消缓冲提示
		onLoad();
		//set data
		this.mTowncount.setText(user.getTowncount()+"");
		this.mPutaocount.setText(user.getPutaocount()+"");
		this.mFanscount.setText(user.getFans()+"");
		this.mSubscricount.setText(user.getSubscricount()+"");
		this.mFavoritecount.setText(user.getFavoritecount()+"");
		this.mBBScount.setText(user.getBbscount()+"");
		//update wall image
		if (user.getWallimage() != null && user.getWallimage().length() > 0)
//			SLoadImage.getInstance().loadImage(mUserWall, user.getWallimage(), 500, 500, new ImgRecy(user.getWallimage(),500).irank);
			BianImageLoader.getInstance().loadImage(mUserWall, user.getWallimage(),500);
		if (!user.getOnlystatis()) {
			this.mToShowData.removeAll(mToShowData);
			List<ApplyTown> list = user.getMytowns();
			UserPreUtil.updateAllMyTowns(list);
			if (list.size() > 0)
				this.mBottomhint.setVisibility(View.GONE);
			this.mToShowData.addAll(list);

			this.maincontentAdapter.isFirstEnter = true;
			this.maincontentAdapter.notifyDataSetChanged();
		}
	}
	/**修改墙纸网络回调*/
	public void onCWall(boolean success) {
		if (success) {
//			this.mUserWall.setImageBitmap(null);
			ImgRecy img = new ImgRecy(userwall,500);
//			SLoadImage.getInstance().loadImage(mUserWall, userwall, 500, 500, img.irank);
			BianImageLoader.getInstance().loadImage(mUserWall, userwall,500);
			dialog2.dismiss();
		}
	}
	
	private void onLoad() {
		this.mXListView.stopRefresh();
		this.mXListView.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXListView.setRefreshTime(datestr);
	}
	/**下拉刷新*/
	@Override
	public void onRefresh() {
		this.loadUserinfo();
		loadUserCover();
	}
	/**上拉加载更多*/
	@Override
	public void onLoadMore() {
		onLoad();		
	}
	/**释放资源*/
	@Override
	public void onDestroy() {
		super.onDestroy();
		Set<View> set = mRecycleResource.keySet();
		for (Iterator<View> it = set.iterator();it.hasNext();) {
			View view = it.next();
			if (view instanceof CircleImageView) {
				LogUtil.v("EdittownActivity info: ", "Recycle one CircleImageView resource!");
				((ImageView)view).setImageBitmap(null);
			} else if (view instanceof ImageView) {
				LogUtil.v("EdittownActivity info: ", "Recycle one imageview resource!");
				((ImageView)view).setImageBitmap(null);
				ImgRecy ir = mRecycleResource.get(view);
//				SLoadImage.getInstance().delBitmapFromMemoryCache(ir.getRecyStr());
			} 
		}
		System.gc();
	}
	/**响应点击墙纸,，弹出对话框等*/
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.dialog_album:	//从相册选择并裁剪照片
			LogUtil.d("CreateTownActivity", "test on click!");
			dialog2.dismiss();
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 5);
			intent.putExtra("aspectY", 3);
			intent.putExtra("outputX", 800);
			intent.putExtra("outputY", 480);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true);			
			             
	        startActivityForResult(intent, CROP_PIC);  
	        break;
		case R.id.dialog_takepic:
			dialog2.dismiss();
			Intent intent_takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
			intent_takepic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent_takepic, TAKE_PIC);
			break;
		case R.id.view_mine_maincontent_userwall:	//打开选择相片方式对话框
			//临时存储照片路径uri
			IMAGE_FILE_LOCATION = this.getActivity().getExternalCacheDir()+"/"+"temp.jpg";//temp file
			File file = new File(IMAGE_FILE_LOCATION);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LogUtil.d("File dir is: ", IMAGE_FILE_LOCATION);
			imageUri = Uri.fromFile(file);
			
			builder = new AlertDialog.Builder(this.getActivity(),AlertDialog.THEME_HOLO_LIGHT);	
			View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_createtown, null);
			View takepic = view.findViewById(R.id.dialog_takepic);
			View album = view.findViewById(R.id.dialog_album);
			takepic.setOnClickListener(this);
			album.setOnClickListener(this);
			
			builder.setView(view);
			dialog2 = builder.create();
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();	
			break;
		}
	}
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_PIC:
			if(imageUri != null && intent != null){
				this.userwall = CharacterUtil.getRandomString(32);
				this.imagewallpath = this.getActivity().getFilesDir() + "/image/" + userwall;
				try {
					File file = new File(imagewallpath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				//复制图片至目标路径
				FileIO.copyByUri(this.getActivity(),imagewallpath,imageUri);
				
			    Bitmap bitmap = FileIO.decodeUriAsBitmap(this.getActivity(),imageUri);//decode bitmap
			    this.mUserWall.setImageBitmap(bitmap);
			    alerLoading();
			    mCurrReq = new CWallUtil(this,this.progressbar,messhandler,this.userwall);
			    mCurrReq.doaction();
			}
			break;
		case TAKE_PIC:
			cropImageUri(imageUri, 800, 480, CROP_PIC);
			break;
		}
		
		switch(resultCode) {
		case DELE_TOWN:
			LogUtil.v("MineFragmentx receive resultCode:", ""+DELE_TOWN);
			this.loadUserinfo();
			break;
		}
	}
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(uri, "image/*");
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 5);
	    intent.putExtra("aspectY", 3);
	    intent.putExtra("outputX", 800);
	    intent.putExtra("outputY", 480);
	    intent.putExtra("scale", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    intent.putExtra("return-data", false);
	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
	    startActivityForResult(intent, CROP_PIC);
		}
	public void alerLoading() {
		Builder builder = new AlertDialog.Builder(this.getActivity(),AlertDialog.THEME_HOLO_LIGHT);	
//		DialogBack.Builder builder = new DialogBack.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
//		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
//		final GifMovieView gif1 = (GifMovieView)view.findViewById(R.id.dialog_loading_gif);
//		gif1.setMovieResource(R.drawable.loading_big);
		View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_progressbar, null);
		progressbar = (ProgressBar)view.findViewById(R.id.dialog_progressbar_bar);
		builder.setView(view);
		builder.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK  
                        && event.getRepeatCount() == 0) {  
					mCurrReq.cancelTask();  
                }  
				return false;
			}			
		});
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();
	}
	
	public void onNetworkFail() {
		dialog2.dismiss();
	}
}