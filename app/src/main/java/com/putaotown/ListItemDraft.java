package com.putaotown;

import com.putaotown.localio.FileIO;
import com.putaotown.net.SLoadImage;
import com.putaotown.tools.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemDraft
{
	DraftActivity context;
	ContentValues content;
	LayoutInflater layoutInflater;
	View listItem;
	ImageView imagev;
	ImageView DelView;
	
	public ListItemDraft(DraftActivity context,ContentValues content) {
		this.context = context;
		this.content = content;
		this.layoutInflater = LayoutInflater.from(context);
		listItem = layoutInflater.inflate(R.layout.listitem_draft, null);
	}
	
	public View getView() {
		DelView = (ImageView)listItem.findViewById(R.id.listitem_draft_delete);
		imagev = (ImageView)listItem.findViewById(R.id.listitem_draft_image);
		TextView title = (TextView)listItem.findViewById(R.id.listitem_draft_title);
		TextView desci = (TextView)listItem.findViewById(R.id.listitem_draft_desci);
		
		//设置数据
		if (content.getAsString("title").length() > 0)
			title.setText(content.getAsString("title"));
		else
			title.setText("未命名");
		if (content.getAsInteger("type") == 0)
			desci.setText("边城");
		else
			desci.setText("故事");
		//load image from disk
		if (content.getAsString("cover") != null) {
			String imagepath = context.getFilesDir() + "/image/" + content.getAsString("cover");
			LogUtil.v("ListItemDraft info: ", "image path: "+imagepath);
			Bitmap bitmap = FileIO.getBitmapFromPath(context, imagepath, 90);
			if (bitmap != null)
				this.imagev.setImageBitmap(bitmap);
			else
				LogUtil.v("ListItem info: ", "bitmap is null!");
		} else
			this.imagev.setImageResource(R.color.basecolor);
		
		//设置附带的数据
		DelView.setTag(content.getAsString("randomcode"));

		//点击监听
		listItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (content.getAsInteger("type") == 0)
					CreateTownActivity.startAction(context, content);
				else
					CreatePutaoActivity.startAction(context, content);
			}
			
		});		
		
		DelView.setOnClickListener(context);
		
		return listItem;
	}
}