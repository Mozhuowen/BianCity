package com.putaotown.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.putaotown.R;
import com.putaotown.fragment.HotFragment;
import com.putaotown.fragment.MineFragmentx;
import com.putaotown.fragment.MsgFragment;
import com.putaotown.fragment.NearFragment;
import com.putaotown.fragment.PersonFragment;

public class BCMainAdapter extends FragmentStatePagerAdapter implements com.putaotown.views.PagerSlidingTabStrip.IconTabProvider
{
	private HotFragment hotFragment;
	private NearFragment nearFragment;
	private MineFragmentx mineFragment;
	private MsgFragment msgFragment;
	private PersonFragment personFragment;
	private final String[] titles = {"主页","附近","消息","我的"};
	public BCMainAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getPageIconResId(int position) {
		switch(position) {
		case 0:
			return R.drawable.iconfont_home;
		case 1:
			return R.drawable.iconfont_dingxiang;
		case 2:
			return R.drawable.iconfont_message;
		case 3:
			return R.drawable.iconfont_person;
		}
		return 0;
	}

	@Override
	public int getSelectedPageIconResId(int position) {
		switch(position) {
		case 0:
			return R.drawable.iconfont_home_press;
		case 1:
			return R.drawable.iconfont_dingxiang_press;
		case 2:
			return R.drawable.iconfont_message_press;
		case 3:
			return R.drawable.iconfont_person_press;
		}
		return 0;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
		case 0:
			if (this.hotFragment == null ) {
				this.hotFragment = new HotFragment();
			}
			return this.hotFragment;
		case 1:
			if (this.nearFragment == null ) {
				this.nearFragment = new NearFragment();
			}
			return this.nearFragment;
		case 2:
			if (this.msgFragment == null) {
				this.msgFragment = new MsgFragment();
			}
			return this.msgFragment;
		case 3:
			if (this.personFragment == null ){
				this.personFragment = new PersonFragment();
			}
			return this.personFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titles.length;
	}
	
}