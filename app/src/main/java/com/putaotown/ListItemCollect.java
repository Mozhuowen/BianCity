package com.putaotown;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemCollect
{
	private Object putao;
	private Context context;
	private Activity activity;
	
	LayoutInflater layoutInflater;
	View listItem;
	
	public ListItemCollect(Context context,Activity activity,Object putao) {
		this.context = context;
		this.putao = putao;
		layoutInflater = LayoutInflater.from(context);
		listItem = layoutInflater.inflate(R.layout.listitem_collect, null);
		this.activity = activity;
	}
	
	public View makeItemView() {
		View divider = layoutInflater.inflate(R.layout.listview_divider, null);
		
		ImageView imagev = (ImageView)listItem.findViewById(R.id.listitem_collect_image);
		TextView titlev = (TextView)listItem.findViewById(R.id.listitem_collect_title);
		TextView goodv = (TextView)listItem.findViewById(R.id.listitem_collect_good);
		TextView datev = (TextView)listItem.findViewById(R.id.listitem_collect_date);
		
		//设置数据
//		imagev.setImageResource(R.drawable.p1);
		titlev.setText("欢迎来到我的葡萄小镇");
		datev.setText("2015-1-1");
		goodv.setText("100");
		
		//设置点击事件
		if(activity instanceof AttentionActivity)
			listItem.setOnClickListener((CollectActivity)activity);
		
		return listItem;
	}
	
	public int getItemHeight(){
		 return listItem.getHeight();
	}
}