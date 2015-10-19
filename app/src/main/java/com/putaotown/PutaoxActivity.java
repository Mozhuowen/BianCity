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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.putaotown.imageviewer.ImagePagerActivity;
import com.putaotown.markdown.MDReader;
import com.putaotown.net.CommentRequest;
import com.putaotown.net.DeleteUtil;
import com.putaotown.net.FavoriteUtil;
import com.putaotown.net.GoodUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackageComment;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class PutaoxActivity extends AppCompatActivity implements IXListViewListener,BaseActivity,OnClickListener,OnTouchListener 
{
	private LayoutInflater layoutInflater;
	static PackagePutao putao;
	static ModelUser mOwner;
	public static boolean mIsEditModel = false;	//判断是否进入编辑模式界面
	private String mRelyContent = "";
	private int mReplyId;				//记录当前回复的的commentid
	private boolean isGooding = false;
	private boolean isGood = false;
	private boolean isGoodPutao = true; //控制赞葡萄还是评论
	private boolean isFavoring = true;	//控制是否点击收藏等待网络响应

	private Context context;
	private View maincontent;
	private XListView mXListView;
	private MultiAdapter maincontentAdapter;
	private List<View> adapterList = new ArrayList<View>();
	private ListView listv;
	private List<View> commentlistviews = new ArrayList<View>();
	private List<PackageComment> mToShowData = new ArrayList<PackageComment>();
	private boolean refreshcomment = false;	//控制加载评论还是刷新评论
	private boolean noMore = false;	//控制是否还有未加载完的评论
	
	private View mDelete;
	private TextView mCurrGood;
	private ImageView mCurrGoodThumb;
	private View mBottomhint;
	private TextView mBottomText;
	private ImageView usercover;
	private ImageView cover;
	private TextView title;
	private TextView username;
	private TextView content;
	private TextView createtime;
	private EditText commentcontent;
	private TextView submitcomment;
	private TextView mGood;
    private ImageView mGoodImage;
	private TextView mFavoriteview;
	private LinearLayout imagelayout;
	private LinearLayout imagelayout1;
	private LinearLayout imagelayout2;
	private View mPickupcomment;
	private View mBtncomment;
	private double dividerwidth = 0;
	private int screenWidth = 0;
	private DisplayMetrics dm = new DisplayMetrics(); // 屏幕分辨率
	Handler messhandler;
	/**需要回收内存的view及其资源装载以便回收*/
	Map<View, ImgRecy> mRecycleResource = new HashMap<View, ImgRecy>();
	private Dialog dialog2;
	private Builder builder;
	private SystemBarTintManager mTintManager;
	InputMethodManager imm ;	//全局输入法控制
	
	/**重新加载图片*/
	@Override
	public void onStart() {
		LogUtil.v("PutaoxActivity info:", "onStart! start to reload image bitmap");
		super.onStart();
	}
	/**回收图片*/
	@Override
	public void onStop() {
		LogUtil.v("PutaoxActivity info:", "onStop! start to recycle image bitmap");
		super.onStop();
	}
	/**保存数据*/
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("putao", putao);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtil.v("PutaoxActivity info: ", "onCreate!");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null ) {
			LogUtil.v("PutaoxActivity info: ", "onCreate! savedInstanceState is not null!");
			putao = (PackagePutao)savedInstanceState.getSerializable("putao");
		}
		setContentView(R.layout.activity_putaox);
		initViews();
	}

	public void initViews() {
		this.context = this.getApplicationContext();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		//中间XListView
		this.mXListView = (XListView)findViewById(R.id.activity_putaox_xlistview);
		this.mXListView.setPullLoadEnable(true);
		this.mXListView.setXListViewListener(this);
		this.mXListView.setActivityTouchEvent(this);
		
		dividerwidth = (screenWidth - (DensityUtil.dip2px(this, 80) * 4)) / 5;
		layoutInflater = LayoutInflater.from(this);
		//葡萄属性view
		maincontent = layoutInflater.inflate(R.layout.view_putao_maincontent,
				null);
		//初始化maincontent中的view
		this.imagelayout = (LinearLayout) maincontent.findViewById(R.id.view_putao_maincontent_images);
		if (this.putao.getImagenames().size() > 0) {			
			this.imagelayout1 = new LinearLayout(this);
			this.imagelayout2 = new LinearLayout(this);
			imagelayout2.setPadding(0, (int)dividerwidth, 0, 0);
			imagelayout.addView(imagelayout1);
			imagelayout.addView(imagelayout2);		
		} else
			this.imagelayout.setVisibility(View.GONE);
		
		this.mBottomhint = maincontent.findViewById(R.id.view_putao_hint);
		this.usercover = (ImageView) maincontent.findViewById(R.id.view_putao_maincontent_usercover);
		this.cover = (ImageView) maincontent.findViewById(R.id.view_putao_maincontent_cover);
		this.title = (TextView) maincontent.findViewById(R.id.view_putao_maincontent_putaotitle);
		this.username = (TextView) maincontent.findViewById(R.id.view_putao_maincontent_username);
		this.content = (TextView) maincontent.findViewById(R.id.view_putao_maincontent_content);
		this.createtime = (TextView) maincontent.findViewById(R.id.view_putao_maincontent_createtime);
		this.commentcontent = (EditText) findViewById(R.id.activity_putaox_commentcontent);
		this.submitcomment = (TextView) findViewById(R.id.activity_putaox_submitcomment);
		this.mGood = (TextView)maincontent.findViewById(R.id.view_putao_maincontent_good);
        this.mGoodImage = (ImageView) maincontent.findViewById(R.id.view_putao_maincontent_good_image);
		this.mFavoriteview = (TextView)maincontent.findViewById(R.id.view_putao_maincontent_favorite);
		this.mPickupcomment = findViewById(R.id.activity_putaox_commentedit);
//		if (!mIsEditModel)
//			this.mDelete.setVisibility(View.GONE);
		// set data
		title.setText(putao.getTitle());
		username.setText(putao.getUsername());
//		content.setText(putao.getContent());
		//set mark down text
		content.setTextKeepState(new MDReader(putao.getContent()).getFormattedContent(),BufferType.SPANNABLE);
		createtime.setText(getString(R.string.putao_publishtime,putao.getCreatetime()));
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				BianImageLoader.getInstance().loadImage(cover, putao.getCover(),500);
				loadUserCover(usercover,putao.getUsercover());
			}
			
		}, 300);				
		
		//loadimages
		int imagesize = this.putao.getImagenames().size();	
		if (imagesize < 5 && imagesize > 0 ) {
			for (int i=0;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				imagetmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
				tmplayout2.setPadding((int)dividerwidth, 0, 0, 0);
				BianImageLoader.getInstance().loadImage(imagetmp, putao.getImagenames().get(i),100);
				//bind incident
				targetimage.setFocusable(true);
				targetimage.setClickable(true);
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout1.addView(targetimage);
				//add to recycle
				this.mRecycleResource.put(imagetmp, new ImgRecy(putao.getImagenames().get(i),100));
			}
		} else if ( imagesize > 4 ) {
			for (int i=0;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				imagetmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
				tmplayout2.setPadding((int)dividerwidth, 0, 0, 0);
				BianImageLoader.getInstance().loadImage(imagetmp, putao.getImagenames().get(i),100);
				//bind incident
				targetimage.setFocusable(true);
				targetimage.setClickable(true);
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout1.addView(targetimage);
				//add to recycle
				this.mRecycleResource.put(imagetmp, new ImgRecy(putao.getImagenames().get(i),100));
			}
			for (int i=4;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				imagetmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
				tmplayout2.setPadding((int)dividerwidth, 0, 0, 0);
				BianImageLoader.getInstance().loadImage(imagetmp, putao.getImagenames().get(i),100);
				//bind incident
				targetimage.setFocusable(true);
				targetimage.setClickable(true);
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout2.addView(targetimage);
				//add to recycle
				this.mRecycleResource.put(imagetmp, new ImgRecy(putao.getImagenames().get(i),100));
			}
		}
		
		//add to adapter
