package com.putaotown.community.adapter;

import java.util.ArrayList;
import java.util.List;

import com.putaotown.OpenImageBrower;
import com.putaotown.R;
import com.putaotown.UserActivity;
import com.putaotown.community.ReplyTieActivity;
import com.putaotown.community.ThemeTieActivity;
import com.putaotown.community.models.ModelTie;
import com.putaotown.community.models.ModelTieReply;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.imageviewer.ImagePagerActivity;
import com.putaotown.markdown.MDReader;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.LogUtil;
import com.putaotown.tools.OpenUserSpan;
import com.putaotown.tools.TimeShowUtil;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class TieThemeAdapter extends BaseAdapter
{
	private static boolean floor_flag = false;
	private static final int ITEMTYPE_ITEM = 1;
	private static final int TYPE_MAX_COUNT = 2;
	private static final int ITEMTYPE_HEADER = 0;
	private ModelTieTheme DataHeader;
	private List<ModelTie> DataTieList = new ArrayList<ModelTie>();
	
	private LayoutInflater layoutInflater;
	private static ThemeTieActivity context;
	
	public TieThemeAdapter(ThemeTieActivity ac,ModelTieTheme header) {
		DataHeader = header;
		this.context = ac;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public void readToUpdate() {
		this.DataTieList.removeAll(DataTieList);
	}
	
	public void loadTies(List<ModelTie> list) {
		this.DataTieList.addAll(list);
		this.notifyDataSetChanged();		
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
		return DataTieList.size()+1;
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
			LogUtil.v("TieThemeAdapter info: ", "get header view!");
			convertView = this.getHeaderView(convertView);
			break;
		case ITEMTYPE_ITEM:
			LogUtil.v("TieThemeAdapter info: ", "get item view!");
			convertView = this.getItemView(position,convertView,holder);
			break;
		}
		return convertView;
	}
	
	private View getHeaderView(View convertView) {
		if (convertView == null){
			convertView = this.layoutInflater.inflate(R.layout.listitem_tietheme_header, null);
			ImageView usercover = (ImageView) convertView.findViewById(R.id.listitem_tietheme_usercover);
			TextView username = (TextView)convertView.findViewById(R.id.listitem_tietheme_username);
			TextView time = (TextView)convertView.findViewById(R.id.listitem_tietheme_time);
			TextView title = (TextView)convertView.findViewById(R.id.listitem_tietheme_title);
			TextView content = (TextView)convertView.findViewById(R.id.listitem_tietheme_content);
			LinearLayout imagelayout = (LinearLayout)convertView.findViewById(R.id.listitem_tietheme_imagelayout);
			//set data
			this.loadImages(imagelayout,this.DataHeader.getImagenames());
			username.setText(this.DataHeader.getUsername());
			time.setText(TimeShowUtil.showGoodTime(this.DataHeader.getTime()));
			title.setText(this.DataHeader.getTitle());
//			content.setText(this.DataHeader.getContent());
			content.setTextKeepState(new MDReader(this.DataHeader.getContent()).getFormattedContent(),BufferType.SPANNABLE);
			this.loadImage(usercover, this.DataHeader.getUsercover(),true);
			//open user
			final ModelUser user = new ModelUser();
			user.setUserid(this.DataHeader.getUserid());
			user.setCover(this.DataHeader.getUsercover());
			user.setName(this.DataHeader.getUsername());
			usercover.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					UserActivity.startAction(context, user);
				}				
			});			
		}
		
		return convertView;
	}
	
	private View  getItemView(final int position,View convertView,ViewHolder holder) {
		int listposition = position-1;
		final ModelTie thistie = this.DataTieList.get(listposition);
		//判断是否使用旧的楼层显示方法
		if (listposition == 0 && thistie.getFloot() == 0)
			floor_flag = true;
		else if (listposition == 0 && thistie.getFloot() > 0)
			floor_flag = false;
			
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem_tietheme_tie, null);			
			holder.usercover = (ImageView)convertView.findViewById(R.id.listitem_tietheme_tie_usercover);
			holder.username = (TextView)convertView.findViewById(R.id.listitem_tietheme_tie_username);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_tietheme_tie_time);
			holder.btncomment = convertView.findViewById(R.id.listitem_tietheme_tie_reply);
			holder.content = (TextView)convertView.findViewById(R.id.listitem_tietheme_tie_content);
			holder.imagelayout = (LinearLayout)convertView.findViewById(R.id.listitem_tietheme_tie_imagelayout);
			holder.lou = (TextView)convertView.findViewById(R.id.listitem_tietheme_tie_lou);
			holder.replylayout = (LinearLayout)convertView.findViewById(R.id.listitem_tietheme_tie_replys);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();
		//设置楼层数
		if (floor_flag)
			holder.lou.setText(context.getString(R.string.bbs_lou, position+1+""));
		else
			holder.lou.setText(context.getString(R.string.bbs_lou, thistie.getFloot()+""));
		holder.imagelayout.removeAllViews();
		this.loadImages(holder.imagelayout, thistie.getImagenames());
		holder.username.setText(thistie.getUsername());
		holder.userid = thistie.getUserid();
		holder.content.setText(thistie.getContent());
		holder.time.setText(TimeShowUtil.showGoodTime(thistie.getTime()));
		holder.btncomment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ReplyTieActivity.startAction(context, position+1,thistie,true);
			}			
		});
		
		this.setReplys(holder.replylayout, thistie.getReplys());
		this.loadImage(holder.usercover, thistie.getUsercover(), true);
		final ModelUser user = new ModelUser();
		user.setUserid(thistie.getUserid());
		user.setCover(thistie.getUsercover());
		user.setName(thistie.getUsername());
		holder.usercover.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				UserActivity.startAction(context, user);
			}				
		});
		
		return convertView;
	}
	
	private void loadImages(LinearLayout imagelayout,List<String> imagenames) {
		imagelayout.setVisibility(View.VISIBLE);
		for (int i=0;i<imagenames.size();i++) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 20, 0, 0);
			ImageView view = new ImageView(context);
			view.setLayoutParams(lp);
