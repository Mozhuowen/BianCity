package com.putaotown.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import uk.co.senab.photoview.PhotoViewAttacher;
import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.tools.BitmapUtil;
import com.putaotown.tools.LogUtil;
import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.GridView;
import android.widget.ImageView;
/**
 * 硬盘缓存和内存缓存结合，
 * @author awen
 *
 */
public class SLoadImage
{
	public static final int SMALL = 0;
	public static final int MEDIUM = 1;
	public static final int LARGE = 2;
	public static final String STRSMALL = "!small";
	public static final String STRMEDIUM = "!medium";
	public static final String STRLARGE = "!large";
//	private final String host = "http://putaoimage.b0.upaiyun.com/";
	private final String host = "http://xiaocheng.b0.upaiyun.com/";
	private Context context;
	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 图片硬盘缓存核心类。
	 */
	private DiskLruCache mDiskLruCache;

	/**
	 * 单例模式，只各供一个实例
	 */
	private static SLoadImage instance;
	
	private SLoadImage() {
		context = PutaoApplication.getContext();
		
		taskCollection = new HashSet<BitmapWorkerTask>();
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 6;
		LogUtil.v("Memory info: ", "Max Memory is: "+maxMemory);
		// 设置图片缓存大小为程序最大可用内存的1/6
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		try {
			// 获取图片缓存路径
			File cacheDir = getDiskCacheDir(context, "thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache
					.open(cacheDir, getAppVersion(context), 1, 20 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static SLoadImage getInstance() {
		if (instance == null) {
			instance = new SLoadImage();
		}
		return instance;
	}
	/**
	 * 
	 * @param imageview
	 * @param url
	 * @param width
	 * @param height
	 * @param type  图片大小版本small-200 medium-500 large-800
	 */
	public void loadImage(ImageView imageview,String url,int width,int height,int type) {
		String urlx = null;
		switch(type){
		case 0:
			if (!url.contains("http"))
				urlx = host + url + "!small";
			else
				urlx = url;
			break;
		case 1:
			if (!url.contains("http"))
				urlx = host + url + "!medium";
			else
				urlx = url;
			break;
		case 2:
			if (!url.contains("http"))
				urlx = host + url + "!large";
			else
				urlx = url;
			break;
		}
		imageview.setImageResource(R.color.basecolor);
		loadBitmaps(imageview, urlx,String.valueOf(width));
	}
	
	/**同上,加入检查tag参数*/
	public void loadImage(ImageView imageview,String url,int width,int height,int type,boolean checkImageTag) {
		String urlx = null;
		switch(type){
		case 0:
			if (!url.contains("http"))
				urlx = host + url + "!small";
			else
				urlx = url;
			break;
		case 1:
			if (!url.contains("http"))
				urlx = host + url + "!medium";
			else
				urlx = url;
			break;
		case 2:
			if (!url.contains("http"))
				urlx = host + url + "!large";
			else
				urlx = url;
			break;
		}
		imageview.setImageResource(R.color.basecolor);
		loadBitmaps(imageview, urlx,String.valueOf(width),checkImageTag);
	}
	
	public void loadImage(ImageView imageview,String url,int width,int height,boolean completeurl) {
		url = url;
//		imageview.setImageResource(R.drawable.empty_photo);
		imageview.setImageResource(R.color.basecolor);
		loadBitmaps(imageview, url,String.valueOf(width));
	}
	
	public void loadImage(ImageView imageview,String url,int width,int height,PhotoViewAttacher mAttacher) {
		String urlx = host + url + "!large";
//		imageview.setImageResource(R.drawable.empty_photo);
		imageview.setImageResource(android.R.color.black);
		loadBitmaps(imageview, urlx,String.valueOf(width),mAttacher);
	}
	
	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl,String width) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl + width);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				taskCollection.add(task);
				task.execute(imageUrl,width);
			} else {
				LogUtil.v("SLoadImage info: ", "image is in memoryCache!");
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**同上，加入checkimagetag参数*/
	public void loadBitmaps(ImageView imageView, String imageUrl,String width,boolean checkImageTag) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl + width);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask(imageView,checkImageTag);
				taskCollection.add(task);
				task.execute(imageUrl,width);
			} else {
				LogUtil.v("SLoadImage info: ", "image is in memoryCache!");
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadBitmaps(ImageView imageView, String imageUrl,String width,PhotoViewAttacher mAttacher) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl + width);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask(imageView,mAttacher);
				taskCollection.add(task);
				task.execute(imageUrl,width);
			} else {
				LogUtil.v("SLoadImage info: ", "image is in memoryCache!");
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
					mAttacher.update();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}
	
	public void delBitmapFromMemoryCache(String keynohost) {
		keynohost = host + keynohost;
		LogUtil.v("SLoadImage info: ", "read to recycle key: " + keynohost);
		if (getBitmapFromMemoryCache(keynohost) != null) {		
			 Bitmap bitmap = mMemoryCache.get(keynohost);
			 if (bitmap != null ) {
				 LogUtil.v("SLoadImage info: ", "bitmap is recycled!");
				 bitmap.recycle();
				 bitmap = null;
			 }
			
		}
		mMemoryCache.remove(keynohost);		
	}	
	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}	
	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}
	/**
	 * 获取当前应用程序的版本号。
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}	
	/**
	 * 使用MD5算法对传入的key进行加密并返回。
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}	
	/**
	 * 将缓存记录同步到journal文件中。
	 */
	public void fluchCache() {
		if (mDiskLruCache != null) {
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}	
	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;
		/**
		 * 目标宽度
		 */
		private int width;
		/**
		 * 目标imageview
		 */
		private ImageView imageview;
		/**
		 * imageviewer
		 * @param image
		 */
		private PhotoViewAttacher mAttacher;
		/**控制显示时是否检查tag的一致性*/
		private boolean checkImageTag = false;
		
		public BitmapWorkerTask(ImageView image) {
			imageview = image;
		}
		
		public BitmapWorkerTask(ImageView image,PhotoViewAttacher attcher) {
			imageview = image;
			mAttacher = attcher;
		}
		
		public BitmapWorkerTask(ImageView image,boolean checkImageTag) {
			imageview = image;
			this.checkImageTag = checkImageTag;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			width = Integer.parseInt(params[1]);
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				// 生成图片URL对应的key
				final String key = hashKeyForDisk(imageUrl);
				// 查找key对应的缓存
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					LogUtil.v("SLoadImage info: ", "diskCache is null ,"+imageUrl+" to be download!");
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					LogUtil.v("SLoadImage info: ", "diskCache had written!");
					fileInputStream = (FileInputStream) snapShot.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitmap对象
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
				//	bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
					bitmap = BitmapUtil.decodeSampledBitmapFromFileDescriptor(fileDescriptor, width, width);
				}
				if (bitmap != null) {
					// 将Bitmap对象添加到内存缓存当中
//					LogUtil.v("SLoadImage info: ", "to added to memoryCache key: "+params[0] + params[1]);
					addBitmapToMemoryCache(params[0] + params[1], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if (imageview != null && bitmap != null) {
				if (this.checkImageTag) {
					if (this.imageUrl.contains((String)imageview.getTag()))
						imageview.setImageBitmap(bitmap);
				} else
					imageview.setImageBitmap(bitmap);
				
				if (this.mAttacher != null ) {
					mAttacher.update();
				}
				mAttacher = null;
				imageview = null;
			}
			taskCollection.remove(this);
		}

		/**
		 * 建立HTTP请求，并获取Bitmap对象。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
			HttpURLConnection urlConnection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
				out = new BufferedOutputStream(outputStream, 8 * 1024);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

	}
}