//		adapterList.add(maincontent);
		this.maincontentAdapter = new MultiAdapter(this,maincontent,this.mToShowData,MultiAdapter.SHOWTYPE_COMMENT);
		this.mXListView.setAdapter(maincontentAdapter);		
		this.mXListView.setOnScrollListener(maincontentAdapter);
		this.maincontentAdapter.notifyDataSetChanged();
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(PutaoxActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(PutaoxActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
		//提交评论监听事件
		this.submitcomment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				PutaoxActivity.this.mToShowData.removeAll(mToShowData);
				PutaoxActivity.this.refreshcomment = true;
				PutaoxActivity.this.noMore = false;
				String content = PutaoxActivity.this.commentcontent.getText().toString();
				if (content == null || content.length()==0)
					return;
				else{
//					content = content + mRelyContent;
				}
				int putaoid = PutaoxActivity.this.putao.getPutaoid();
				int townid = PutaoxActivity.this.putao.getTownid();
				CommentRequest postrequest = new CommentRequest(PutaoxActivity.this,messhandler,putaoid,townid,content,mReplyId);
				postrequest.submitComment();
				PutaoxActivity.this.commentcontent.setText("");
				PutaoxActivity.this.commentcontent.setHint("  说点什么吧");
				//隐藏输入法
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘	
				//清空被回复功能
				mRelyContent = "";
				mReplyId = 0;
			}
			
		});
		/**延迟加载评论*/
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				loadComments();
			}
			
		}, 300);
		/**加载葡萄点赞数据*/
		loadGood();
		/**加载收藏情况*/
		loadFavorite();
		/**set actionbar*/
		this.setActionBar();
	}
	
	/**加载用户头像*/
	private void loadUserCover(ImageView mCover,String cover) {
		BianImageLoader.getInstance().loadImage(mCover, cover,90);
			
	}
	/**
	 * 加载评论
	 */
	public void loadComments() {
		int putaoid = this.putao.getPutaoid();
		CommentRequest postrequest = new CommentRequest(this,messhandler,putaoid);
		postrequest.loadComments();
	}
	/**加载赞*/
	private void loadGood() {
		GoodUtil.getGoods(this, 1, putao.getPutaoid());
	}
	/**加载收藏情况*/
	private void loadFavorite() {
		FavoriteUtil.getFavorite(this, putao.getPutaoid());
	}
	/**
	 * 获取服务端返回的数据，更新评论列表
	 * @param comments
	 */
	public void onFinishNetwork(List<PackageComment> comments) {
		if (comments.size() > 0)
			this.mBottomhint.setVisibility(View.GONE);
		this.mToShowData.addAll(comments);
		this.maincontentAdapter.isFirstEnter = true;
		
		this.maincontentAdapter.notifyDataSetChanged();
		
		if ( comments.size() < 10 )
			this.mXListView.setPullLoadEnable(false);
		else
			this.mXListView.setPullLoadEnable(true);
		
		if (refreshcomment) {
			//滚动到顶端
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					mXListView.setSelection(2);
				}
				
			}, 200);
		}
		//取消缓冲提示
		onLoad();
	}
	/**点赞网络信息返回*/
	@Override
	public void onGood(ModelGood good){
		LogUtil.v("TownActivity info: ", "onGood return!");
		this.isGooding = false;
		if (isGoodPutao) {        //赞葡萄还是评论
            mCurrGood = mGood;    //改变当前赞对象
            mCurrGoodThumb = mGoodImage;
        }
		this.mCurrGood.setText("" + good.getGoods());
		if (good.getDogood()) {
            this.mCurrGoodThumb.setImageResource(R.drawable.ic_list_thumbup);
			this.isGood = true;
			//bind good accident
			if (isGoodPutao) {	//如果是赞葡萄需重新绑定事件
				this.mCurrGoodThumb.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (!isGooding){
							isGooding = true;
							isGoodPutao = true;
							GoodUtil.doGood(PutaoxActivity.this, 1, putao.getPutaoid(),1);
						}
					}				
				});
			}
		} else {
            this.mCurrGoodThumb.setImageResource(R.drawable.ic_list_thumb);
			this.isGood = Boolean.valueOf(false);
			//bind good accident
			if (isGoodPutao) {	//如果是赞葡萄需重新绑定事件
				this.mCurrGoodThumb.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if (!isGooding){
							isGooding = true;
							isGoodPutao = true;
							GoodUtil.doGood(PutaoxActivity.this, 1, putao.getPutaoid(),0);
						}
					}				
				});
			}
		}
	}
	/**收藏网络返回*/
	@Override
	public void onFavorite(ModelFavorite favorite) {
		LogUtil.v("PutaoxActivity info:", "onFavorite return!");
		this.isFavoring = false;
		if (favorite.getDofavori()) {
			this.mFavoriteview.setText(getString(R.string.favorite_no));
			this.mFavoriteview.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					FavoriteUtil.doFavorite(PutaoxActivity.this, putao.getPutaoid(), 1);
				}});
		} else {
			this.mFavoriteview.setText(getString(R.string.favorite_yes));
			this.mFavoriteview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoriteUtil.doFavorite(PutaoxActivity.this, putao.getPutaoid(), 0);
                }
            });
		}
	}
	
	public void onNetworkFail() {
		//停止缓冲动画
		onLoad();
	}
	
	public static void startAction(Context context,PackagePutao pt) {
		putao = pt;
		Intent intent = new Intent(context,PutaoxActivity.class);
		context.startActivity(intent);
	}
	public static void startAction(Activity context,PackagePutao pt,boolean isEdit) {
		putao = pt;
		mIsEditModel = isEdit;
		Intent intent = new Intent(context,PutaoxActivity.class);
		context.startActivityForResult(intent, TownActivity.STARTACTION_PUTAO);
	}
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	/**
	 * 启动照片查看器
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
		LogUtil.v("PutaoxActivity info: ", "Image was clicked!");
		Intent intent = new Intent(this, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		this.startActivity(intent);
	}
	
	/**
	 * 点击图片启动图片查看器，未绑定view组件
	 * @author awen
	 *
	 */
	private class ViewImage implements OnClickListener
	{
		int position = 0;
		public ViewImage(int p) {
			this.position = p;
		}
		@Override
		public void onClick(View v) {
			imageBrower(position,new ArrayList(putao.getImagenames()));
		}	
	}
	/**停止刷新动画*/
	private void onLoad() {
		this.mXListView.stopRefresh();
		this.mXListView.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXListView.setRefreshTime(datestr);
	}
	@Override
	public void onRefresh() {
		this.mToShowData.removeAll(mToShowData);
		this.refreshcomment = true;
		this.noMore = false;
		
		int putaoid = this.putao.getPutaoid();
		CommentRequest postrequest = new CommentRequest(this,messhandler,putaoid);
		postrequest.loadComments();	
	}

	@Override
	public void onLoadMore() {
		this.refreshcomment = false;
		if (this.noMore) {
			onLoad();
			return;
		}
		int putaoid = this.putao.getPutaoid();
		CommentRequest postrequest = new CommentRequest(this,messhandler,putaoid);
		postrequest.loadMoreComments(this.mToShowData.size());
	}
	/**评论点赞监听*/
	public class GoodListener implements OnClickListener
	{
		private TextView goodview;
        private ImageView thumbview;
		private int commentid;
		private boolean actionGood;
		
		public GoodListener(TextView t,ImageView i,int commentid,boolean actiongood) {
			this.goodview = t;
            this.thumbview = i;
			this.commentid = commentid;
			this.actionGood = actiongood;
		}
		@Override
		public void onClick(View v) {
			if (!isGooding){
				isGooding = true;
				isGoodPutao = false;
				mCurrGood = goodview;
                mCurrGoodThumb = thumbview;
				if (actionGood){
					GoodUtil.doGood(PutaoxActivity.this, 2, commentid,0);
					actionGood = !actionGood;
				} else {
					GoodUtil.doGood(PutaoxActivity.this, 2, commentid,1);
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
		int replyid;
		public OnReply(String username,String beRplycontent,int replyid) {
			content = "//<font color='#1E90FF'>@" + username + "</font>:" + beRplycontent;
			this.username = username;
			this.replyid = replyid;
		}
		@Override
		public void onClick(View v) {
			mReplyId = replyid;
			PutaoxActivity.this.mPickupcomment.setVisibility(View.VISIBLE);
			PutaoxActivity.this.commentcontent.setHint("回复： "+this.username);
			//弹出输入法
			PutaoxActivity.this.commentcontent.setFocusable(true);
			PutaoxActivity.this.commentcontent.setSelection(0);
			PutaoxActivity.this.commentcontent.requestFocus();
			//强制显示键盘
			imm.showSoftInput(commentcontent, InputMethodManager.SHOW_FORCED, null);
		}
		
	}
	/**点击评论头像监听事件*/
	public class OnOpenUser implements OnClickListener
	{
		ModelUser targetuser;
		public OnOpenUser(ModelUser mu) {
			this.targetuser = mu;
		}
		@Override
		public void onClick(View v) {
			if (targetuser != null)
				UserActivity.startAction(PutaoxActivity.this, targetuser);
		}
	}
	/**xml绑定的头像点击事件*/
	public void openUser(View v) {
		this.mOwner = new ModelUser();
		mOwner.setCover(putao.getUsercover());
		mOwner.setName(putao.getUsername());
		mOwner.setUserid(putao.getUserid());
		UserActivity.startAction(this, mOwner);
	}
	/**
	 * 释放资源
	 */
	@Override
	public void onDestroy() {
		LogUtil.v("PutaoxActivity info: ", "onDestroy!");
		super.onDestroy();
		this.setContentView(R.layout.view_null);
		messhandler.removeCallbacksAndMessages(null);
		System.gc();
	}
	/**获取订阅情况返回,此处用不上*/
	public void onSubscri(ModelSubscriTown subscri){
		
	}
	/**显示评论编辑框*/
	public void pickUpComment(View view) {
//		LogUtil.v("PutaoxActivity info: ", "pick up comment!");
		if (this.mPickupcomment.getVisibility() == View.GONE) {
			this.mPickupcomment.setVisibility(View.VISIBLE);
			commentcontent.setFocusable(true);
			commentcontent.setSelection(0);
			commentcontent.requestFocus();
			//强制显示键盘
			imm.showSoftInput(commentcontent, InputMethodManager.SHOW_FORCED, null);						
		} else {
			commentcontent.requestFocus();
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
			this.mRelyContent = "";
			this.mReplyId = 0;
			PutaoxActivity.this.commentcontent.setText("");
			PutaoxActivity.this.commentcontent.setHint("  说点什么吧");
			this.mPickupcomment.setVisibility(View.GONE);			
		}
	}
	public void hideCommentBox() {
		commentcontent.requestFocus();
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
		this.mRelyContent = "";
		this.mReplyId = 0;
		PutaoxActivity.this.commentcontent.setText("");
		PutaoxActivity.this.commentcontent.setHint("  说点什么吧");
		this.mPickupcomment.setVisibility(View.GONE);
	}
	@Override
	public void onDelete(ResponseSimple res) {
		if (res.isStat()) {
			Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
			this.setResult(RESULT_OK);
			finish();
		} else 
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();	
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
//		case R.id.activity_putaox_delete:
//			alerDeleteDialog();
//			break;
		case R.id.dialog_delete_delete:
			dialog2.dismiss();
			DeleteUtil.delete(this,1, putao.getPutaoid());
			break;
		case R.id.dialog_delete_cancel:
			dialog2.dismiss();			
			break;
		}	
	}
	/**弹出删除对话框*/
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
		actionbar.setDisplayShowCustomEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_story, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		this.mBtncomment = v.findViewById(R.id.actionbar_comment);
		this.mBtncomment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				pickUpComment(null);
			}			
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mIsEditModel)
			getMenuInflater().inflate(R.menu.mystory, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	    	finish();
	        return true; 
	    case R.id.actionbar_story_delete:
	    	this.alerDeleteDialog();
	    	return true;
	        default:
	        	return true;
	    }  
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.hideCommentBox();
		return false;
	}
}