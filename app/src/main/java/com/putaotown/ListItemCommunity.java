package com.putaotown;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.putaotown.community.Community;
import com.putaotown.community.models.ModelCommunity;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelUser;

public class ListItemCommunity
{
	private ModelCommunity data;
	private Activity activity;
	public ImageView imagev;
	LayoutInflater layoutInflater;
	View listItem;
	
	public ListItemCommunity(Activity ac,ModelCommunity m) {
		this.activity = ac;
		this.data = m;
		this.layoutInflater = LayoutInflater.from(ac);
		listItem = layoutInflater.inflate(R.layout.listitem_substown, null);
	}
	
	public View makeItemView() {	
		listItem.setClickable(true);
		listItem.setFocusable(true);
		imagev = (ImageView)listItem.findViewById(R.id.listitem_substown_image);
		TextView name = (TextView)listItem.findViewById(R.id.listitem_substown_name);
		TextView location = (TextView)listItem.findViewById(R.id.listitem_substown_location);
		
		//设置数据
		BianImageLoader.getInstance().loadImage(imagev, data.getCover(),90);
		name.setText(data.getName());
		location.setText(data.getAddress());
		
		//设置点击事件
		listItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Community.startAction(activity, data.getId());
			}			
		});
		
		return listItem;
	}
}