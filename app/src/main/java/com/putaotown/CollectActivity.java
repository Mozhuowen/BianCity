package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import com.putaotown.net.objects.PackagePutao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CollectActivity extends Activity implements OnClickListener
{
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
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);
        
        initViews();
      //test code
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<View> listviews = new ArrayList<View>();
		        listviews.add(new ListItemPutao(context,CollectActivity.this,new PackagePutao()).makeItemView());
		        listviews.add(new ListItemPutao(context,CollectActivity.this,new PackagePutao()).makeItemView());
		        listviews.add(new ListItemPutao(context,CollectActivity.this,new PackagePutao()).makeItemView());
		        
		        MyListAdapter myadapter = new MyListAdapter(context,listviews);
				listv.setAdapter(myadapter);
				
				setListViewHeightBasedOnChildren(listv);
			}
        	
        },2000);
        
	}
	
	private void initViews() {
		this.context = this.getApplicationContext();
		this.listv = (ListView)findViewById(R.id.activity_collect_listv);
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
}