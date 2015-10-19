package com.putaotown;
/**
 * 主页面ListAdapter
 */
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.LogUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MyListAdapter extends BaseAdapter implements OnScrollListener,OnClickListener
{
	public boolean isFirstEnter = true;
	private Context context;
	private Activity ac;
	private List<View> viewlist;
	private List<ImgThing> imagethings;
	private List<ApplyTown> towndata;
	private Queue<ImgThing> mToShowImg;	//固定长度队列，最大长度等于当前屏幕显示的数量
	/**第一个可见view的下标*/
	private int mFirstVisibleItem;
	/**一屏有多少行view可见*/
	private int mVisibleItemCount;
	
	public MyListAdapter(Context context,List<View> list){
		super();
		this.context = context;
		this.viewlist = list;
	}
	
	public MyListAdapter(Context context,List<View> list,List<ImgThing> i){
		super();
		this.context = context;
		this.viewlist = list;
		this.imagethings = i;
	}
	
	public MyListAdapter(Activity ac,List<ApplyTown> ts) {
		this.ac = ac;
		this.towndata = ts;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (this.towndata == null && this.viewlist != null)
			return this.viewlist.size();
		else
			return this.towndata.size()/2 + this.towndata.size()%2;
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
		if (this.towndata == null || towndata.size() == 0) {
			convertView = null;
			int targetpos = position;
			return viewlist.get(targetpos);	
		} else {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = new LinearLayout(ac);
				View view1 = ListItemTown.getNewView(ac);
				View view2 = ListItemTown.getNewView(ac);
				holder.cover1 = (ImageView)view1.findViewById(R.id.view_mine_town_image);
				holder.townname1 = (TextView)view1.findViewById(R.id.view_mine_town_towname);
				holder.address1 = (TextView)view1.findViewById(R.id.view_mine_town_address);
				holder.goods1 = (TextView)view1.findViewById(R.id.view_mine_town_goods);
				holder.cover2 = (ImageView)view2.findViewById(R.id.view_mine_town_image);
				holder.townname2 = (TextView)view2.findViewById(R.id.view_mine_town_towname);
				holder.address2 = (TextView)view2.findViewById(R.id.view_mine_town_address);
				holder.goods2 = (TextView)view2.findViewById(R.id.view_mine_town_goods);
				((LinearLayout)convertView).addView(view1);
				((LinearLayout)convertView).addView(view2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			//set data
//			LogUtil.v("MyListAdapter info: ", "position: "+position);
			ApplyTown at1 = null;
			ApplyTown at2 = null;
			at1 = this.towndata.get(position*2);		
			holder.cover1.setImageBitmap(null);
			holder.cover1.setTag(at1.getCover());
			holder.townname1.setText(at1.getTownname());
			holder.address1.setText(at1.getGeoinfo().getCity()+at1.getGeoinfo().getFreeaddr());
			holder.goods1.setText(at1.getGood()+"");						
			holder.ir1 = new ImgRecy(at1.getCover(),150);	
			BianImageLoader.getInstance().loadImage(holder.cover1, at1.getCover(), 150);
//			this.addToShowImg(it);
			((LinearLayout)convertView).getChildAt(0).setTag(at1);
			((LinearLayout)convertView).getChildAt(0).setOnClickListener(this);
			if ((position*2+1)< towndata.size()) {				
				at2 = this.towndata.get(position*2 + 1);
				holder.cover2.setImageBitmap(null);
				holder.cover2.setTag(at2.getCover());
				holder.townname2.setText(at2.getTownname());
				holder.address2.setText(at2.getGeoinfo().getCity()+at2.getGeoinfo().getFreeaddr());
				holder.goods2.setText(at2.getGood()+"");
				holder.ir2 = new ImgRecy(at2.getCover(),150);
				BianImageLoader.getInstance().loadImage(holder.cover2, at2.getCover(), 150);
				((LinearLayout)convertView).getChildAt(1).setTag(at2);
				((LinearLayout)convertView).getChildAt(1).setOnClickListener(this);
				if (((LinearLayout)convertView).getChildAt(1).getVisibility() == View.GONE)
					((LinearLayout)convertView).getChildAt(1).setVisibility(View.VISIBLE);
			} else 
				((LinearLayout)convertView).getChildAt(1).setVisibility(View.GONE);								
		}
			
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当静止时才去加载图片，滑动时取消所有正在下载的任务  
        if (scrollState == SCROLL_STATE_IDLE) {  
        	LogUtil.v("MyListAdapter list info: ", "mFirstVisibleItem: "+mFirstVisibleItem + " mVisibleItemCount: "+mVisibleItemCount);
        }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;  
        mVisibleItemCount = visibleItemCount;
	}
	
	public void addToShowImg(ImgThing i) {
		if (this.mToShowImg == null)								// queue要确保有空间，不能阻塞插入操作
			this.mToShowImg = new ArrayBlockingQueue<ImgThing>(8);  //基本手机上一屏显示数量为6
		if (this.mToShowImg.size() >= 6) {							//超过6即可移除
			this.mToShowImg.poll();
		}
		this.mToShowImg.add(i);
	}
	
	public void loadImg() {
		if (this.mToShowImg.size()>0) {			
			for (Iterator<ImgThing> it = this.mToShowImg.iterator();it.hasNext();) {
				ImgThing img = it.next();
				ImageView imageview = img.img;
				if (img != null && imageview != null) {
					BianImageLoader.getInstance().loadImage(imageview,img.imgrecy.imgname,img.imgrecy.size);
	            	img.imgrecy.isShow = true;
				}
			}
		}
	}
	
	public static class ImgThing
	{
		public ImageView img;
		public ImgRecy imgrecy;
		
		public ImgThing(ImageView i,ImgRecy ir)
		{
			this.img = i;
			this.imgrecy = ir;
		}
	}
	
	public static class ViewHolder
	{
		public ImageView cover1;
		public TextView townname1;
		public TextView address1;
		public TextView goods1;
		public ImgRecy ir1;
		public ImageView cover2;
		public TextView townname2;
		public TextView address2;
		public TextView goods2;
		public ImgRecy ir2;
	}
	/**点击打开小城事件绑定*/
	@Override
	public void onClick(View v) {
		ApplyTown at = (ApplyTown)v.getTag();
		ModelUser owner = new ModelUser();
		owner.setCover(at.getUsercover());
		owner.setName(at.getUsername());
		owner.setUserid(at.getUserid());
		TownActivity.startAction(ac, at, owner,false);
	}
}