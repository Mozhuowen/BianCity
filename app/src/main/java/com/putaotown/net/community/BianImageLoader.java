package com.putaotown.net.community;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.net.SLoadImage;

public class BianImageLoader
{
	private static BianImageLoader instance;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;
	private static final String host = "http://xiaocheng.b0.upaiyun.com/";
	
	private BianImageLoader() {
		//other zujian image option
		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_picture_loading)
		.showImageOnFail(R.drawable.ic_picture_loadfailed)
		.cacheInMemory(true).cacheOnDisk(true)
		.resetViewBeforeLoading(true).considerExifParams(false)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		
		
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int memCacheSize = maxMemory / 6;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(PutaoApplication.getContext())
		.threadPoolSize(5)
		// default Thread.NORM_PRIORITY - 1
		.threadPriority(Thread.NORM_PRIORITY)
		// default FIFO
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		// default
		.denyCacheImageMultipleSizesInMemory()
		.memoryCache(new LruMemoryCache(memCacheSize))
		.memoryCacheSize(memCacheSize)
		.diskCacheSize(50 * 1024 * 1024)
		.diskCache(
				new UnlimitedDiskCache(StorageUtils.getCacheDirectory(PutaoApplication.getContext(), true)))
		.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
		// default
		.imageDownloader(new BaseImageDownloader(PutaoApplication.getContext()))
		// default
		.imageDecoder(new BaseImageDecoder(false))
		// default
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
		// default
		.defaultDisplayImageOptions(imageOptions)
		.build();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		
		//biancity image option
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.showImageOnLoading(R.color.basecolor)
		.build();
	}
	
	public static BianImageLoader getInstance() {
		if (instance == null) {
			instance = new BianImageLoader();
		}
		return instance;
	}
	
	public void loadImage(ImageView imageview,String imageuri,int size) {
		if (!imageuri.contains("http")){
			imageuri = host + imageuri;
			if ( size >0 && size <=200) {
				imageuri += "!small";
			} else if ( size >200 && size <=500) {
				imageuri += "!medium";
			} else {
				imageuri +="!large";
			}
		}
		imageLoader.displayImage(imageuri, imageview, options);
	}
	
	public void loadImage(ImageView imageview,String imageuri,int size,final PhotoViewAttacher mAttacher) {
		if (!imageuri.contains("http") && !imageuri.contains("file")){
			imageuri = host + imageuri;
			if ( size >0 && size <=200) {
				imageuri += "!small";
			} else if ( size >200 && size <=500) {
				imageuri += "!medium";
			} else {
				imageuri +="!large";
			}
		}
		imageLoader.displayImage(imageuri, imageview,options, new ImageLoadingListener(){
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				// TODO Auto-generated method stub
				mAttacher.update();
			}
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub	
			}
		});
	}
}