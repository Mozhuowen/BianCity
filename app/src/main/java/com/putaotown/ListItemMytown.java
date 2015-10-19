package com.putaotown;

import java.util.List;

import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.ApplyTown;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemMytown
{
	private Object putao;
	private Context context;
	private Activity activity;
	private ApplyTown townobj;
	public ImageView imagev;
	
	LayoutInflater layoutInflater;
	View listItem;
	
	public ListItemMytown(Context context,Activity activity,ApplyTown town) {
		this.context = context;
		this.townobj = town;
		layoutInflater = LayoutInflater.from(context);
		listItem = layoutInflater.inflate(R.layout.listitem_mytown, null);
		this.activity = activity;
	}
	
	public View makeItemView() {
		View divider = layoutInflater.inflate(R.layout.listview_divider, null);
		
		imagev = (ImageView)listItem.findViewById(R.id.listitem_mytown_image);
		TextView titlev = (TextView)listItem.findViewById(R.id.listitem_mytown_title);
		TextView goodv = (TextView)listItem.findViewById(R.id.listitem_mytown_good);
		TextView datev = (TextView)listItem.findViewById(R.id.listitem_mytown_date);
		
		//设置数据
		titlev.setText(townobj.getTownname());
		datev.setText(townobj.getCreatetime());
		goodv.setText("100");
		//load image
//		SLoadImage loadimage = SLoadImage.getInstance();
//		loadimage.loadImage(imagev, townobj.getCover(), 100, 100);
		//绑定点击事件
		listItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				EditTownActivity.startAction(context, townobj, false);
			}
			
		});
		
		return listItem;
	}
	
	public int getItemHeight(){
		 return listItem.getHeight();
	}
}