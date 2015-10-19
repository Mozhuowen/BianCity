package com.putaotown.community.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putaotown.OpenImageBrower;
import com.putaotown.R;
import com.putaotown.community.Community;
import com.putaotown.community.models.ModelCommuHeader;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.tools.LogUtil;
import com.putaotown.tools.TimeShowUtil;
import com.putaotown.views.AImageView;
import com.putaotown.views.ALinearLayout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommunityAdapter extends BaseAdapter
{
	private static final int ITEMTYPE_HEADER = 0;	//getview返回的当前view的类别，分头部和头部之外的
	private static final int ITEMTYPE_ITEM = 1;
	private static final int TYPE_MAX_COUNT = 2;
	private LayoutInflater layoutInflater;
	private ModelCommuHeader DataHeader;
	private List<ModelTieTheme> DataTieList;
	private Community context;
	
	public CommunityAdapter(Community context,ModelCommuHeader header,List<ModelTieTheme> ties) {
		this.DataHeader = header;
		this.DataTieList = ties;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	/**更新*/
	public void update(ModelCommuHeader header,List<ModelTieTheme> ties) {
		this.DataHeader = header;
		this.DataTieList.removeAll(DataTieList);
		this.DataTieList.addAll(ties);
		this.notifyDataSetChanged();
	}
	/**增加*/
	public void loadMore(List<ModelTieTheme> ties) {
		if (ties != null ){
			DataTieList.addAll(ties);
			this.notifyDataSetChanged();
		}
	}
	
	@Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
	/**获取当前position的view的类别*/
	@Override
    public int getItemViewType(int position) {
        return position == 0?0:1;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.DataTieList.size()+1;
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
		int itemtype = getItemViewType(position);
		ViewHolder holder = null;
		
		switch(itemtype) {
		case ITEMTYPE_HEADER:
			LogUtil.v("CommunityAdapter info: ", "get header view!");
			convertView = this.getHeaderView(convertView);
			break;
		case ITEMTYPE_ITEM:
			LogUtil.v("CommunityAdapter info: ", "get item view!");
			convertView = this.getItemView(position,convertView,holder);
			break;
		}
		return convertView;
	}
	
	private View getHeaderView(View convertView) {
		if (convertView == null){
			convertView = this.layoutInflater.inflate(R.layout.listitem_community_header, null);
			TextView communityname = (TextView)convertView.findViewById(R.id.listitem_community_header_cname);
			TextView membercount = (TextView)convertView.findViewById(R.id.listitem_community_header_membercount);
			TextView tiecount = (TextView)convertView.findViewById(R.id.listitem_community_header_tiecount);
			TextView btnjoin = (TextView)convertView.findViewById(R.id.listitem_community_header_btnjoin);
			ImageView coverimage = (ImageView)convertView.findViewById(R.id.listitem_community_header_coverimage);
			LinearLayout toplayout = (LinearLayout)convertView.findViewById(R.id.listitem_community_header_toptie);
			
			getTopTieView(toplayout);
			LogUtil.v("CommunityAdapter info: ", "join info: "+this.DataHeader.isHasjoin());
			if (this.DataHeader.isHasjoin()) {
				btnjoin.setVisibility(View.GONE);				
			} else {
				btnjoin.setVisibility(View.VISIBLE);
			}
			btnjoin.setOnClickListener(context);
			//set data
			communityname.setText(this.DataHeader.getCommunityname());
			membercount.setText(context.getString(R.string.bbs_membercount,this.DataHeader.getMemberscount()+""));
			tiecount.setText(context.getString(R.string.bbs_tiecount, this.DataHeader.getTiecount()));
			loadImage(coverimage,this.DataHeader.getCover());
			List<String> imgurl = new ArrayList<String>();
			imgurl.add(this.DataHeader.getCover());
			coverimage.setOnClickListener(new OpenImageBrower(context,0,imgurl));
			
			
		}
		
		return convertView;
	}
	
	private void getTopTieView(LinearLayout layout) {
		for (int i=0;i<this.DataHeader.getTops().size();i++) {
			View topview = this.layoutInflater.inflate(R.layout.view_community_header_toptie, null);
			TextView titleview = (TextView)topview.findViewById(R.id.view_community_header_title);
			titleview.setText(DataHeader.getTops().get(i).getTitle());			
			topview.setOnClickListener(new Community.OnItemSelected(context, DataHeader.getTops().get(i), this.DataHeader.getAdminid()));
			layout.addView(topview);
		}
	}
	
	private View  getItemView(int position,View convertView,ViewHolder holder) {
		int listposition = position-1;
		ModelTieTheme thistie = this.DataTieList.get(listposition);
		LogUtil.v("CommunityAdapter info: ", "Enter getItemView,listtie size: "+DataTieList.size());
		
		if (convertView == null){
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem_community_tieth, null);
			holder.mainlayout = convertView.findViewById(R.id.listitem_community_mainlayout);
			holder.usercover = (ImageView) convertView.findViewById(R.id.listitem_community_usercover);
			holder.username = (TextView)convertView.findViewById(R.id.listitem_community_username);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_community_time);
			holder.goodcount = (TextView)convertView.findViewById(R.id.listitem_community_goodcount);
			holder.commentcount = (TextView)convertView.findViewById(R.id.listitem_community_commentcount);
			holder.title = (TextView)convertView.findViewById(R.id.listitem_community_title);
			holder.content = (TextView)convertView.findViewById(R.id.listitem_community_content);
			holder.images = (LinearLayout)convertView.findViewById(R.id.listitem_community_content_images);		
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder)convertView.getTag();
		//set data
		holder.username.setText(thistie.getUsername());
		holder.time.setText(TimeShowUtil.showGoodTime(thistie.getTime()));
		holder.goodcount.setText(thistie.getGoodcou()+"");
		holder.commentcount.setText(thistie.getCommentcou()+"");
		holder.title.setText(thistie.getTitle());
		holder.content.setText(thistie.getContent());
		
		//load headercover
		this.loadImage(holder.usercover, thistie.getUsercover());
		
		//bind accident
		holder.mainlayout.setOnClickListener(new Community.OnItemSelected(context, thistie, this.DataHeader.getAdminid()));
		
		//images in each tie
		holder.images.removeAllViews();
		if (thistie.getImagenames().size() > 0) {
			for (int i=0;i< (thistie.getImagenames().size()>3?3:thistie.getImagenames().size()) ;i++) {
				AImageView image = new AImageView(context);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
				lp.setMargins(3, 0, 3, 0);
				image.setLayoutParams(lp);
				image.setScaleType(ImageView.ScaleType.CENTER_CROP);
				this.loadImage(image, thistie.getImagenames().get(i));
				holder.images.addView(image);
			}			
		}
		
		return convertView;
	}
	
	private void loadImage(ImageView imageview,String uri) {
		BianImageLoader.getInstance().loadImage(imageview, uri,250);
	}

	/**主题帖的viewholder*/
	public static class ViewHolder
	{
		public int tieid;
		public int userid;
		public View mainlayout;
		public TextView time;
		public TextView username;
		public ImageView usercover;
		public TextView goodcount;
		public TextView commentcount;
		public TextView title;
		public TextView content;
		public LinearLayout images;
	}
}