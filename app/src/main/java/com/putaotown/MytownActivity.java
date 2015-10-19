package com.putaotown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MytownActivity extends Activity implements OnClickListener
{
	private static List<ApplyTown> townobjs;
	/**
	 * Context
	 */
	private Context context;
	/**
	 * 标题layout
	 */
	private TextView titleView;
	/**
	 *  列表listv
	 */
	private ListView listv;
	Map<View,Object> recycleResource = new HashMap<View,Object>();	//需要回收内存的view及其资源装载以便回收
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mytown);
        
        initViews();
        List<View> listviews = new ArrayList<View>();
        for (int i=0;i<townobjs.size();i++) {
        	ListItemMytown item = new ListItemMytown(this,MytownActivity.this,townobjs.get(i));
        	View view = item.makeItemView();
        	listviews.add(view);
        	//add to recycle
        	this.recycleResource.put(item.imagev,townobjs.get(i).getCover()+100);
        }
        
        MyListAdapter myadapter = new MyListAdapter(context,listviews);
		listv.setAdapter(myadapter);
		
		setListViewHeightBasedOnChildren(listv);
        //test code
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<View> listviews = new ArrayList<View>();
		        listviews.add(new ListItemMytown(context,MytownActivity.this,new Object()).makeItemView());
		        listviews.add(new ListItemMytown(context,MytownActivity.this,new Object()).makeItemView());
		        listviews.add(new ListItemMytown(context,MytownActivity.this,new Object()).makeItemView());
		        
		        MyListAdapter myadapter = new MyListAdapter(context,listviews);
				listv.setAdapter(myadapter);
				
				setListViewHeightBasedOnChildren(listv);
			}
        	
        },2000);*/
        
	}
	
	private void initViews() {
		this.context = this.getApplicationContext();
		this.listv = (ListView)findViewById(R.id.activity_mytown_listv);
		
		townobjs = UserPreUtil.getMyTowns();
	}
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
//				LogUtil.v("MytownActivity info: ", "Recycle one CircleImageView resource!");
//				((ImageView)view).setImageBitmap(null);
//			} else if (view instanceof ImageView) {
//				LogUtil.v("MytownActivity info: ", "Recycle one imageview resource!");
//				((ImageView)view).setImageBitmap(null);
//				SLoadImage.getInstance().delBitmapFromMemoryCache((String)recycleResource.get(view));
//			} 
//		}
		System.gc();
	}
}