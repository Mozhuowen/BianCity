package com.putaotown;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelPushComment;
import com.putaotown.net.objects.ModelUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
/**
 * 显示消息推送列表状态的Adapter
 * @author awen
 *
 */
public class MsgAdapter extends BaseAdapter implements OnScrollListener
{
	private List<ModelPushComment> comments;
	private Activity context;
	private LayoutInflater layoutInflater;
	
	public MsgAdapter(Activity context,List<ModelPushComment> comments) {
		this.comments = comments;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return comments.size();
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
		ViewHolder holder = null;
		if ( convertView == null ) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem_msg_comment, null);
			holder.content = (TextView)convertView.findViewById(R.id.listitem_msg_comment_content);
			holder.usercover = (ImageView)convertView.findViewById(R.id.listitem_msg_comment_usercover);
			holder.storyimage = (ImageView)convertView.findViewById(R.id.listitem_msg_comment_storyimage);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_msg_comment_time);
			holder.username = (TextView)convertView.findViewById(R.id.listitem_msg_comment_username);
			holder.replytype = (TextView)convertView.findViewById(R.id.listitem_msg_comment_msgtypestr);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		//set data
		ModelPushComment commentdata = this.comments.get(position);
		holder.username.setText(commentdata.getUsername());
		holder.content.setText(commentdata.getContent());
		if (commentdata.getRetype() == 0)
			holder.replytype.setText(this.context.getString(R.string.msg_comment_normal));
		else
			holder.replytype.setText(this.context.getString(R.string.msg_comment_reply));
		//deal time
		Calendar time = Calendar.getInstance();		
		if (commentdata.getTime() == 0)
			time = Calendar.getInstance();
		else
			time.setTimeInMillis(commentdata.getTime());
		SimpleDateFormat format = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestr = format.format(time.getTime());
		holder.time.setText(timestr);
		//bind accident
		holder.content.setOnClickListener(new MsgShowActivity.MsgShowListener(commentdata.getCommentid()
				,commentdata.getUsername()
				,commentdata.getStory().getPutaoid()
				,commentdata.getStory().getTownid()));
		holder.storyimage.setOnClickListener(new MsgShowActivity.MsgOpenStoryListener(context, commentdata.getStory()));
		holder.usercover.setOnClickListener(new MsgShowActivity.MsgOpenUserListener(context, new ModelUser(commentdata.getUserid(),commentdata.getUsername(),commentdata.getUsercover())));
		//loadimage
		holder.ir1 = new ImgRecy(commentdata.getUsercover(),90,true);
		holder.ir2 = new ImgRecy(commentdata.getStory().getCover(),90);
//		SLoadImage.getInstance().loadImage(holder.usercover
//    			, holder.ir1.imgname
//    			, holder.ir1.size
//    			, holder.ir1.size
//    			, holder.ir1.irank);
		BianImageLoader.getInstance().loadImage(holder.usercover, commentdata.getUsercover(),90);
		BianImageLoader.getInstance().loadImage(holder.storyimage, commentdata.getStory().getCover(),90);
//		SLoadImage.getInstance().loadImage(holder.storyimage
//    			, holder.ir2.imgname
//    			, holder.ir2.size
//    			, holder.ir2.size
//    			, holder.ir2.irank);
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		public ImageView usercover;
		public ImageView storyimage;
		public TextView username;
		public TextView content;
		public TextView time;
		public TextView replytype;
		public ImgRecy ir1;
		public ImgRecy ir2;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub		
		MsgShowActivity.onScroll();
	}
	
}