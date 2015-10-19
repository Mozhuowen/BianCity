package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import com.putaotown.community.Community;
import com.putaotown.community.adapter.CommunityAdapter.ViewHolder;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.LogUtil;
import com.putaotown.tools.TimeShowUtil;
import com.putaotown.views.AImageView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchTownAdapter extends BaseAdapter
{
	private List<ApplyTown> towns;
	private Activity context;
	private LayoutInflater layoutInflater;
	private boolean isEdit = false;
	
	public SearchTownAdapter(Activity context,List<ApplyTown> towns) {
		this.isEdit = false;
		this.towns = towns;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	public SearchTownAdapter(Activity context,List<ApplyTown> towns,boolean isedit) {
		this.towns = towns;
		this.isEdit = isedit;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	public void changeDataSet(List<ApplyTown> towns) {
		this.towns = towns;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return towns.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final ApplyTown data = towns.get(position);
		
		if (convertView == null){
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem_substown, null);
			holder.cover = (ImageView) convertView.findViewById(R.id.listitem_substown_image);
			holder.name = (TextView) convertView.findViewById(R.id.listitem_substown_name);
			holder.address = (TextView) convertView.findViewById(R.id.listitem_substown_location);					
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder)convertView.getTag();
		//set data
		holder.name.setText(data.getTownname());
		holder.address.setText(data.getGeoinfo().getProvince()+data.getGeoinfo().getFreeaddr());
		
		//load image
		BianImageLoader.getInstance().loadImage(holder.cover, data.getCover(), 100);
		
		//bind accident
		final ModelUser user = new ModelUser();
		user.setUserid(data.getUserid());
		user.setName(data.getUsername());
		user.setCover(data.getUsercover());
		convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (isEdit)
					TownActivity.startAction(context, data, user, true);
				else
					TownActivity.startAction(context, data, user);
			}			
		});	
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		public ImageView cover;
		public TextView name;
		public TextView address;
	}
	
}