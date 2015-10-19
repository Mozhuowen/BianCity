package com.putaotown;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.putaotown.MsgAdapter.ViewHolder;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelPushComment;
import com.putaotown.net.objects.ModelPushGood;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.MsgShowGoodActivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/***
 * 消息显示赞提醒的adapter
 * @author awen
 *
 */
public class MsgShowGoodAdapter extends BaseAdapter
{
	private List<ModelPushGood> goods;
	private Activity context;
	private LayoutInflater layoutInflater;
	
	public MsgShowGoodAdapter(Activity context,List<ModelPushGood> goods) {
		this.goods = goods;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goods.size();
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
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if ( convertView == null ) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.listitem_msg_good, null);
			holder.usercover = (ImageView)convertView.findViewById(R.id.listitem_msg_good_usercover);
			holder.targetimage = (ImageView)convertView.findViewById(R.id.listitem_msg_good_storyimage);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_msg_good_time);
			holder.username = (TextView)convertView.findViewById(R.id.listitem_msg_good_username);
			holder.typestr = (TextView)convertView.findViewById(R.id.listitem_msg_good_msgtypestr);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		//set data
		ModelPushGood goodmess = this.goods.get(position);
		holder.username.setText(goodmess.getUsername());
		if (goodmess.getGoodtype() == 0)
			holder.typestr.setText(context.getString(R.string.msg_good_story));
		else if (goodmess.getGoodtype() ==1 )
			holder.typestr.setText(context.getString(R.string.msg_good_town));
		else if (goodmess.getGoodtype() == 2)
			holder.typestr.setText(context.getString(R.string.msg_good_comment));
		//deal time
		Calendar time = Calendar.getInstance();		
		if (goodmess.getTime() == 0)
			time = Calendar.getInstance();
		else
			time.setTimeInMillis(goodmess.getTime());
		SimpleDateFormat format = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestr = format.format(time.getTime());
		holder.time.setText(timestr);
		//bind accident		
		holder.usercover.setOnClickListener(new MsgShowActivity.MsgOpenUserListener(context, new ModelUser(goodmess.getUserid(),goodmess.getUsername(),goodmess.getUsercover())));
		if (goodmess.getGoodtype() == 0 || goodmess.getGoodtype() == 2)
			holder.targetimage.setOnClickListener(new MsgShowGoodActivity.MsgOpenTargetListener(context,goodmess.getGoodtype(), goodmess.getStory()));
		else if (goodmess.getGoodtype() == 1)
			holder.targetimage.setOnClickListener(new MsgShowGoodActivity.MsgOpenTargetListener(context,goodmess.getGoodtype(), goodmess.getTown()));
		
		//loadimage
		holder.ir1 = new ImgRecy(goodmess.getUsercover(),90,true);
		if (goodmess.getGoodtype() == 0)
			holder.ir2 = new ImgRecy(goodmess.getStory().getCover(),90);
		else if (goodmess.getGoodtype() == 1 )
			holder.ir2 = new ImgRecy(goodmess.getTown().getCover(),90);
		else if (goodmess.getGoodtype() ==2 )
			holder.ir2 = new ImgRecy(goodmess.getStory().getCover(),90);
		BianImageLoader.getInstance().loadImage(holder.usercover,goodmess.getUsercover(),90);
		BianImageLoader.getInstance().loadImage(holder.targetimage,holder.ir2.imgname,90);
//		SLoadImage.getInstance().loadImage(holder.usercover
//    			, holder.ir1.imgname
//    			, holder.ir1.size
//    			, holder.ir1.size
//    			, holder.ir1.irank);
//		SLoadImage.getInstance().loadImage(holder.targetimage
//    			, holder.ir2.imgname
//    			, holder.ir2.size
//    			, holder.ir2.size
//    			, holder.ir2.irank);
		
		return convertView;
	}
	
	public static class ViewHolder
	{
		public ImageView usercover;
		public ImageView targetimage;
		public TextView username;
		public TextView time;
		public TextView typestr;
		public int goodtype;
		public ImgRecy ir1;
		public ImgRecy ir2;
	}
	
}