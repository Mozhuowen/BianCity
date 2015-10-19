package com.putaotown.tools;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;


public class BitmapUtil
{
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		
		return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd,
	        int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
//	    BitmapFactory.decodeResource(res, resId, options);
	    BitmapFactory.decodeFileDescriptor(fd, null, options);
	    // 调用上面定义的方法计算inSampleSize值
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    LogUtil.v("BitmapUtil info: ", "compress size: "+options.inSampleSize);
	    // 使用获取到的inSampleSize值再次解析图片
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFileDescriptor(fd, null, options);
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);
	    // 调用上面定义的方法计算inSampleSize值
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // 使用获取到的inSampleSize值再次解析图片
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static Bitmap decodeBitmapFromInputStream(InputStream in,int reqWidth,int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
//	    BitmapFactory.decodeResource(res, resId, options);
	    BitmapFactory.decodeStream(in, null, options);;
	    // 调用上面定义的方法计算inSampleSize值
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // 使用获取到的inSampleSize值再次解析图片
	    options.inJustDecodeBounds = false;
	    try {
			in.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
//	    return BitmapFactory.decodeResource(res, resId, options);
	    return BitmapFactory.decodeStream(in,null,options);
	}
	
	public static int getImageWidth(View image){
		int w = View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED);
		image.measure(0, 0);
		
		return image.getMeasuredWidth();
	}
}