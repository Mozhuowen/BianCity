package com.putaotown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.GetPutaosListener;
import com.putaotown.net.RequestUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.net.objects.RequestGetputao;
import com.putaotown.tools.BitmapUtil;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditTownActivity extends Activity
{
	private static ApplyTown townobj;
	private ImageView coverimage;
	private ImageView headimage;
	private TextView townnameview;
	private TextView coordview;
	private TextView usernameview;
	private TextView subcriptionview;
	private TextView discriview;
	private Button btncreate;
	private ListView putaolistview;
	private Handler messhandler;
	Map<View,Object> recycleResource = new HashMap<View,Object>();	//需要回收内存的view及其资源装载以便回收
	
	public static final int STARTACTION_PUTAO = 200;
	private SystemBarTintManager mTintManager;
	
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_townedit);
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
		
//		initViews();
	}
	
/*	@Override
	protected void onStart() {
		super.onStart();
		ViewTreeObserver vto = coverimage.getViewTreeObserver(); 
		ViewTreeObserver vto1 = headimage.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
		    public boolean onPreDraw() { 
		        int height = coverimage.getMeasuredHeight(); 
		        int width = coverimage.getMeasuredWidth(); 		
		        SLoadImage.getInstance().loadImage(coverimage, townobj.getCover(), 800, 800);	
		        //add to recycle
				recycleResource.put(coverimage,townobj.getCover() + 800);
		        return true; 
		    } 
		});
		vto1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
		    public boolean onPreDraw() { 
		        int height = headimage.getMeasuredHeight(); 
		        int width = headimage.getMeasuredWidth(); 		
//		        SLoadImage.getInstance().loadImage(headimage, UserPreUtil.getUserCover(), 90, 90);
		        SLoadImage.getInstance().loadImage(headimage, UserPreUtil.getUserCover(), 90, 90, true);
		        //add to recycle
				recycleResource.put(headimage,UserPreUtil.getUserCover() + 90 );
		        return true; 
		    } 
		});
	}*/
	
	public void initViews() {
		this.putaolistview = (ListView)findViewById(R.id.activity_town_edit_putaolist);
		this.coverimage = (ImageView)findViewById(R.id.activity_town_edit_cover);
		this.headimage = (ImageView)findViewById(R.id.activity_town_edit_headimage);
		this.townnameview = (TextView)findViewById(R.id.activity_town_edit_townname);
		this.coordview = (TextView)findViewById(R.id.activity_town_edit_coordinate);
		this.usernameview = (TextView)findViewById(R.id.activity_town_edit_username);
		this.subcriptionview = (TextView)findViewById(R.id.activity_town_edit_subscription);
		this.discriview = (TextView)findViewById(R.id.activity_town_edit_descri);
		btncreate = (Button)findViewById(R.id.activity_town_edit_btncreate);
		//set data
		this.townnameview.setText(townobj.getTownname());
		this.usernameview.setText(UserPreUtil.getUsername());
		this.discriview.setText("简介: "+townobj.getDescri());
		this.coordview.setText(townobj.getCoordstr());
		//load image
		
		//绑定创建葡萄事件
		btncreate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				CreatePutaoActivity.startAction(EditTownActivity.this,townobj.getTownid(),STARTACTION_PUTAO);
			}		
		});
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(EditTownActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(EditTownActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		//获取Putao列表
		this.getPutaos();
	}
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context,ApplyTown t,boolean ifshutdownpre) {
		townobj = t;
		Log.d("EditTownActivity townobj townid info: ", townobj.getTownid()+"");
		Intent intent = new Intent(context,EditTownActivity.class);
		((Activity)context).startActivity(intent);
		if (ifshutdownpre) {
			((Activity)context).finish();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case STARTACTION_PUTAO:
			switch(resultCode){
			case RESULT_OK:	//成功，更新putao列表
				List<PackagePutao> pt = (List<PackagePutao>)intent.getSerializableExtra("putao");
				List<View> listviews = new ArrayList<View>();
				for (int i=0;i<pt.size();i++) {
					listviews.add(new ListItemPutao(this,this,pt.get(i)).makeItemView());
				}
				MyListAdapter myadapter = new MyListAdapter(this,listviews);
				this.putaolistview.setAdapter(myadapter);
				setListViewHeightBasedOnChildren(putaolistview);
				break;
			}
			break;
		}
	}
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
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
    }
	
	public void onFinishNetwork(List<PackagePutao> p) {
		List<View> listviews = new ArrayList<View>();
		for (int i=0;i<p.size();i++) {
			ListItemPutao item = new ListItemPutao(this,this,p.get(i));
			View view = item.makeItemView();
			listviews.add(view);
			//add to recycle
			recycleResource.put(item.imagev,p.get(i).getCover() + 90);
		}
		MyListAdapter myadapter = new MyListAdapter(this,listviews);
		this.putaolistview.setAdapter(myadapter);
		setListViewHeightBasedOnChildren(putaolistview);
	}
	public void onNetworkFail() {
		
	}
	/**
	 *  获取putao-json
	 */
	private void getPutaos() {
		int townid = townobj.getTownid();
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
//				LogUtil.v("EdittownActivity info: ", "Recycle one CircleImageView resource!");
//				((ImageView)view).setImageBitmap(null);
//			} else if (view instanceof ImageView) {
//				LogUtil.v("EdittownActivity info: ", "Recycle one imageview resource!");
//				((ImageView)view).setImageBitmap(null);
//				SLoadImage.getInstance().delBitmapFromMemoryCache((String)recycleResource.get(view));
//			} 
//		}
		System.gc();
	}
}