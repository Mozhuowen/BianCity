package com.putaotown;

import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.BitmapUtil;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemPutao
{
	private PackagePutao putao;
	private Activity activity;
	public ImageView imagev;
	boolean isEditModel;
	
	private static LayoutInflater mLayoutInflater;
	View listItem;
	
	public ListItemPutao(Context context,Activity activity,PackagePutao putao) {
		this.putao = putao;
		mLayoutInflater = LayoutInflater.from(activity);
		listItem = mLayoutInflater.inflate(R.layout.listitem_putao, null);
		this.activity = activity;
	}
	
	public static View getNewView(Activity ac) {
		mLayoutInflater = LayoutInflater.from(PutaoApplication.getContext());
		View listItem = mLayoutInflater.inflate(R.layout.listitem_putao, null);
			
		return listItem;
	}
	
	public View makeItemView() {	
		listItem.setClickable(true);
		listItem.setFocusable(true);
		imagev = (ImageView)listItem.findViewById(R.id.listitem_putao_image);
		TextView titlev = (TextView)listItem.findViewById(R.id.listitem_putao_title);
		TextView goodv = (TextView)listItem.findViewById(R.id.listitem_putao_good);
		TextView datev = (TextView)listItem.findViewById(R.id.listitem_putao_date);
		
		//设置数据
//		SLoadImage.getInstance().loadImage(imagev, putao.getCover(), 90, 90,SLoadImage.SMALL);
		BianImageLoader.getInstance().loadImage(imagev, putao.getCover(),90);
		titlev.setText(putao.getTitle());
		datev.setText(putao.getCreatetime());
		goodv.setText(""+putao.getGoods());
		
		//设置点击事件
		listItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (activity instanceof PutaoxActivity)
					if (((PutaoxActivity)activity).mIsEditModel)
						PutaoxActivity.startAction(activity, putao,true);
					else
						PutaoxActivity.startAction(activity, putao,false);
				else
					PutaoxActivity.startAction(activity, putao,false);
			}			
		});
		
		return listItem;
	}
	
	public int getItemHeight(){
		 return listItem.getHeight();
	}
}