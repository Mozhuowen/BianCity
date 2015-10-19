package com.putaotown.imageviewer;

import com.putaotown.R;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.tools.LogUtil;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {
	private static final String host = "http://putaoimage.b0.upaiyun.com/";
	private String mImageUrl;
	public ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	private int screenWidth;
	private boolean isloadimage = false;

	public static ImageDetailFragment newInstance(String imageUrl,int width) {
		final ImageDetailFragment f = new ImageDetailFragment();
		LogUtil.v("ImageDetailFragment info: ", "new a instance!");
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		args.putInt("screenWidth", width);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
		screenWidth = getArguments() != null ? getArguments().getInt("screenWidth") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageviewer_image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
				System.gc();
			}
		});

	//	progressBar = (ProgressBar) v.findViewById(R.id.imageviewer_loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//这里放置加载图片逻辑
//		SLoadImage sloadimage = SLoadImage.getInstance();
//		sloadimage.loadImage(mImageView, mImageUrl, 500, 500,mAttacher);
		BianImageLoader.getInstance().loadImage(mImageView, mImageUrl, 850, mAttacher);
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//释放内存
		LogUtil.v("ImageDetailFragment info: ", "to be destroyed,free memory!");
		mImageView.setImageBitmap(null);
	}
}
