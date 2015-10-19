package com.putaotown.community;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;
import com.putaotown.CreatePutaoActivity;
import com.putaotown.OpenImageBrower;
import com.putaotown.R;
import com.putaotown.localio.FileIO;
import com.putaotown.net.objects.Image;
import com.putaotown.tools.CharacterUtil;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPicActivity extends AppCompatActivity implements OnClickListener
{
	protected LinearLayout selectImageLayout;
	protected static View hintview;
	protected View COVERVIEW;
	protected TextView imagetext;
	protected LinearLayout coverlayout;
	protected static String cover;	//封面照片
	protected Activity context;
	private Builder builder;
	private Dialog dialog;
	private static String IMAGE_FILE_LOCATION;//temp file
	protected Uri imageUri;
	protected static LinearLayout imagelayout1;
	protected static LinearLayout imagelayout2;
	private static int dividerwidth = 0;
	private static int screenWidth = 0;
	private DisplayMetrics  dm = new DisplayMetrics();	//屏幕分辨率
	private final int CROP_PIC = 200;
	private final int TAKE_PIC = 300;
	private final int CROP_COVER = 400;
	private final int SELECT_COVER	= 500;
	private final int SELECT_NEWTOWN_COVER = 600;
	private String image;
	protected static List<String> images = new ArrayList<String>();
	protected static ArrayList<Image> imagenames;
	private static LayoutInflater layoutInflater;
	ProgressBar progressbar;
	private Dialog dialog2;
	protected Handler messhandler;
	InputMethodManager imm ;	//全局输入法控制
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
		screenWidth = dm.widthPixels;
		dividerwidth = (screenWidth-(DensityUtil.dip2px(this, 80)*4))/5;
		
		imagenames = new ArrayList<Image>();
		init();
	}
	
	public void init(){
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					dialog2.dismiss();
					Toast.makeText(context, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){

				}else if (msg.what == 400){	//更新进度条
					int progress = (int)msg.arg1;
					progressbar.setProgress(progress);
				}  else {
					String mess = (String)msg.obj;
					Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	/**初始化*/
	protected void setImageLayout() {
		layoutInflater = LayoutInflater.from(context);
		View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
		LinearLayout tmplayout = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
		imagelayout1 = new LinearLayout(this);
		imagelayout2 = new LinearLayout(this);
		imagelayout2.setPadding(0, dividerwidth, 0, 0);
		
		tmplayout.setPadding(dividerwidth, 0, 0, 0);
		addimage.setClickable(true);
		addimage.setOnClickListener(this);
		imagelayout1.addView(addimage);
		selectImageLayout.addView(imagelayout1);
		selectImageLayout.addView(imagelayout2);
		
		//初始化弹出的选择照片对话框
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_createtown, null);
		View takepic = view.findViewById(R.id.dialog_takepic);
		View album = view.findViewById(R.id.dialog_album);
		takepic.setOnClickListener(this);
		album.setOnClickListener(this);
		builder.setView(view);
		this.dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		
		IMAGE_FILE_LOCATION = this.getExternalCacheDir()+"/"+"temp.jpg";//temp file
		File file = new File(IMAGE_FILE_LOCATION);
		imageUri = Uri.fromFile(file);
		if (file.exists()) {	//do some recover
			
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	/**静态变量清理*/
	public void finishUpload() {
		if (selectImageLayout != null)
			selectImageLayout.removeAllViews();
		if (hintview != null)
			hintview.setVisibility(View.GONE);
		images = new ArrayList<String>();
		imagenames = new ArrayList<Image>();
		cover = null;
	}
	/**子类上传前需要调用*/
	public void submit() {
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘	
		alerLoading();
	}
	/**子类上传结束时需要调用*/
	public void finishSubmit() {
		dialog2.dismiss();
		finishUpload();
	}
	
	/**弹出上传进度条*/
	public void alerLoading() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
		progressbar = (ProgressBar)view.findViewById(R.id.dialog_progressbar_bar);
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//启动获取照片
		switch(v.getId()) {
		case R.id.putao_addimage_layout:	//点击添加图片按钮
			LogUtil.v("SelectPicActivity info: 	", "Pick Up Pic click!");
			Intent intentx = new Intent(this, PhotoSelectorActivity.class);
			intentx.putExtra(PhotoSelectorActivity.KEY_MAX, 8-this.imagenames.size());
			intentx.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			this.startActivityForResult(intentx, 900);
			break;
		case R.id.dialog_album:	//从相册选择并裁剪照片
			dialog.dismiss();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "false");
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true);
			startActivityForResult(intent, CROP_PIC);
			break;
		case R.id.dialog_takepic:
			dialog.dismiss();
			Intent intent_takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
			intent_takepic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent_takepic, TAKE_PIC);
			break;
		case R.id.dialog_draft_exit:
//			this.dialog2.dismiss();
			this.finish();
			break;
		case R.id.dialog_draft_save:
			Toast.makeText(this, "保存成功Y(^_^)Y", Toast.LENGTH_SHORT).show();
//			this.saveDraft();
//			this.dialog2.dismiss();
			this.finish();
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_PIC:
			if(resultCode == RESULT_OK && imageUri != null && intent != null){
				this.image = CharacterUtil.getRandomString(32);
				String imagepath = this.getFilesDir() + "/image/" + image;
				try {
					File file = new File(imagepath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				//复制图片至目标路径
				FileIO.copyByUri(this,imagepath,imageUri);	
				Image thisimage = new Image(image,FileIO.getMd5(imagepath),FileIO.getFileSize(imagepath),imagenames.size());
				//记录图片名
				imagenames.add(thisimage);
			    //Bitmap bitmap = FileIO.decodeUriAsBitmap(this,imageUri);//decode bitmap
				//避免将全图放置到view以防超内存
				Bitmap bitmap = FileIO.decodeUriAsBitmap(this, imageUri, 150);
			    
			    View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
			    View targetimage = layoutInflater.inflate(R.layout.view_select_image, null);
			    targetimage.setTag(""+(imagenames.size()-1));
				LinearLayout tmplayout1 = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				View delImage = targetimage.findViewById(R.id.del_image);
				delImage.setOnClickListener(new OnDelImage(this,thisimage));
				tmplayout1.setPadding(dividerwidth, 0, 0, 0);
				tmplayout2.setPadding(dividerwidth, 0, 0, 0);
				imagetmp.setImageBitmap(bitmap);
				addimage.setOnClickListener(this);
				if(imagenames.size()>3) {
					if(imagenames.size()==4) {
						imagelayout1.removeViewAt(3);
						imagelayout1.addView(targetimage);
						imagelayout2.addView(addimage);
					} else if (imagenames.size()==8){
						imagelayout2.removeViewAt(imagenames.size()-4-1);
						imagelayout2.addView(targetimage);
					}else {
						imagelayout2.removeViewAt(imagenames.size()-4-1);
						imagelayout2.addView(targetimage);
						imagelayout2.addView(addimage);
					}				
				} else {
					imagelayout1.removeViewAt(imagenames.size()-1);
					imagelayout1.addView(targetimage);
					imagelayout1.addView(addimage);
				}			
			}
			if (this.imagenames.size()>0) {
				if (hintview != null )
					hintview.setVisibility(View.VISIBLE);
			} else {
				if (hintview != null)
					hintview.setVisibility(View.GONE);
			}
			break;
		case TAKE_PIC:
			if (resultCode == RESULT_OK) {
//				cropImageUri(imageUri, 800, 480, CROP_PIC);
			}
			break;
		case 900:
			if (intent != null && intent.getExtras() != null) {
				List<PhotoModel> photos = (List<PhotoModel>) intent.getExtras().getSerializable("photos");
				for (PhotoModel ph:photos) {
					LogUtil.v("CreatePutaoActivity info: 	", ph.getOriginalPath());
					this.addOneImage(ph.getOriginalPath());
				}
			}
			break;
		case SELECT_COVER:
			LogUtil.v("SelectPicActivity info: ", "onActivityResult->SELECT_COVER resume!");
			if (intent != null && intent.getExtras() != null) {
				PhotoModel ph = ((List<PhotoModel>) intent.getExtras().getSerializable("photos")).get(0);
				LogUtil.v("SelectPicActivity info: ", ph.getOriginalPath());
				IMAGE_FILE_LOCATION = this.getExternalCacheDir()+"/"+"temp.jpg";//temp file
				File file = new File(IMAGE_FILE_LOCATION);
				try {
					file.createNewFile();
				} catch(IOException e) { LogUtil.v("SelectPicActivity info: ", "Create file dir fail!"); e.printStackTrace();}
				//复制图片至指定目录
//				FileIO.copyBySourcePath(tmpcoverpath, ph.getOriginalPath());
				this.imageUri = Uri.fromFile(file);
				Uri sourceuri = Uri.fromFile(new File(ph.getOriginalPath()));
				cropImageUri(sourceuri,imageUri,5,3, 800, 480, CROP_COVER);
			}
			break;
		case SELECT_NEWTOWN_COVER:
			LogUtil.v("SelectPicActivity info: ", "onActivityResult->SELECT_COVER resume!");
			if (intent != null && intent.getExtras() != null) {
				PhotoModel ph = ((List<PhotoModel>) intent.getExtras().getSerializable("photos")).get(0);
				LogUtil.v("SelectPicActivity info: ", ph.getOriginalPath());
				IMAGE_FILE_LOCATION = this.getExternalCacheDir()+"/"+"temp.jpg";//temp file
				File file = new File(IMAGE_FILE_LOCATION);
				try {
					file.createNewFile();
				} catch(IOException e) { LogUtil.v("SelectPicActivity info: ", "Create file dir fail!"); e.printStackTrace();}
				//复制图片至指定目录
//				FileIO.copyBySourcePath(tmpcoverpath, ph.getOriginalPath());
				this.imageUri = Uri.fromFile(file);
				Uri sourceuri = Uri.fromFile(new File(ph.getOriginalPath()));
				cropImageUri(sourceuri,imageUri,1,1, 800, 800, CROP_COVER);
			}
			break;
		case CROP_COVER:
			LogUtil.v("SelectPicActivity info: ", "onActivityResult->CROP_COVER resume!");
			if(imageUri != null && intent != null) {
				cover = CharacterUtil.getRandomString(32);
				String coverpath = this.getFilesDir() + "/image/" + cover;
				try {
					File file = new File(coverpath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				
				//复制图片至目标路径
				FileIO.copyByUri(this,coverpath,this.imageUri);		
	
			    Bitmap bitmap1 = FileIO.decodeUriAsBitmap(this,imageUri);//decode bitmap
			    ImageView imageview = new ImageView(this);
				imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
				imageview.setImageBitmap(bitmap1);
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imagetext.setVisibility(View.GONE);
				coverlayout.removeAllViews();
				coverlayout.addView(imageview);
			}
			break;
		}
	}
	private void cropImageUri(Uri sourceuri,Uri uri,int aspectx,int aspecty ,int outputX, int outputY, int requestCode){
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(sourceuri, "image/*");
	    intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectx);
		intent.putExtra("aspectY", aspecty);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
	    intent.putExtra("scale", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    intent.putExtra("return-data", false);
	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
	    startActivityForResult(intent, requestCode);
		}
	
	protected void addOneImage(String imagepath) {
		this.image = CharacterUtil.getRandomString(32);
		String targetpath = this.getFilesDir() + "/image/" + image;
		File file = new File(targetpath);
		try {			
			file.getParentFile().mkdir();
			file.createNewFile();
		} catch (IOException e) {e.printStackTrace();}
		Uri imageuri = Uri.fromFile(file);
		this.images.add(imageuri.toString());
		//复制图片至目标路径
//		FileIO.copyByUri(this,imagepath,imageUri);
		FileIO.copyBySourcePath(targetpath, imagepath);
		Image thisimage = new Image(image,FileIO.getMd5(targetpath),FileIO.getFileSize(targetpath),imagenames.size());
		//记录图片名
		imagenames.add(thisimage);
	    //Bitmap bitmap = FileIO.decodeUriAsBitmap(this,imageUri);//decode bitmap
		//避免将全图放置到view以防超内存
//		Bitmap bitmap = FileIO.decodeUriAsBitmap(this, imageUri, 150);
		Bitmap bitmap = FileIO.getBitmapFromPath(context, targetpath, 150);
	    
	    View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
	    View targetimage = layoutInflater.inflate(R.layout.view_select_image, null);
	    targetimage.setTag(""+(imagenames.size()-1));
		LinearLayout tmplayout1 = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
		FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
		//目标装载的imageview
		ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
		View delImage = targetimage.findViewById(R.id.del_image);
		delImage.setOnClickListener(new OnDelImage(this,thisimage));
		tmplayout1.setPadding(dividerwidth, 0, 0, 0);
		tmplayout2.setPadding(dividerwidth, 0, 0, 0);
		imagetmp.setImageBitmap(bitmap);
		//set onclick preview
		imagetmp.setOnClickListener(new OpenImageBrower(this,images.size()-1,this.images));
		addimage.setOnClickListener(this);
		if(imagenames.size()>3) {
			if(imagenames.size()==4) {
				imagelayout1.removeViewAt(3);
				imagelayout1.addView(targetimage);
				imagelayout2.addView(addimage);
			} else if (imagenames.size()==8){
				imagelayout2.removeViewAt(imagenames.size()-4-1);
				imagelayout2.addView(targetimage);
			}else {
				imagelayout2.removeViewAt(imagenames.size()-4-1);
				imagelayout2.addView(targetimage);
				imagelayout2.addView(addimage);
			}				
		} else {
			imagelayout1.removeViewAt(imagenames.size()-1);
			imagelayout1.addView(targetimage);
			imagelayout1.addView(addimage);
		}
	}
	
	public static class OnDelImage implements OnClickListener
	{
		private Image imagename;
		private SelectPicActivity context;
		public OnDelImage(SelectPicActivity context,Image i) {
			this.imagename = i;
			this.context = context;
		}		
		@Override
		public void onClick(View v) {
			for (int i=0;i<imagenames.size();i++) {
				if (imagenames.get(i).equals(imagename)) {
					imagenames.remove(imagename);
					images.remove(i);
				} 
			}
			//重构Image中的list_index
			for (int i=0;i<imagenames.size();i++) {
				imagenames.get(i).setList_index(i);
			}
			//重新加载所有图片,先全部删除掉
			imagelayout1.removeAllViews();
			imagelayout2.removeAllViews();
			View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
			LinearLayout tmplayout1 = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
			tmplayout1.setPadding(dividerwidth, 0, 0, 0);
			addimage.setOnClickListener(context);
//			imagelayout1.addView(addimage);
			LogUtil.v("SelectPicActivity info ", "imagenames size: "+imagenames.size());
			int pic_index = 0;	//用于设置打开的图片的序列
			for (Image image:imagenames) {
				String path = context.getFilesDir() + "/image/" + image.getImagename();
				//避免将全图放置到view以防超内存
				Bitmap bitmap = FileIO.getBitmapFromPath(context, path, 90);
			    
			    View targetimage = layoutInflater.inflate(R.layout.view_select_image, null);
			    targetimage.setTag(""+(imagenames.size()-1));
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
				View delImage = targetimage.findViewById(R.id.del_image);
				delImage.setOnClickListener(new OnDelImage(context,image));
				tmplayout2.setPadding(dividerwidth, 0, 0, 0);
				imagetmp.setImageBitmap(bitmap);
				imagetmp.setOnClickListener(new OpenImageBrower(context,pic_index,images));
				pic_index++;
				if (imagelayout1.getChildCount()<4)
					imagelayout1.addView(targetimage);
				else
					imagelayout2.addView(targetimage);
			}
			
			if (imagenames.size() >4 && imagenames.size() <8)
				imagelayout2.addView(addimage);
			else if (imagenames.size() == 4)
				imagelayout2.addView(addimage);
			else
				imagelayout1.addView(addimage);
			
			if (imagenames.size()>0) {
				if (hintview != null )
					hintview.setVisibility(View.VISIBLE);
			} else {
				if (hintview != null)
					hintview.setVisibility(View.GONE);
			}
		}		
	}
	
	public class SelectCover implements OnClickListener
	{
		int cropcovertype = 0;	//0-故事封面 1-边城封面
		public SelectCover(View clickView,int cropcovertype) {
			COVERVIEW = clickView;		
			this.cropcovertype = cropcovertype;
		}

		@Override
		public void onClick(View v) {
			LogUtil.v("SelectPicActivity info: 	", "Pick Up Cover Pic click!");
			Intent intentx = new Intent(SelectPicActivity.this, PhotoSelectorActivity.class);
			intentx.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
			intentx.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			if (this.cropcovertype == 0)
				SelectPicActivity.this.startActivityForResult(intentx, SELECT_COVER);
			else if (this.cropcovertype == 1)
				SelectPicActivity.this.startActivityForResult(intentx, SELECT_NEWTOWN_COVER);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//clear
		this.finishUpload();
		finishThing();
	}
	/**用于子类继承做清理工作*/
	protected void finishThing() {
		
	}
}