//			view.setScaleType(ImageView.ScaleType.FIT_CENTER);
			view.setAdjustViewBounds(true);
//			Uri imageuri = Uri.fromFile(new File(this.context.getFilesDir() + "/image/" + imagenames.get))
			view.setOnClickListener(new OpenImageBrower(context,i,imagenames));
			imagelayout.addView(view);
			this.loadImage(view, imagenames.get(i),false);
		}
	}
	
	private void setReplys(LinearLayout layout,List<ModelTieReply> replys) {
		layout.removeAllViews();
		if (replys.size() > 0){			
			View divider = new View(context);
			LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
			lpd.setMargins(0, 11, 0, 0);
			divider.setLayoutParams(lpd);
			divider.setBackgroundColor(context.getResources().getColor(R.color.Gainsboro));
			layout.addView(divider);
		}
		for(int i=0;i<replys.size();i++) {
			TextView spanview = new TextView(context);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 21, 0, 0);
			spanview.setLayoutParams(lp);
			spanview.setTextSize(11);
			spanview.setTextColor(context.getResources().getColor(R.color.grey_light));
			SpannableString spStr = new SpannableString(replys.get(i).getUsername());
			OpenUserSpan span = new OpenUserSpan(context,replys.get(i).getUserid(),replys.get(i).getUsername(),replys.get(i).getUsercover());
			spStr.setSpan(span, 0, spStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanview.append(spStr);
			spanview.append(": "+replys.get(i).getContent()+"  "+TimeShowUtil.showGoodTime(replys.get(i).getTime()));
			spanview.setMovementMethod(LinkMovementMethod.getInstance());
			layout.addView(spanview);
		}
	}
	
	private void loadImage(ImageView imageview,String uri,boolean isusercover) {
		if (isusercover)
			BianImageLoader.getInstance().loadImage(imageview, uri,250);
		else
			BianImageLoader.getInstance().loadImage(imageview, uri,850);
	}
	
	public static class ViewHolder
	{
		public int tieid;
		public int userid;
		public View mainlayout;
		public TextView time;
		public TextView username;
		public TextView lou;
		public ImageView usercover;
		public View btncomment;
		public TextView content;
		public LinearLayout imagelayout;
		public LinearLayout replylayout;
	}
}