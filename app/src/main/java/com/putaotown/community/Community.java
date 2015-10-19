package com.putaotown.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.putaotown.R;
import com.putaotown.community.adapter.CommunityAdapter;
import com.putaotown.community.models.ModelCommuHeader;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.community.CommunityHeaderUtil;
import com.putaotown.net.community.CommunityTieThUtil;
import com.putaotown.net.community.JoinBBS;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class Community extends AppCompatActivity implements OnClickListener,IXListViewListener
{
	private static int communityid;
	private View mNewTheme;
	private XListView mXList;
	private TextView btnjoin;
	private TextView texthint;
	private SystemBarTintManager mTintManager;
	
	private ModelCommuHeader mHeader;
	private List<ModelTieTheme> ties;
	private CommunityAdapter communityAdater;
	private int adminId;
	private boolean hasjoin;
	private boolean isRefresh = false;
	private List<Integer> mRejectids;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

		initViews();
	}
	
	public void initViews() {
		this.setActionBar();
		this.isRefresh = true;
		this.ties = new ArrayList<ModelTieTheme>();
		this.mRejectids = new ArrayList<Integer>();
		this.mXList = (XListView)findViewById(R.id.activity_community_xlist);
//		this.mNewTheme = findViewById(R.id.activity_community_write);
		this.texthint = (TextView) findViewById(R.id.activity_community_texthint);
		this.texthint.setVisibility(View.VISIBLE);
		this.mXList.setPullRefreshEnable(true);
		this.mXList.setPullLoadEnable(true);
		this.mXList.setXListViewListener(this);
		
		//第一次进入，刷新
		firstload();
	}
	/**第一次进入获取社区头和主题帖列表*/
	public void firstload() {
		mHeader = null;
		ties = new ArrayList<ModelTieTheme>();
		this.isRefresh = true;
		if (this.mRejectids.size() > 0)
			this.mRejectids = new ArrayList<Integer>();
		CommunityHeaderUtil.get(this, communityid);
		CommunityTieThUtil.get(this, communityid,this.mRejectids);
	}
	/**刷新*/
	public void refresh() {
		
	}
	/**header响应*/
	public void onGetHeader(ModelCommuHeader header) {
		this.onLoad();
		if (header != null) {
			this.mHeader = header;
			this.adminId = this.mHeader.getAdminid();
			this.hasjoin = this.mHeader.isHasjoin();
			LogUtil.v("Community info: ", "Got header");
			if (this.ties != null) {
				this.texthint.setVisibility(View.GONE);
				this.onLoad();
				this.communityAdater = new CommunityAdapter(this,mHeader,ties);
				this.mXList.setAdapter(communityAdater);
				this.communityAdater.notifyDataSetChanged();
				LogUtil.v("Community info: ", "refresh list!");
			}
		}
	}
	/**主题贴响应*/
	public void onGetTieth(List<ModelTieTheme> ties) {
		this.onLoad();
		if (ties != null && ties.size() > 0 ) {
			LogUtil.v("Community info: ", "Got ties!");
			this.ties = ties;
			this.texthint.setVisibility(View.GONE);
			if (mHeader != null && isRefresh) {				
				isRefresh = false;
				this.texthint.setVisibility(View.GONE);
				if (this.communityAdater != null)
					this.communityAdater.update(mHeader, ties);
				else {
					this.communityAdater = new CommunityAdapter(this,mHeader,ties);
					this.mXList.setAdapter(communityAdater);
					this.communityAdater.notifyDataSetChanged();
				}
				LogUtil.v("Community info: ", "refresh list!");
			} else if(this.mHeader != null) {
				this.communityAdater.loadMore(ties);
			}
			
			//update rejectids
			for (int i=0;i<ties.size();i++) {
				this.mRejectids.add(ties.get(i).getTieid());
			}
		} 
//		else if (isRefresh){
//			isRefresh = false;
//			if (mHeader != null ) {
//				this.texthint.setVisibility(View.GONE);
//				this.mXList.setFooterHint("还没有帖子");
//				this.communityAdater = new CommunityAdapter(this,mHeader,this.ties);
//				this.mXList.setAdapter(communityAdater);
//				this.communityAdater.notifyDataSetChanged();
//			}
//		} 
		else {
			this.texthint.setVisibility(View.GONE);
			this.mXList.setFooterHint("暂无更多");
		}
	}
	/**加入社区响应*/
	public void onJoinBBS(boolean stat) {
		if (this.btnjoin != null ){
			btnjoin.setVisibility(View.GONE);
			this.hasjoin =true;
			Toast.makeText(this, "成功加入社区", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void startAction(Context context,int id) {
		communityid = id;
		context.startActivity(new Intent(context,Community.class));
	}
	
	public void backEvent(View source){
		finish();
	}
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent){
		LogUtil.v("Community info: ", "result info: requestCode: "+requestCode+" resultCode: "+resultCode);
		switch(resultCode) {
		case RESULT_OK:
			firstload();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.listitem_community_header_btnjoin:
			btnjoin = (TextView) v;
			JoinBBS.join(this, communityid);
			break;
		}
	}
	
	public static class OnItemSelected implements OnClickListener
	{
		private ModelTieTheme tie;
		private int adminid;
		private Community activity;
		public OnItemSelected(Community ac,ModelTieTheme tie,int adminid) {
			this.tie = tie;
			this.adminid = adminid;
			this.activity = ac;
		}

		@Override
		public void onClick(View v) {
			LogUtil.v("Community.OnItemSelected info: ", "onclick !");
			ThemeTieActivity.startAction(activity, tie, adminid);
		}
		
	}
	
	/**结束下拉上拉动画*/
	private void onLoad() {
		this.mXList.stopRefresh();
		this.mXList.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXList.setRefreshTime(datestr);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		isRefresh = true;
		this.mRejectids.removeAll(mRejectids);
		mHeader = null;
		ties = null;
		this.firstload();
	}

	@Override
	public void onLoadMore() {
		this.isRefresh = false;
		CommunityTieThUtil.get(this, communityid,this.mRejectids);
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_bbs_newtheme, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		View okv = v.findViewById(R.id.actionbar_ok);
		okv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (adminId != UserPreUtil.getUserid() && hasjoin != true)
					Toast.makeText(Community.this, "你还没有加入该社区", Toast.LENGTH_SHORT).show();
				else
					NewThemeActivity.startAction(Community.this,communityid);
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	    	finish();
	        return true; 
	        default:
	        	return true;
	    }  
	}
}