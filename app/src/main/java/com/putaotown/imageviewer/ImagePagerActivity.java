package com.putaotown.imageviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

import com.putaotown.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {
	private final String host = "http://putaoimage.b0.upaiyun.com/";
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index"; 
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	
	private int dividerwidth = 0;
	private int screenWidth = 0;
	private DisplayMetrics  dm = new DisplayMetrics();	//屏幕分辨率
	ArrayList<String> urls;	//获取所有的图片链接
	ImagePagerAdapter mAdapter;
	

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_detail_pager);
		//处理获取屏幕宽度
//		getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
//		screenWidth = dm.widthPixels;
		screenWidth = 800;
		
		//当前显示位置
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		//获取所有的图片链接
		urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);

		mPager = (HackyViewPager) findViewById(R.id.pager);
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}
	/**
	 * 查看图片的Adapter
	 * @author awen
	 *
	 */
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public Map<Integer,ImageDetailFragment> fragmentlist = new HashMap<Integer,ImageDetailFragment>();
		public ArrayList<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			ImageDetailFragment fragment = null;
			if (fragmentlist.get(position) == null) {
				fragment = ImageDetailFragment.newInstance(url,screenWidth);
				fragmentlist.put(position, fragment);
			} else
				fragment = fragmentlist.get(position);
			return fragment;
		}
	}
}
