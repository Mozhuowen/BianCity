package com.putaotown;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackageComment;

public class ListItemComment
{
	private PackageComment comment;
	private PutaoxActivity context;
	
	private static LayoutInflater layoutInflater;
	View listItem;
	public ImageView imagev;
	
	public ListItemComment(PutaoxActivity context,PackageComment comment) {
		this.context = context;
		this.comment = comment;
		layoutInflater = LayoutInflater.from(context);
		listItem = layoutInflater.inflate(R.layout.listitem_comment, null);
	}
	
	public static View getNewView(Activity ac) {
		layoutInflater = LayoutInflater.from(ac);
		View listItem = layoutInflater.inflate(R.layout.listitem_comment, null);		
		
		return listItem;
	}
	
	public View makeItemView() {
		
		imagev = (ImageView)listItem.findViewById(R.id.listitem_comment_image);
		TextView username = (TextView)listItem.findViewById(R.id.listitem_comment_username);
		TextView datev = (TextView)listItem.findViewById(R.id.listitem_comment_date);
		TextView content = (TextView)listItem.findViewById(R.id.listitem_comment_content);
		View cicon = listItem.findViewById(R.id.listitem_comment_cicon);
		TextView good = (TextView)listItem.findViewById(R.id.listitem_comment_good);
		ImageView goodthumb = (ImageView) listItem.findViewById(R.id.listitem_comment_good_image);
		
		datev.setText(comment.getTime());
//		username.setText(comment.getUsername());
		username.setText(context.getString(R.string.comment_reply,comment.getUsername(),comment.getReplyname()));
		String contentstr = comment.getContent();
		contentstr = contentstr.replaceAll("(@.*?):", "<font color='#1E90FF'>$1</font>: ");
		content.setText(Html.fromHtml(contentstr));
		good.setText(""+comment.getGoods());
//		SLoadImage.getInstance().loadImage(imagev, comment.getCover(), 100, 100,true);
		BianImageLoader.getInstance().loadImage(imagev, comment.getCover(),90);
		
		cicon.setOnClickListener(context.new OnReply(comment.getUsername(),comment.getContent(),comment.getCommentid()));
		//set good view image
		//设置拇指图标
//		Drawable thumb = context.getResources().getDrawable(R.drawable.ic_list_thumb);
//		Drawable thumbup = context.getResources().getDrawable(R.drawable.ic_list_thumbup);
//		thumb.setBounds(0, 0, thumb.getMinimumWidth(), thumb.getMinimumHeight());
//		thumbup.setBounds(0, 0, thumbup.getMinimumWidth(), thumbup.getMinimumHeight());
		if (comment.isDogood()) {
//			good.setCompoundDrawables(null, null, thumbup, null);
			goodthumb.setImageResource(R.drawable.ic_list_thumbup);
		}else {
//			good.setCompoundDrawables(null, null, thumb, null);
			goodthumb.setImageResource(R.drawable.ic_list_thumb);
		}
		//bind do good accident
        goodthumb.setOnClickListener(context.new GoodListener(good,goodthumb,comment.getCommentid(),!comment.isDogood()));
		//bind openuser accident
		ModelUser user = new ModelUser();
		user.setUserid(comment.getUserid());
		user.setName(comment.getUsername());
		user.setCover(comment.getCover());
		imagev.setOnClickListener(context.new OnOpenUser(user));
		
		//绑定comment对象
		listItem.setTag(comment);
		return listItem;
	}
	
	
}