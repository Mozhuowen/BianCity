package com.putaotown;

import com.putaotown.fragment.HotFragment;
import com.putaotown.fragment.NearFragment;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemFactory
{
	private float per;
	private int pic1;
	private int pic2 = 0;
	private Fragment hot = null;
	//判断fragment是hot还是near的
	private int fragmentType = 0;
	LayoutInflater layoutInflater;
	private Context context = PutaoApplication.getContext();
	
	public ListItemFactory(Fragment hot,Context con,float p,int pic1,int pic2){
		this.per = p;
		this.pic1 = pic1;
		this.pic2 = pic2;
		this.context = con;
		this.hot = hot;
		this.fragmentType = (this.hot instanceof HotFragment?0:1);
		layoutInflater = LayoutInflater.from(context);
	}
	
	public View getInstance(){
		/*LogUtil.v("Display info: ", "per: "+per);
		AbsListView.LayoutParams layoutparam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)per*17);
		//第一层1
		LinearLayout linearlayout = new LinearLayout(context);
		linearlayout.setLayoutParams(layoutparam);
		linearlayout.setOrientation(LinearLayout.HORIZONTAL);
//		linearlayout.setPadding(0, 0, 0, per*2);
		//层1.1
		LinearLayout imagelayout1 = new LinearLayout(context);
		imagelayout1.setOrientation(LinearLayout.VERTICAL);
		imagelayout1.setPadding((int)(per+1), 0, 0, 0);

		//层1.2
		LinearLayout imagelayout2 = new LinearLayout(context);
		imagelayout2.setOrientation(LinearLayout.VERTICAL);
		imagelayout2.setPadding((int)(per+1), 0, 0, 0);
		//层1.1.1
		ImageView image1 = new ImageView(context);
		image1.setImageResource(pic1);
		image1.setLayoutParams(new LinearLayout.LayoutParams((int)(per*13),(int)(per*13)LinearLayout.LayoutParams.MATCH_PARENT));
		image1.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//绑定单击事件
		if(fragmentType==0)
			image1.setOnClickListener(((HotFragment)hot).new ListItemOnClickListener(context));
		else
			image1.setOnClickListener(((NearFragment)hot).new ListItemOnClickListener(context));
		//层1.1.2
		TextView text1 = new TextView(context);
		text1.setText("葡萄小镇\nE113°17'18''N23°13'19''");	
		text1.setTextSize(10);
		
		imagelayout1.addView(image1);
		imagelayout1.addView(text1);
		if(pic2!=0){
			//层1.2.1
			ImageView image2 = new ImageView(context);
			image2.setImageResource(pic2);
			image2.setLayoutParams(new LinearLayout.LayoutParams((int)(per*13),(int)(per*13)LinearLayout.LayoutParams.MATCH_PARENT));
			image2.setScaleType(ImageView.ScaleType.CENTER_CROP);
			//绑定单击事件
			if(fragmentType==0)
				image2.setOnClickListener(((HotFragment)hot).new ListItemOnClickListener(context));
			else
				image2.setOnClickListener(((NearFragment)hot).new ListItemOnClickListener(context));
			TextView text2 = new TextView(context);
			text2.setText("葡萄小镇\nE113°17'18''N23°13'19''");	
			text2.setTextSize(10);
			imagelayout2.addView(image2);
			imagelayout2.addView(text2);
		}
		linearlayout.addView(imagelayout1);
		linearlayout.addView(imagelayout2);*/
		
		AbsListView.LayoutParams layoutparam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(per*18));
		//第一层1
		LinearLayout linearlayout = new LinearLayout(context);
		linearlayout.setLayoutParams(layoutparam);
		linearlayout.setOrientation(LinearLayout.HORIZONTAL);
		linearlayout.setPadding(0, 0, 0, (int)per);
		//1.1
		View view1 = layoutInflater.inflate(R.layout.view_mine_town, null);
		LinearLayout mainlayout1 = (LinearLayout)view1.findViewById(R.id.view_mine_town_main);
		ImageView imageview1 = (ImageView)view1.findViewById(R.id.view_mine_town_image);
		//设置为13份
		LinearLayout.LayoutParams mainlayoutparam1 = new LinearLayout.LayoutParams((int)(per*13),(int)(per*17));
		mainlayoutparam1.setMargins((int)per, 0, 0, 0);
		mainlayout1.setLayoutParams(mainlayoutparam1);
		//设置图片宽度
		int imagewidth1 =(int) (per*13-(DensityUtil.dip2px(context, 2)*2));
		imageview1.setLayoutParams(new LinearLayout.LayoutParams(imagewidth1, imagewidth1));
		//1.2
		View view2 = layoutInflater.inflate(R.layout.view_mine_town, null);
		LinearLayout mainlayout2 = (LinearLayout)view2.findViewById(R.id.view_mine_town_main);
		ImageView imageview2 = (ImageView)view2.findViewById(R.id.view_mine_town_image);
		//设置为13份
		LinearLayout.LayoutParams mainlayoutparam2 = new LinearLayout.LayoutParams((int)(per*13),(int)(per*17));
		mainlayoutparam2.setMargins((int)per, 0, 0, 0);
		mainlayout2.setLayoutParams(mainlayoutparam2);
		//设置图片宽度
		int imagewidth2 =(int) (per*13-(DensityUtil.dip2px(context, 2)*2));
		imageview2.setLayoutParams(new LinearLayout.LayoutParams(imagewidth2, imagewidth2));
		
		linearlayout.addView(view1);
		linearlayout.addView(view2);
		
		return linearlayout;
	}
}