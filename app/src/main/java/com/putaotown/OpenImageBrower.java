package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.putaotown.imageviewer.ImagePagerActivity;
import com.putaotown.tools.LogUtil;

/**打开照片查看器*/
public class OpenImageBrower implements OnClickListener
{
	private int position;
	private List<String> urls;
	private Context context;
	public OpenImageBrower(Context context,int p,List<String> u) {
		this.position = p;
		this.urls = u;
		this.context = context;
	}
	@Override
	public void onClick(View v) {
		LogUtil.v("PutaoxActivity info: ", "Image was clicked!");
		Intent intent = new Intent(context, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList)urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);
	}
	
}