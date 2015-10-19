package com.putaotown;

import android.os.Handler;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class MyXListListener implements IXListViewListener
{
	private Handler mHandler = new Handler();
	private XListView mListView;
	
	public MyXListListener(XListView xlist){
		this.mListView = xlist;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}
	
}