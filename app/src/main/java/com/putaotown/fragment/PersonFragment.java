package com.putaotown.fragment;

import com.putaotown.AboutActivity;
import com.putaotown.CommunitylistActivity;
import com.putaotown.DraftActivity;
import com.putaotown.EditUserActivity;
import com.putaotown.FAQActivity;
import com.putaotown.FavolistActivity;
import com.putaotown.MainActivity;
import com.putaotown.MyTownsListActivity;
import com.putaotown.R;
import com.putaotown.SublistActivity;
import com.putaotown.TownActivity;
import com.putaotown.UpdateCallBack;
import com.putaotown.UserActivity;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UpdateUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelAppUpdate;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.update.UpdateService;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class PersonFragment extends Fragment implements UpdateCallBack
{
	private View mMainView;
	private ImageView mUserCoverView;
	private String cover;
	private View mOPenMyPage;
	private View mOpenMyTowns;
	private View mEditProfile;
	private View mOpenSubcribe;
	private View mOpenFavorite;
	private View mOpenCommunity;
	private View mExitView;
	private View mDraftView;
	private View mCheckUpdate;
	private View mAboutView;
	private View mFAQ;
	private ApplyTown mTownObject;
	private ModelUser mUserObject;
	private ModelAppUpdate latestinfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if (mMainView == null) {
			mMainView = inflater.inflate(R.layout.fragment_setting, container,false);
			mUserCoverView = (ImageView) mMainView.findViewById(R.id.fragment_setting_usercover);
			cover  = UserPreUtil.getCover();
			BianImageLoader.getInstance().loadImage(this.mUserCoverView, cover,90);
			
			this.mOPenMyPage = mMainView.findViewById(R.id.fragment_setting_mypage);
			this.mOpenMyTowns = mMainView.findViewById(R.id.fragment_setting_mytowns);
			this.mEditProfile = mMainView.findViewById(R.id.fragment_setting_editprofile);
			this.mOpenSubcribe = mMainView.findViewById(R.id.fragment_setting_subcribe);
			this.mOpenFavorite = mMainView.findViewById(R.id.fragment_setting_favorite);
			this.mOpenCommunity = mMainView.findViewById(R.id.fragment_setting_community);
			this.mExitView = mMainView.findViewById(R.id.fragment_setting_exit);
			this.mDraftView = mMainView.findViewById(R.id.fragment_setting_draftbox);
			this.mCheckUpdate = mMainView.findViewById(R.id.fragment_setting_checkupdate);
			this.mAboutView = mMainView.findViewById(R.id.fragment_setting_about);
			this.mFAQ = mMainView.findViewById(R.id.fragment_setting_faq);
			
			this.mUserObject = new ModelUser();
			mUserObject.setUserid(UserPreUtil.getUserid());
			mUserObject.setName(UserPreUtil.getUsername());
			mUserObject.setCover(UserPreUtil.getCover());
			this.mOPenMyPage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					UserActivity.startAction(PersonFragment.this.getActivity(), mUserObject);
				}
				
			});
			this.mOpenMyTowns.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					MyTownsListActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});
			this.mFAQ.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					FAQActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});
			this.mEditProfile.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					EditUserActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});this.mOpenSubcribe.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SublistActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});this.mOpenFavorite.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					FavolistActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});this.mOpenCommunity.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					CommunitylistActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});this.mExitView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					FragmentManager fm = PersonFragment.this.getActivity().getSupportFragmentManager();
					LogOutDialogFragment dialog = new LogOutDialogFragment();
					dialog.show(fm, "退出");
				}
				
			});this.mDraftView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					DraftActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});this.mCheckUpdate.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					try {
						UpdateUtil.checkUpdate(PersonFragment.this);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
						Toast.makeText(PersonFragment.this.getActivity(), "网络不给力", Toast.LENGTH_SHORT).show();
					}
				}
				
			});this.mAboutView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					AboutActivity.startAction(PersonFragment.this.getActivity());
				}
				
			});
		}		
		return mMainView;
	}

	@Override
	public void onReceive(ModelAppUpdate info) {
		// TODO Auto-generated method stub
		int currVersionCode = 0;
		try {
			currVersionCode = this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(currVersionCode > 0 && info.getVersioncode() > currVersionCode) {
			this.latestinfo = info;
			FragmentManager fm = this.getActivity().getSupportFragmentManager();
			UpdateDialogFragment dialog = new UpdateDialogFragment(this.latestinfo);
			dialog.show(fm, "updatedialog");
		} else {
			//已经是最近版本不用处理
			Toast.makeText(this.getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
		}
	}
}