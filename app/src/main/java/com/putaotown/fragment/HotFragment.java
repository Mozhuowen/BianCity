package com.putaotown.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putaotown.BaseTownShow;
import com.putaotown.MyListAdapter;
import com.putaotown.R;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.LoadTownUtil;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class HotFragment extends Fragment implements BaseTownShow,IXListViewListener,OnClickListener
{
	private View mainview;
	private View searchView;
	private TextView mTexthint;
	private LinearLayout mMainLayout;
	private XListView mXList;
	private MyListAdapter mMainAdapter;
	private List<View> mAdapterList = new ArrayList<View>();
	private List<View> mTownviewlist = new ArrayList<View>();
	private List<ApplyTown> towndata = new ArrayList<ApplyTown>();
	private List<MyListAdapter.ImgThing> mImgThings = new ArrayList<MyListAdapter.ImgThing>();
	
	private List<Integer> mRejectids= new ArrayList<Integer>();
	private boolean isRefresh = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if (mainview == null ) {
			mainview = inflater.inflate(R.layout.fragment_hot, container,false);
			this.mTexthint = (TextView)mainview.findViewById(R.id.fragment_hot_texthint);
			this.mTexthint.setOnClickListener(this);
			this.mMainLayout = (LinearLayout)mainview.findViewById(R.id.fragment_hot_mainlayout);
			mXList = (XListView)mainview.findViewById(R.id.fragment_hot_listv);
			mXList.setPullLoadEnable(false);
			mXList.setPullRefreshEnable(true);
			mXList.setXListViewListener(this);
	
			this.mMainAdapter = new MyListAdapter(this.getActivity(),this.towndata);
			mXList.setOnScrollListener(mMainAdapter);
			this.mXList.setAdapter(mMainAdapter);

			load();
		}
		
		return mainview;
	}
	
	public void load() {
		loadHot(mRejectids);
	}

	@Override
	public void loadHot(List<Integer> rejectid) {
		LoadTownUtil.loadHot(this, rejectid);
	}

	@Override
	public void loadNear(GeoInfo geo,List<Integer> rejectid) {
		// TODO Auto-generated method stub
		
	}
	/**返回数据*/
	@Override
	public void onReceive(List<ApplyTown> towns) {
		LogUtil.v("HotFragment info: ", "onReceive!");		
		//add to reject list
		
		if (towns == null ) {
			this.mTexthint.setText(getString(R.string.get_no_content));
		} else if (towns.size() == 0){
			
		} else if (towns.size() > 0) {
			for (int i=0;i<towns.size();i++) {
				this.mRejectids.add(towns.get(i).getTownid());
			}
			this.mMainLayout.removeView(mTexthint);
			this.mXList.setVisibility(View.VISIBLE);
			mXList.setPullLoadEnable(true);
			
			this.towndata.addAll(towns);
			
			this.mMainAdapter.notifyDataSetChanged();
			this.mMainAdapter.isFirstEnter = true;
		}
		//停止缓冲动画
		onLoad();
	}

	private void onLoad() {
		this.mXList.stopRefresh();
		this.mXList.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXList.setRefreshTime(datestr);
	}
	@Override
	public void onRefresh() {		
		this.towndata.removeAll(towndata);
		this.mRejectids.removeAll(mRejectids);
		this.isRefresh = true;
		this.mXList.setPullLoadEnable(false);
		load();
		
	}

	@Override
	public void onLoadMore() {
		this.isRefresh = false;
		load();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.fragment_hot_texthint:
			load();
			this.mTexthint.setText(getString(R.string.xlistview_header_hint_loading));
			break;
		}
	}
}