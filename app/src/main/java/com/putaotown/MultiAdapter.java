package com.putaotown;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackageComment;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import de.hdodenhof.circleimageview.CircleImageView;

public class MultiAdapter extends BaseAdapter implements OnScrollListener,OnClickListener
{
	private static final int TYPE_MAX_COUNT = 2;
	private static final int ITEMTYPE_HEADER = 0;	//getview返回的当前view的类别，分头部和头部之外的
	private static final int ITEMTYPE_ITEM = 1;
	public static final int SHOWTYPE_TOWN = 0;	//显示内容的类别，
	public static final int SHOWTYPE_PUTAO = 1;
	public static final int SHOWTYPE_COMMENT = 2;
	public static final int SHOWTYPE_MYTOWN = 3;	//显示我创建的town
	private static final int CLICKTYPE_USER = 0;	//点击内容区分
	private static final int CLICKTYPE_GOOD = 1;
	private static final int CLICKTYPE_REPLY = 2;
	private int mCurrShowType = 0;
	private View mFirstView;
	public boolean isFirstEnter = true;
	private Activity ac;
	private List<View> viewlist;
	private List mData;
	private Queue<ImgThing> mToShowImg;	//固定长度队列，最大长度等于当前屏幕显示的数量
	/**第一个可见view的下标*/
	private int mFirstVisibleItem;
	/**一屏有多少行view可见*/
	private int mVisibleItemCount;
	
	public MultiAdapter(Activity ac,View firstview,List data,int currshowtype) {
		this.ac = ac;
		this.mFirstView = firstview;
		this.mData = data;
		this.mCurrShowType = currshowtype;
		if (currshowtype == SHOWTYPE_TOWN || currshowtype == SHOWTYPE_MYTOWN)
			this.mToShowImg = new ArrayBlockingQueue<ImgThing>(8);
		else
			this.mToShowImg = new ArrayBlockingQueue<ImgThing>(12);
	}
	/**获取view的类别总数*/
	@Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
	/**获取当前position的view的类别*/
	@Override
    public int getItemViewType(int position) {
        return position == 0?0:1;
    }
	
