package com.putaotown;

import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelMessboard;
import com.putaotown.net.objects.ModelUser;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemMessboard
{
	private ModelMessboard mess;
	private MessboardActivity context;
	
	LayoutInflater layoutInflater;
	View listItem;
	ImageView imagev;
	
	public ListItemMessboard(MessboardActivity context,ModelMessboard mess) {
		this.context = context;
		this.mess = mess;
		layoutInflater = LayoutInflater.from(context);
		listItem = layoutInflater.inflate(R.layout.listitem_messboard, null);
	}
	
	public View makeItemView() {
		imagev = (ImageView)listItem.findViewById(R.id.listitem_messs_image);
		TextView username = (TextView)listItem.findViewById(R.id.listitem_mess_username);
		TextView datev = (TextView)listItem.findViewById(R.id.listitem_mess_date);
		TextView content = (TextView)listItem.findViewById(R.id.listitem_mess_content);
		View cicon = listItem.findViewById(R.id.listitem_mess_cicon);
		TextView good = (TextView)listItem.findViewById(R.id.listitem_mess_good);
		
		datev.setText(mess.getTime());
		username.setText(mess.getUsername());
		String contentstr = mess.getContent();
		contentstr = contentstr.replaceAll("(@.*?):", "<font color='#1E90FF'>$1</font>: ");
		content.setText(Html.fromHtml(contentstr));
		good.setText(""+mess.getGoods());
//		SLoadImage.getInstance().loadImage(imagev, mess.getCover(), 100, 100,true);
		loadUserCover(imagev,mess.getCover());
		//bind reply accident
		cicon.setOnClickListener(context.new OnReply(mess.getUsername(),mess.getContent()));
		//设置拇指图标
		Drawable thumb = context.getResources().getDrawable(R.drawable.ic_list_thumb);
		Drawable thumbup = context.getResources().getDrawable(R.drawable.ic_list_thumbup);
		thumb.setBounds(0, 0, thumb.getMinimumWidth(), thumb.getMinimumHeight());
		thumbup.setBounds(0, 0, thumbup.getMinimumWidth(), thumbup.getMinimumHeight());
		good.setOnClickListener(context.new GoodListener(good,mess.getMessid(),!mess.getDogood()));
		if (mess.getDogood())
			good.setCompoundDrawables(null, null, thumbup, null);
		else
			good.setCompoundDrawables(null, null, thumb, null);
		//bind openuser accident
		ModelUser user = new ModelUser();
		user.setName(mess.getUsername());
		user.setCover(mess.getCover());
		user.setUserid(mess.getUserid());
		imagev.setOnClickListener(context.new OnOpenUser(user));

		return listItem;
	}
	/**加载用户头像*/
	private void loadUserCover(ImageView mCover,String cover) {
		BianImageLoader.getInstance().loadImage(mCover, cover,90);
//		if (cover.contains("http")) {
//			SLoadImage.getInstance().loadImage(mCover, cover, 100, 100,true);			
//		} else {
//			ImgRecy ir = new ImgRecy(cover,100);
//			SLoadImage.getInstance().loadImage(mCover, cover, ir.size,ir.size,ir.irank);
//		}
			
	}
	
}