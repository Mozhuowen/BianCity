package com.putaotown;

import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelUser;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemSubstown
{
	private ApplyTown town;
	private Activity activity;
	public ImageView imagev;
	LayoutInflater layoutInflater;
	View listItem;
	
	public ListItemSubstown(Activity ac,ApplyTown t) {
		this.town = t;
		this.activity = ac;
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
//		SLoadImage.getInstance().loadImage(imagev, town.getCover(), 90, 90,SLoadImage.SMALL);
		BianImageLoader.getInstance().loadImage(imagev, town.getCover(),90);
		name.setText(town.getTownname());
		location.setText(town.getGeoinfo().getProvince()+town.getGeoinfo().getFreeaddr());
		
		//设置点击事件
		listItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ModelUser u = new ModelUser();
				u.setUserid(town.getUserid());
				u.setName(town.getUsername());
				u.setCover(town.getUsercover());
				TownActivity.startAction(activity, town, u,false);
			}			
		});
		
		return listItem;
	}
}