	@Override
	public int getCount() {
		// 数据总量加上view头
//		LogUtil.v("MultiAdapter info Data size: ",""+ (this.mData.size()));
//		LogUtil.v("MultiAdapter info getCount: ",""+ (this.mData.size()/2 + this.mData.size()%2 + 1));
		if (this.mCurrShowType == SHOWTYPE_TOWN || this.mCurrShowType == SHOWTYPE_MYTOWN)
			return this.mData.size()/2 + this.mData.size()%2 + 1;
		else
			return this.mData.size() + 1;
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
		LogUtil.v("MultiAdapter info: ", "positon: "+position);
		ViewHolder holder = null;
		int showtype = this.mCurrShowType;
		int itemtype = getItemViewType(position);
		
		//判断当前页面显示内容
		switch(showtype) {
		case SHOWTYPE_TOWN:	//显示某用户小镇
			//判断是否viewheader
			switch(itemtype) {
			case ITEMTYPE_HEADER:
				convertView = this.returnFirstView(convertView);
				break;
			case ITEMTYPE_ITEM:
				convertView = this.setTownItemView(convertView, holder, position);
				break;
			}
			break;
		case SHOWTYPE_MYTOWN:	//显示我的小镇
			//判断是否viewheader
			switch(itemtype) {
			case ITEMTYPE_HEADER:
				convertView = this.returnFirstView(convertView);
				break;
			case ITEMTYPE_ITEM:
				convertView = this.setTownItemView(convertView, holder, position);
				break;
			}
			break;
		case SHOWTYPE_PUTAO:	//显示边城页面,listitem为故事
			switch(itemtype) {
			case ITEMTYPE_HEADER:
				convertView = this.returnFirstView(convertView);
				break;
			case ITEMTYPE_ITEM:
				convertView = this.setPutaoItemView(convertView, holder, position);
				break;
			}
			break;
		case SHOWTYPE_COMMENT:	//显示故事界面，listitem为评论
			switch(itemtype) {
			case ITEMTYPE_HEADER:
				convertView = this.returnFirstView(convertView);
				break;
			case ITEMTYPE_ITEM:
				convertView = this.setCommentItemView(convertView,holder,position);
				break;
			}
			break;
		}
		 	
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE && this.mCurrShowType != SHOWTYPE_COMMENT) {
        	LogUtil.v("MyListAdapter list info: ", "mFirstVisibleItem: "+mFirstVisibleItem + " mVisibleItemCount: "+mVisibleItemCount);
        	loadImg();	
        }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;  
        mVisibleItemCount = visibleItemCount;  
//        LogUtil.v("MyListAdapter info: ", "onScroll! firstVisibleItem: "+firstVisibleItem+" visibleItemCount: "+visibleItemCount
//        		+" mToShowImg size: "+ mToShowImg.size());
        //第一次或刷新时调用
        if (isFirstEnter && visibleItemCount > 0 && mToShowImg.size() > 0) { 
	            loadImg();
	            isFirstEnter = false;  
        }
       
	}
	
	public View returnFirstView(View convertView) {
		if (convertView == null)
			return this.mFirstView;
		else			
			return convertView;
	}
	/**加载评论时直接load头像*/
	public View setCommentItemView(View convertView,ViewHolder holder,int position) {
		if (convertView == null ) {
			holder = new ViewHolder();
			convertView = ListItemComment.getNewView(ac);
			holder.image = (ImageView)convertView.findViewById(R.id.listitem_comment_image);
			holder.name = (TextView)convertView.findViewById(R.id.listitem_comment_username);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_comment_date);
			holder.goods = (TextView)convertView.findViewById(R.id.listitem_comment_good);
            holder.goodthumb = (ImageView) convertView.findViewById(R.id.listitem_comment_good_image);
			holder.content = (TextView)convertView.findViewById(R.id.listitem_comment_content);
			holder.comment = convertView.findViewById(R.id.listitem_comment_cicon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		//set data
		PackageComment pc = (PackageComment)this.mData.get(position-1);
//		holder.image.setImageResource(R.drawable.empty_photo);imageview.
//		holder.image.setImageResource(R.color.basecolor);
		//处理回复情况
		if (pc.getReplyname() != null && pc.getReplyname().length() > 0) {
			holder.name.setText(this.ac.getString(R.string.comment_reply, pc.getUsername(),pc.getReplyname()));
		}else
			holder.name.setText(pc.getUsername());
		holder.time.setText(pc.getTime());
		holder.goods.setText(""+pc.getGoods());
		String contentstr = pc.getContent();
		contentstr = contentstr.replaceAll("(@.*?):", "<font color='#1E90FF'>$1</font>: ");
		holder.content.setText(Html.fromHtml(contentstr));
		holder.ir1 = new ImgRecy(pc.getCover(),90,true);
		this.addToShowImg(new ImgThing(holder.image,holder.ir1));
		//设置拇指图标
		if (pc.isDogood()) {
            holder.goodthumb.setImageResource(R.drawable.ic_list_thumbup);
        } else {
            holder.goodthumb.setImageResource(R.drawable.ic_list_thumb);
        }
		//set click listener
		holder.image.setOnClickListener(this);
		holder.image.setTag(pc);
		holder.goodthumb.setOnClickListener(((PutaoxActivity)ac).new GoodListener(holder.goods,holder.goodthumb,pc.getCommentid(),!pc.isDogood()));
		holder.comment.setOnClickListener(((PutaoxActivity)ac).new OnReply(pc.getUsername(),pc.getContent(),pc.getCommentid()));
		
		convertView.setTag(holder);
		BianImageLoader.getInstance().loadImage(holder.image, pc.getCover(), 90);
		return convertView;
	}
	
	public View setPutaoItemView(View convertView,ViewHolder holder,int position) {
		if (convertView == null ) {
			holder = new ViewHolder();
			convertView = ListItemPutao.getNewView(ac);
			holder.image = (ImageView)convertView.findViewById(R.id.listitem_putao_image);
			holder.name = (TextView)convertView.findViewById(R.id.listitem_putao_title);
			holder.time = (TextView)convertView.findViewById(R.id.listitem_putao_date);
			holder.goods = (TextView)convertView.findViewById(R.id.listitem_putao_good);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		//set data
		PackagePutao pp = (PackagePutao)this.mData.get(position-1);
		holder.image.setImageBitmap(null);
		
		holder.name.setText(pp.getTitle());
		holder.time.setText(pp.getCreatetime());
		holder.goods.setText(""+pp.getGoods());
		holder.ir1 = new ImgRecy(pp.getCover(),90);
		holder.extraData = pp;
		this.addToShowImg(new ImgThing(holder.image,holder.ir1));
		convertView.setTag(holder);
		convertView.setOnClickListener(this);
		
		convertView.setTag(holder);
		
		BianImageLoader.getInstance().loadImage(holder.image, pp.getCover(), 90);
		return convertView;
	}
	
	public View setTownItemView(View convertView,ViewHolder holder,int position) {
		if (convertView == null ) {
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
		ApplyTown at1 = null;
		ApplyTown at2 = null;
		at1 = (ApplyTown)this.mData.get((position-1)*2);		
		holder.cover1.setImageBitmap(null);
		holder.townname1.setText(at1.getTownname());
		holder.address1.setText(at1.getGeoinfo().getCity()+at1.getGeoinfo().getFreeaddr());
		holder.goods1.setText(at1.getGood()+"");						
		holder.ir1 = new ImgRecy(at1.getCover(),150);			
		this.addToShowImg(new ImgThing(holder.cover1,holder.ir1));
		((LinearLayout)convertView).getChildAt(0).setTag(at1);
		((LinearLayout)convertView).getChildAt(0).setOnClickListener(this);
		if (((position-1)*2+1)< mData.size()) {				
			at2 = (ApplyTown)this.mData.get((position-1)*2 + 1);
			holder.cover2.setImageBitmap(null);
			holder.townname2.setText(at2.getTownname());
			holder.address2.setText(at2.getGeoinfo().getCity()+at2.getGeoinfo().getFreeaddr());
			holder.goods2.setText(at2.getGood()+"");
			holder.ir2 = new ImgRecy(at2.getCover(),150);
			this.addToShowImg(new ImgThing(holder.cover2,holder.ir2));
			((LinearLayout)convertView).getChildAt(1).setTag(at2);
			((LinearLayout)convertView).getChildAt(1).setOnClickListener(this);
			if (((LinearLayout)convertView).getChildAt(1).getVisibility() == View.GONE)
				((LinearLayout)convertView).getChildAt(1).setVisibility(View.VISIBLE);
		} else 
			((LinearLayout)convertView).getChildAt(1).setVisibility(View.GONE);								
		return convertView;
	}
	
	public void addToShowImg(ImgThing i) {
//		LogUtil.v("MyListAdapter info: ", "add one ImgThig!");
		switch(this.mCurrShowType) {
		case SHOWTYPE_TOWN:
			if (this.mToShowImg.size() >= 6) {							
				this.mToShowImg.poll();
			}
			break;
		case SHOWTYPE_MYTOWN:
			if (this.mToShowImg.size() >= 6) {							
				this.mToShowImg.poll();
			}
			break;
		default:
			if (this.mToShowImg.size() >= 10) {							
				this.mToShowImg.poll();
			}
			break;
		}
		
		this.mToShowImg.add(i);
	}
	
	public void loadImg() {
		if (this.mToShowImg.size()>0) {			
			for (Iterator<ImgThing> it = this.mToShowImg.iterator();it.hasNext();) {
				ImgThing img = it.next();
				ImageView imageview = img.img;
				if (img != null && imageview != null) {
					if (!img.imgrecy.isheadcover)	//判断是否是价值头像
						BianImageLoader.getInstance().loadImage(imageview, img.imgrecy.imgname, img.imgrecy.size);
						
					else{
						loadUserCover(imageview,img.imgrecy.imgname);
					}
	            	img.imgrecy.isShow = true;
				}
			}
		}
	}
	/**将imageview和对应的图片信息一一对应*/
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
	/**view需要绑定的对象，extraData中携带更多其他对象*/
	public static class ViewHolder
	{
		public static final int header = 0;
		public static final int dogood = 1;
		public static final int replycomment = 2;
		public ImageView image;
		public TextView name;
		public TextView freeaddr;
		public TextView goods;
		public ImageView goodthumb;
		public TextView time;
		public TextView username;
		public TextView content;
		public View comment;
		//townviewitem需要的属性
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
		public Object extraData;
		public int commentClickType;
	}
	/**点击打开小城事件绑定*/
	@Override
	public void onClick(View v) {
		switch(this.mCurrShowType) {
		case SHOWTYPE_TOWN:
			onClickTown(v);
			break;
		case SHOWTYPE_MYTOWN:
			onClickTown(v);
			break;
		case SHOWTYPE_PUTAO:
			onClickPutao(v);
			break;
		case SHOWTYPE_COMMENT:
			onClickComment(v);
			break;
		}		
	}
	public void onClickComment(View v) {
		LogUtil.v("MultiAdapter info: ", "v.getTag(): "+v.getTag());
		PackageComment pc = (PackageComment)v.getTag();
		if (v instanceof CircleImageView) {
			ModelUser user = new ModelUser();
			user.setUserid(pc.getUserid());
			user.setName(pc.getUsername());
			user.setCover(pc.getCover());
			UserActivity.startAction(ac, user);
		} else if (v instanceof TextView) {
			 
		} else if (v instanceof ImageView) {
			
		}
	}
	/**点击事件*/
	public void onClickTown(View v) {		
		ApplyTown at = (ApplyTown)v.getTag();
		ModelUser owner = new ModelUser();
		if (at.getUsercover() == null) {
			owner.setCover(UserPreUtil.getCover());
			owner.setName(UserPreUtil.getUsername());
			owner.setUserid(UserPreUtil.getUserid());
			owner.setFans(0);
		} else {
			owner.setCover(at.getUsercover());
			owner.setName(at.getUsername());
			owner.setUserid(at.getUserid());
		}
		if (this.mCurrShowType == SHOWTYPE_MYTOWN)
			TownActivity.startAction(ac, at, owner,true);
		else
			TownActivity.startAction(ac, at, owner,false);
	}
	public void onClickPutao(View v) {
		PackagePutao pp = (PackagePutao)((ViewHolder)v.getTag()).extraData;
		TownActivity pac = (TownActivity)ac;
		if (pac.mIsEditModel)
			PutaoxActivity.startAction(ac, pp,true);
		else
			PutaoxActivity.startAction(ac, pp,false);
	}
	/**加载用户头像*/
	private void loadUserCover(ImageView mCover,String cover) {
		BianImageLoader.getInstance().loadImage(mCover, cover, 90);
			
	}
}