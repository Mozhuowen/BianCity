package com.putaotown.fragment;

import com.putaotown.MsgShowActivity;
import com.putaotown.MsgShowBBSActivity;
import com.putaotown.MsgShowGoodActivity;
import com.putaotown.MsgShowSysActivity;
import com.putaotown.R;
import com.putaotown.localio.PushMessUtil;
import com.putaotown.tools.LogUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MsgFragment extends Fragment implements OnClickListener
{
	private View mainview;
	public static boolean isShow = false;
	private static int count_comment = 0;
	private static int count_good = 0;
	private static int count_sys = 0;
	private static int count_community = 0;
	private static String count_commentstr;
	//views
	private static TextView mCommentCount;
	private static View mCommentItem;	
	private static TextView mGoodCount;
	private static View mGoodItem;
	private static TextView mSysCount;
	private static View mSysItem;
	private static TextView mCommunityCount;
	private static View mCommunityItem;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mainview == null ){
			mainview = inflater.inflate(R.layout.fragment_news, container,false);
			mCommentCount = (TextView) mainview.findViewById(R.id.fragment_news_comment_count);
			mCommentItem = mainview.findViewById(R.id.fragment_news_comment_layout);
			mGoodCount = (TextView)mainview.findViewById(R.id.fragment_news_goods_count);
			mGoodItem = mainview.findViewById(R.id.fragment_news_good_layout);
			mSysCount = (TextView)mainview.findViewById(R.id.fragment_news_sys_count);
			mSysItem = mainview.findViewById(R.id.fragment_news_sys_layout);
			mCommunityCount = (TextView)mainview.findViewById(R.id.fragment_news_community_count);
			mCommunityItem = mainview.findViewById(R.id.fragment_news_community_layout);
			
			mCommentItem.setOnClickListener(this);
			mGoodItem.setOnClickListener(this);
			mSysItem.setOnClickListener(this);
			mCommunityItem.setOnClickListener(this);
		}
		
		return mainview;
	}
	
	@Override
	public void onStart() {
//		LogUtil.v("MsgFragment info", "is Resumed!");
		super.onStart();
		isShow = true;
		count_comment = PushMessUtil.getUnreadComCou();
		count_good = PushMessUtil.getUnreadGoodCou();
		count_sys = PushMessUtil.getUnreadSysCou();
		count_community = PushMessUtil.getUnreadBBSCou();
		LogUtil.v("MsgFragment info: ", "count_commentstr: "+count_comment + "count_good: "+count_good);
		if (count_comment > 0) {
			mCommentCount.setVisibility(View.VISIBLE);
			mCommentCount.setText(count_comment+"");
		}
		if (count_good > 0) {
			mGoodCount.setVisibility(View.VISIBLE);
			mGoodCount.setText(count_good+"");
		}
		if (count_sys > 0) {
			mSysCount.setVisibility(View.VISIBLE);
			mSysCount.setText(count_sys+"");
		}
		if (count_community > 0) {
			mCommunityCount.setVisibility(View.VISIBLE);
			mCommunityCount.setText(count_community+"");
		}
	}
	
	public static void addMess(int type) {
		switch(type) {
		case 0:
			count_comment++;
			if (isShow) {
				mCommentCount.setVisibility(View.VISIBLE);
				mCommentCount.setText(count_comment+"");
			}
			break;
		case 1:
			count_comment++;
			if (isShow) {
				mCommentCount.setVisibility(View.VISIBLE);
				mCommentCount.setText(count_comment+"");
			}
			break;
		case 2:
			count_good++;
			if (isShow) {
				mGoodCount.setVisibility(View.VISIBLE);
				mGoodCount.setText(count_good+"");
			}
			break;
		case 3:
			count_sys++;
			if (isShow) {
				mSysCount.setVisibility(View.VISIBLE);
				mSysCount.setText(count_sys+"");
			}
			break;
		case 4:
			count_community++;
			if (isShow) {
				mCommunityCount.setVisibility(View.VISIBLE);
				mCommunityCount.setText(count_community+"");
			}
			break;
		}
	}	
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.fragment_news_comment_layout:
			count_comment = 0;
			mCommentCount.setVisibility(View.GONE);
			PushMessUtil.resetUnreadCom();
			MsgShowActivity.startAction(this.getActivity());
			break;
		case R.id.fragment_news_good_layout:
			count_good = 0;
			mGoodCount.setVisibility(View.GONE);
			PushMessUtil.resetUnreadGoods();
			MsgShowGoodActivity.startAction(this.getActivity());
			break;
		case R.id.fragment_news_sys_layout:
			count_sys = 0;
			mSysCount.setVisibility(View.GONE);
			PushMessUtil.resetUnreadSys();
			MsgShowSysActivity.startAction(getActivity());
			break;
		case R.id.fragment_news_community_layout:
			count_community = 0;
			mCommunityCount.setVisibility(View.GONE);
			LogUtil.v("MsgFragment info: ", "start MsgShowBBSActivity!");
			PushMessUtil.resetUnreadBBS();
			MsgShowBBSActivity.startAction(getActivity());
			break;
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		isShow = false;
		LogUtil.v("MsgFragment info", "is Stoped!");
	}
}