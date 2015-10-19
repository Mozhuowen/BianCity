package com.putaotown;

import java.util.List;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemTown 
{
	/**将屏幕分成29份中的一份宽度*/
	private static float mPer;
	private static DisplayMetrics  dm = new DisplayMetrics();
	private Activity mContext;
	private static LayoutInflater mLayoutInflater;
	private ApplyTown mT1;
	private ApplyTown mT2;
	private ModelUser mOwner1;
	private ModelUser mOwner2;
	/**以备资源回收的imageview*/
	public ImageView im1;
	public ImageView im2;
	public ImgRecy imrecy1;
	public ImgRecy imrecy2;
	
	public ListItemTown(Activity ac,ApplyTown t1,ApplyTown t2){
		this.mContext = ac;
		mLayoutInflater = LayoutInflater.from(mContext);
		this.mT1 = t1;
		this.mT2 = t2;
		mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);    
		float widthPixels= dm.widthPixels;		
		this.mPer = widthPixels/29;
	}
	
	public ListItemTown(Activity ac,ApplyTown t1,ApplyTown t2,ModelUser Owner1,ModelUser Owner2){
		this.mContext = ac;
		mLayoutInflater = LayoutInflater.from(mContext);
		this.mT1 = t1;
		this.mT2 = t2;
		this.mOwner1 = Owner1;
		this.mOwner2 = Owner2;
		mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);    
		float widthPixels= dm.widthPixels;		
		this.mPer = widthPixels/29;
	}
	
	public static  View getNewView(Activity ac) {
		mLayoutInflater = LayoutInflater.from(PutaoApplication.getContext());
		ac.getWindowManager().getDefaultDisplay().getMetrics(dm); 
		float widthPixels= dm.widthPixels;		
		mPer = widthPixels/29;
		
		//1.1
		View view1 = mLayoutInflater.inflate(R.layout.view_mine_town, null);
		LinearLayout mainlayout1 = (LinearLayout)view1.findViewById(R.id.view_mine_town_main);
		ImageView imageview1 = (ImageView)view1.findViewById(R.id.view_mine_town_image);
		//设置宽为13份高16份
		LinearLayout.LayoutParams mainlayoutparam1 = new LinearLayout.LayoutParams((int)(mPer*13),(int)(mPer*16));
		mainlayoutparam1.setMargins((int)mPer, 0, 0, (int)mPer);
		mainlayout1.setLayoutParams(mainlayoutparam1);
		//设置图片宽度
		int imagewidth1 =(int) (mPer*13-(DensityUtil.dip2px(ac, 2)*2));
		imageview1.setLayoutParams(new LinearLayout.LayoutParams(imagewidth1, imagewidth1));
		return view1;
	}
	
	public View getView() {
		LogUtil.v("ListItemTown info: ", " pixel per: "+mPer);
		AbsListView.LayoutParams layoutparam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(mPer*18));
		//第一层1
		LinearLayout linearlayout = new LinearLayout(mContext);
		linearlayout.setLayoutParams(layoutparam);
		linearlayout.setOrientation(LinearLayout.HORIZONTAL);
		linearlayout.setPadding(0, 0, 0, (int)mPer);
		//1.1
		View view1 = mLayoutInflater.inflate(R.layout.view_mine_town, null);
		LinearLayout mainlayout1 = (LinearLayout)view1.findViewById(R.id.view_mine_town_main);
		ImageView imageview1 = (ImageView)view1.findViewById(R.id.view_mine_town_image);
		im1 = imageview1;
		TextView goodview1 = (TextView)view1.findViewById(R.id.view_mine_town_goods);
		//设置为13份
		LinearLayout.LayoutParams mainlayoutparam1 = new LinearLayout.LayoutParams((int)(mPer*13),(int)(mPer*17));
		mainlayoutparam1.setMargins((int)mPer, 0, 0, 0);
		mainlayout1.setLayoutParams(mainlayoutparam1);
		//设置图片宽度
		int imagewidth1 =(int) (mPer*13-(DensityUtil.dip2px(mContext, 2)*2));
		imageview1.setLayoutParams(new LinearLayout.LayoutParams(imagewidth1, imagewidth1));
		//load image in the adapter
//		SLoadImage.getInstance().loadImage(imageview1, this.mT1.getCover(), 150, 150,SLoadImage.SMALL);
		imrecy1 = new ImgRecy(this.mT1.getCover(),150);
		//set data
		TextView townname = (TextView)view1.findViewById(R.id.view_mine_town_towname);
		TextView address = (TextView)view1.findViewById(R.id.view_mine_town_address);
		townname.setText(this.mT1.getTownname());
		address.setText(this.mT1.getGeoinfo().getCity()+this.mT1.getGeoinfo().getFreeaddr());
		goodview1.setText(""+mT1.getGood());
		//bind accident
		view1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (mOwner1 == null) {
					 mOwner1 = new ModelUser();
					 mOwner1.setCover(UserPreUtil.getCover());
					 mOwner1.setName(UserPreUtil.getUsername());
					 mOwner1.setUserid(UserPreUtil.getUserid());
					 mOwner1.setFans(0);
				}
				TownActivity.startAction(mContext, mT1, mOwner1);
			}
			
		});
		linearlayout.addView(view1);
		
		if (mT2 != null) {
			//1.2
			View view2 = mLayoutInflater.inflate(R.layout.view_mine_town, null);
			LinearLayout mainlayout2 = (LinearLayout)view2.findViewById(R.id.view_mine_town_main);
			ImageView imageview2 = (ImageView)view2.findViewById(R.id.view_mine_town_image);
			im2 = imageview2;
			//设置为13份
			LinearLayout.LayoutParams mainlayoutparam2 = new LinearLayout.LayoutParams((int)(mPer*13),(int)(mPer*17));
			mainlayoutparam2.setMargins((int)mPer, 0, 0, 0);
			mainlayout2.setLayoutParams(mainlayoutparam2);
			//设置图片宽度
			int imagewidth2 =(int) (mPer*13-(DensityUtil.dip2px(mContext, 2)*2));
			imageview2.setLayoutParams(new LinearLayout.LayoutParams(imagewidth2, imagewidth2));
			//load image in the adapter
//			SLoadImage.getInstance().loadImage(imageview2, this.mT2.getCover(), 150, 150,SLoadImage.SMALL);
			imrecy2 = new ImgRecy(this.mT2.getCover(),150);
			//set data
			TextView townname2 = (TextView)view2.findViewById(R.id.view_mine_town_towname);
			TextView address2 = (TextView)view2.findViewById(R.id.view_mine_town_address);
			TextView goodview2 = (TextView)view2.findViewById(R.id.view_mine_town_goods);
			townname2.setText(this.mT2.getTownname());
			address2.setText(this.mT2.getGeoinfo().getCity()+this.mT2.getGeoinfo().getFreeaddr());
			goodview2.setText(""+mT2.getGood());
			//bind accident
			view2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (mOwner2 == null) {
						 mOwner2 = new ModelUser();
						 mOwner2.setCover(UserPreUtil.getCover());
						 mOwner2.setName(UserPreUtil.getUsername());
						 mOwner2.setUserid(UserPreUtil.getUserid());
						 mOwner2.setFans(0);
					}
					TownActivity.startAction(mContext, mT2, mOwner2);
				}
				
			});
			linearlayout.addView(view2);
		}		
		return linearlayout;
	}
	
	public ImgRecy[] getImg() {		
		return new ImgRecy[]{imrecy1,imrecy1};
	}
}