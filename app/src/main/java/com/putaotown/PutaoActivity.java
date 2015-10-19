package com.putaotown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.putaotown.imageviewer.ImagePagerActivity;
import com.putaotown.markdown.MDReader;
import com.putaotown.net.CommentRequest;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.PackageComment;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class PutaoActivity extends Activity
{
	private LayoutInflater layoutInflater;
	static PackagePutao putao;
	/**
	 * Context
	 */
	private Context context;
	/**
	 *  评论列表listv
	 */
	private ListView listv;
	private ImageView usercover;
	private ImageView cover;
	private TextView title;
	private TextView username;
	private TextView content;
	private TextView createtime;
	private EditText commentcontent;
	private TextView submitcomment;
	private LinearLayout imagelayout;
	private LinearLayout imagelayout1;
	private LinearLayout imagelayout2;
	private ScrollView scrollview;
	private int dividerwidth = 0;
	private int screenWidth = 0;
	private DisplayMetrics  dm = new DisplayMetrics();	//屏幕分辨率
	Handler messhandler;
	Map<View,Object> recycleResource = new HashMap<View,Object>();	//需要回收内存的view及其资源装载以便回收
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_putao);
        initViews();
        
	}
	/**
	 * 在onstart中设置图片，目的是获取imageview的准确大小
	 */
	@Override
	protected void onStart() {
		super.onStart();
		ViewTreeObserver vto = cover.getViewTreeObserver(); 
		ViewTreeObserver vto1 = usercover.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
		    public boolean onPreDraw() { 
		        int height = cover.getMeasuredHeight(); 
		        int width = cover.getMeasuredWidth(); 		
//		        Log.d("PutaoActivity coverimage width info: ", ""+width);
//		        SLoadImage.getInstance().loadImage(cover, putao.getCover(), 800, 800);	
		        //add to recycle
				recycleResource.put(cover,putao.getCover() + 800);
		        return true; 
		    } 
		});
		vto1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
		    public boolean onPreDraw() { 
		        int height = usercover.getMeasuredHeight(); 
		        int width = usercover.getMeasuredWidth(); 		
//		        Log.d("PutaoActivity headimage width info: ", ""+width);
//		        SLoadImage.getInstance().loadImage(usercover, putao.getUsercover(), 90, 90);
		        //add to recycle
				recycleResource.put(usercover,"");
		        return true; 
		    } 
		});
	}
	
	public void initViews() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
		screenWidth = dm.widthPixels;
		dividerwidth = (screenWidth-(DensityUtil.dip2px(this, 80)*4))/5;
		this.imagelayout = (LinearLayout)findViewById(R.id.activity_putao_images);
		this.imagelayout1 = new LinearLayout(this);
		this.imagelayout2 = new LinearLayout(this);
		imagelayout2.setPadding(0, dividerwidth, 0, 0);
		imagelayout.addView(imagelayout1);
		imagelayout.addView(imagelayout2);
		layoutInflater = LayoutInflater.from(this);
		this.context = this.getApplicationContext();
		this.listv = (ListView)findViewById(R.id.activity_putao_listv);
		this.usercover = (ImageView)findViewById(R.id.activity_putao_usercover);
		this.cover = (ImageView)findViewById(R.id.activity_putao_cover);
		this.title = (TextView)findViewById(R.id.activity_putao_putaotitle);
		this.username = (TextView)findViewById(R.id.activity_putao_username);
		this.content = (TextView)findViewById(R.id.activity_putao_content);
		this.createtime = (TextView)findViewById(R.id.activity_putao_createtime);
		this.commentcontent = (EditText)findViewById(R.id.activity_putao_commentcontent);
		this.submitcomment = (TextView)findViewById(R.id.activity_putao_submitcomment);
		this.scrollview = (ScrollView)findViewById(R.id.activity_putao_scrollview);
		//apply data
		title.setText(putao.getTitle());
		username.setText(putao.getUsername());
