package com.putaotown.net;

import java.io.File;
import java.util.Map;

import com.putaotown.PutaoApplication;
import com.putaotown.tools.LogUtil;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MultiUpImage
{
	// 空间名
	String bucket = "xiaocheng";
	// 表单密钥
	String formApiSecret = "8giBNZVp2lo8f9c7gf6Q8Wk8BQw=";
	private PutaoBaseNetwork pt;
	/** 云端根目录 */
	private static final String UPYUN_DIR_ROOT = "/";
	private String LOCAL_FILEPATH; 
	private int mTotalBytes;	//总上传大小
	private int mCurrTranBytes = 0;	//已经上传大小
	private ProgressBar progressBar;
	private String[] imagepaths;
	private String[] imagenames;
	private int mHasUp = 0;	//已经成功上传的文件个数
	private boolean mIsError = false;	//记录上传是否出错，如果出错需要停止
	private boolean mIsStopTask = false; 
	private Handler messhandler;
	private UploadTask mCurrTask;	//当前的任务，用于停止
	
	
	/**
	 * 上传多张图片
	 * @param imagepaths 多张图片名数组
	 * @param probar	对话框进度条
	 */
	public MultiUpImage(PutaoBaseNetwork ptnetwork,Handler messhandler,String[] imagenames,ProgressBar probar) {
		this.messhandler = messhandler;
		this.pt = ptnetwork;
		this.imagenames = imagenames;
		this.progressBar = probar;
		this.imagepaths = new String[imagenames.length];
		for (int i=0;i<imagenames.length;i++) {
			this.imagepaths[i] = PutaoApplication.getContext().getFilesDir() + "/image/" +imagenames[i];
		}
		//计算总文件大小
		for (int i=0;i<imagepaths.length;i++) {
			File file = new File(imagepaths[i]);
			this.mTotalBytes += file.length();
		}
		//初始化设置进度条	
		Message message = new Message();
		message.arg1 = 10;
		message.what = 400;
		messhandler.sendMessage(message);
	}
	
	
	public void upload() {
		if (mIsStopTask){
			return;
		}else if (!mIsError && this.mHasUp < imagepaths.length) {
			LogUtil.v("MultiUpImage info: ", "start to upload Image: "+imagepaths[mHasUp]);
//			String filepath = PutaoApplication.getContext().getFilesDir() + "/image/" +imagepaths[mHasUp];
			mCurrTask = new UploadTask(this,imagepaths[mHasUp],imagenames[mHasUp]);
			mCurrTask.execute();
		} else {
			pt.onFinishUpImage(mIsError);
		}
	}
	
	public void cancelTask() {
		LogUtil.v("MultiUpImage info: ", "Task cancel!");
		mCurrTask.cancel(true);
		this.mIsStopTask = true;
	}
	
	public class UploadTask extends AsyncTask<Void, Void, String> {
		private String localFilePath = null;
		private String savePath = null;
		private MultiUpImage callback;
		private long thisonetotal = 0;
		
		public UploadTask(MultiUpImage m,String filepath,String filename) {
			this.localFilePath = filepath;
			this.savePath = UPYUN_DIR_ROOT + filename;
			this.callback = m;
			LogUtil.v("MultiUpImage info: task ", "localFilePath: "+localFilePath+" savePath: "+savePath);
		}
		
		@Override
		protected String doInBackground(Void... params) {
			File localFile = new File(localFilePath);
			try {
				ProgressListener progressListener = new ProgressListener() {
					
					@Override
					public void transferred(long transferedBytes, long totalBytes) {
						// do something...
						LogUtil.v("Upload info:","trans:" + transferedBytes + "; total:" + totalBytes);
						thisonetotal = totalBytes;
						transferedBytes = transferedBytes + mCurrTranBytes;
						int show = (int)(100 * ((double)transferedBytes)/((double)mTotalBytes));
						Message message = new Message();
						message.what = 400;
						message.arg1 = show;
						messhandler.sendMessage(message);
						LogUtil.v("MultiUpImage info: ", "send message! show: "+show+" mTotalBytes: "+mTotalBytes+" mCurrTranBytes: "+mCurrTranBytes+" math: "+(int)(100 * ((double)transferedBytes)/((double)mTotalBytes)));
					}
				};
				
				CompleteListener completeListener = new CompleteListener() {
					@Override
					public void result(boolean isComplete, String result, String error) {
						// do something...
						LogUtil.v("Upload info:","isComplete:"+isComplete+";result:"+result+";error:"+error);
						if (isComplete == true && error == null)
							mHasUp +=1;
						else 
							mIsError = true;
						mCurrTranBytes += (int)thisonetotal;
						callback.upload();
					}
				};
				
				UploaderManager uploaderManager = UploaderManager.getInstance(bucket);
				uploaderManager.setConnectTimeout(60);
				uploaderManager.setResponseTimeout(60);
				Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
				uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "result";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
//				Toast.makeText(PutaoApplication.getContext(), "成功", Toast.LENGTH_LONG).show();
				LogUtil.v("MultiUpImage info: ", "onPostExecute!");
			} else {
//				Toast.makeText(PutaoApplication.getContext(), "失败", Toast.LENGTH_LONG).show();
				LogUtil.v("MultiUpImage info: ", "onPostExecute!");
			}
		}
	}
}