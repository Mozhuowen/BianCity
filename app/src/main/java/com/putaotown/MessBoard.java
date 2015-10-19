package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MessBoard extends Activity 
{
	/**
	 * Context
	 */
	private Context context;
	/**
	 *  列表listv
	 */
	private ListView listv;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_messageboard);
        initViews();
        
      //test code
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<View> listviews = new ArrayList<View>();
		        /*listviews.add(new ListItemMessboard(context,new Object()).makeItemView());
		        listviews.add(new ListItemMessboard(context,new Object()).makeItemView());
		        listviews.add(new ListItemMessboard(context,new Object()).makeItemView());*/

		        MyListAdapter myadapter = new MyListAdapter(context,listviews);
				listv.setAdapter(myadapter);
				setListViewHeightBasedOnChildren(listv);
			}
        	
        },2000);
	}
	
	private void initViews() {
		this.listv = (ListView)findViewById(R.id.activity_messboard_listv);
		this.context = this.getApplicationContext();
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
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
//        params.height = 692;
        Log.d("list view height", ""+totalHeight);
        listView.setLayoutParams(params);  
    }
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
}