//		content.setText(putao.getContent());
		//set markdown text
		content.setTextKeepState(new MDReader(putao.getContent()).getFormattedContent(),BufferType.SPANNABLE);
		createtime.setText(putao.getCreatetime());
		//loadimages
		int imagesize = this.putao.getImagenames().size();	
		if (imagesize < 5 && imagesize > 0 ) {
			for (int i=0;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				LinearLayout tmplayout2 = (LinearLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				tmplayout2.setPadding(dividerwidth, 0, 0, 0);
//				SLoadImage.getInstance().loadImage(imagetmp, putao.getImagenames().get(i), 100, 100);
				//bind incident
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout1.addView(targetimage);
				//add to recycle
				this.recycleResource.put(imagetmp, putao.getImagenames().get(i) + 100 );
			}
		} else if ( imagesize > 4 ) {
			for (int i=0;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				LinearLayout tmplayout2 = (LinearLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				tmplayout2.setPadding(dividerwidth, 0, 0, 0);
//				SLoadImage.getInstance().loadImage(imagetmp, putao.getImagenames().get(i), 100, 100);
				//bind incident
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout1.addView(targetimage);
				//add to recycle
				this.recycleResource.put(imagetmp, putao.getImagenames().get(i) + 100 );
			}
			for (int i=4;i<imagesize;i++) {
				View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
				LinearLayout tmplayout2 = (LinearLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				tmplayout2.setPadding(dividerwidth, 0, 0, 0);
//				SLoadImage.getInstance().loadImage(imagetmp, putao.getImagenames().get(i), 100, 100);
				//bind incident
				targetimage.setOnClickListener(new ViewImage(i));
				imagelayout2.addView(targetimage);
				//add to recycle
				this.recycleResource.put(imagetmp, putao.getImagenames().get(i) + 100 );
			}
		}
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(PutaoActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(PutaoActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
		//提交评论监听事件
		this.submitcomment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String content = PutaoActivity.this.commentcontent.getText().toString();
				if (content == null || content.length()==0)
					return;
				int putaoid = PutaoActivity.this.putao.getPutaoid();
				int townid = PutaoActivity.this.putao.getTownid();
				
				CommentRequest postrequest = new CommentRequest(PutaoActivity.this,messhandler,putaoid,townid,content,0);
				postrequest.submitComment();
				PutaoActivity.this.commentcontent.setText("");
				PutaoActivity.this.commentcontent.setHint("  说点什么吧");
				//隐藏输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);     
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				
			}
			
		});
		
		//加载评论
		loadComments();
	}
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	/**
	 * 获取服务端返回的数据，更新评论列表
	 * @param comments
	 */
	public void onFinishNetwork(List<PackageComment> comments) {
		/*List<View> listviews = new ArrayList<View>();
		for (int i=0;i<comments.size();i++) {
			ListItemComment item = new ListItemComment(this,comments.get(i));
			View view = item.makeItemView();
			listviews.add(view);
			//add to recycle
			recycleResource.put(item.imagev,"");
		}
		MyListAdapter myadapter = new MyListAdapter(context,listviews);		
		listv.setAdapter(myadapter);
//		listv.setSelection(5); //不起作用
		//滚动到顶端
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				scrollview.scrollTo(0, 0);
			}
			
		}, 200);
		setListViewHeightBasedOnChildren(listv);*/
	}
	
	public void onNetworkFail() {
		
	}
	/**
	 * 加载评论
	 */
	public void loadComments() {
		int putaoid = PutaoActivity.this.putao.getPutaoid();
		CommentRequest postrequest = new CommentRequest(PutaoActivity.this,messhandler,putaoid);
		postrequest.loadComments();
	}
	
	/**
	 * 重置listv的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight() + 4;  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) /*+ DensityUtil.dip2px(this, 42)*/;  
//        params.height = 692;
        Log.d("list view height", ""+params.height);
        listView.setLayoutParams(params);  
    }
	
	/**
	 * 启动照片查看器
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
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
	
	public static void startAction(Context context,PackagePutao pt) {
		putao = pt;
		Intent intent = new Intent(context,PutaoActivity.class);
		((Activity)context).startActivity(intent);
	}
	/**
	 * 设置回复监听事件
	 * @author awen
	 *
	 */
	public class OnReply implements OnClickListener
	{
		String targetcontent = null;
		public OnReply(String username,String beRplycontent) {
			targetcontent = "//@" + username + ":" + beRplycontent;
		}
		@Override
		public void onClick(View v) {
			PutaoActivity.this.commentcontent.setText(targetcontent);		
			//弹出输入法
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);     
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			PutaoActivity.this.commentcontent.setFocusable(true);
			PutaoActivity.this.commentcontent.setSelection(0);
			PutaoActivity.this.commentcontent.requestFocus();
		}
		
	}
	/**
	 * 释放资源
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
//		Set<View> set = recycleResource.keySet();
//		for (Iterator it = set.iterator();it.hasNext();) {
//			View view = (View)it.next();
//			if (view instanceof CircleImageView) {
//				LogUtil.v("PutaoActivity info: ", "Recycle one CircleImageView resource!");
//				((ImageView)view).setImageBitmap(null);
//			} else if (view instanceof ImageView) {
//				LogUtil.v("PutaoActivity info: ", "Recycle one imageview resource!");
//				((ImageView)view).setImageBitmap(null);
//				SLoadImage.getInstance().delBitmapFromMemoryCache((String)recycleResource.get(view));
//			} 
//		}
		System.gc();
	}
}