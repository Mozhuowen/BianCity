package com.putaotown.net;

import java.io.File;
import java.io.IOException;

import com.sina.weibo.sdk.utils.LogUtil;

import android.content.Context;
import android.util.Log;


public class UpImage extends Thread
{
	private static String localfilepath;
	private static String cover;
	private String filePath;
	private Context context;
	private boolean result;
	private PutaoBaseNetwork pt;
	private static final String BUCKET_NAME = "putaoimage";
	private static final String OPERATOR_NAME = "awen";
	private static final String OPERATOR_PWD = "A12345687";
	
	/** 根目录 */
	private static final String DIR_ROOT = "/";

	UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);
	
	public UpImage(Context context,PutaoBaseNetwork ptnetwork,String filepath,String imagename) {
		localfilepath = filepath;
		cover = imagename;
		filePath = DIR_ROOT + cover;
		this.context = context;
		this.pt = ptnetwork;
	}
	
	public boolean getResult() {
		return this.result;
	}
	
	
	// 测试图片文件测试
	public void WritePic(){
		if (cover != null)
			LogUtil.d("Uploading image:", cover);
		// 本地待上传的图片文件
		File file = new File(localfilepath);
		upyun.setTimeout(60);
		// 设置待上传文件的 Content-MD5 值
		// 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
		try {
//			throw new IOException();
			upyun.setContentMD5(UpYun.md5(file));
			// 上传文件，并自动创建父级目录（最多10级）
			result = upyun.writeFile(filePath, file, true);
		} catch (IOException e) {
			LogUtil.v("Upload Image Exception: ", e.toString());
			result = false;
		}		
	}
	
	public void run() {
		WritePic();
		pt.onFinishUpImage(this);
	}
}