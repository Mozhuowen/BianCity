package com.putaotown;

import java.io.File;
import java.io.IOException;

import com.putaotown.fragment.MineFragmentx;
import com.putaotown.localio.FileIO;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.CUCoverUtil;
import com.putaotown.net.CWallUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.net.UserUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.objects.ImgRecy;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.CharacterUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserActivity extends AppCompatActivity implements UserInterface,OnClickListener
{
	private String mSexValue = "m";
	private ImageView mCover;
	private EditText mUsername;
	private EditText mLocation;
	private View mCCover;
	private View mSexLayout;
	private TextView mSex;
	/**弹出选择对话框*/
	Dialog dialog2;
	private Uri imageUri = null;
	/**事件判断*/
	public final int CROP_PIC = 200;
	public final int TAKE_PIC = 300;
	/**上传对话框的进度条*/
	ProgressBar progressbar;	
	private Builder builder;
	private String imagepath;
	private String IMAGE_FILE_LOCATION;//temp file
	private String mCoverName;
	private static CUCoverUtil mCurrReq;
	private Handler messhandler;
	
	@Override
	public void onStart() {
		super.onStart();
		loadUserCover();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edituser);
        initViews();
        this.setActionBar();
	}
	
	public void ok(View v) {
		String newname = this.mUsername.getText().toString();
		String newlocation = this.mLocation.getText().toString();
		if (newname!=null && newname.length() > 0 && newlocation!=null && newlocation.length()>0) {
//			this.alerLoading(false);
			ModelUser req = new ModelUser();
			req.setName(newname);
			req.setLocation(newlocation);
			req.setSex(this.mSexValue);
			UserUtil.CUserInfo(this, req);
		}
	}
	
	public void initViews() {
		this.mCover = (ImageView)findViewById(R.id.activity_edituser_usercover);
		this.mUsername = (EditText)findViewById(R.id.activity_edituser_username);
		this.mLocation = (EditText)findViewById(R.id.activity_edituser_locationtext);
		this.mCCover = findViewById(R.id.activity_edituser_ccover);
		this.mSexLayout = findViewById(R.id.activity_edituser_usersex);
		this.mSex = (TextView)findViewById(R.id.activity_edituser_sextext);
		
		//set data
		this.mUsername.setText(UserPreUtil.getUsername());
		this.mLocation.setText(UserPreUtil.getLocation());		
		this.mCCover.setOnClickListener(this);
		this.mSexLayout.setOnClickListener(this);
		if (UserPreUtil.getSex().equals("m"))
			this.mSex.setText("男");
		else
			this.mSex.setText("女");
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(EditUserActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else if (msg.what == 400){	//更新进度条
					int progress = (int)msg.arg1;
					progressbar.setProgress(progress);
				} else {
					onNetworkFail();
					String mess = (String)msg.obj;
					Toast.makeText(EditUserActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,EditUserActivity.class);
		context.startActivity(intent);
	}
	
	public void backEvent(View source){
		finish();
	}
	
	/**加载用户头像*/
	private void loadUserCover() {
		String cover = UserPreUtil.getCover();
		BianImageLoader.getInstance().loadImage(mCover, cover,90);
//		if (cover.contains("http")) {
////			SLoadImage.getInstance().loadImage(this.mCover, cover, 90, 90,true);	
//			BianImageLoader.getInstance().loadImage(mCover, cover,90);
//		} else {
//			ImgRecy ir = new ImgRecy(cover,90);
//			SLoadImage.getInstance().loadImage(this.mCover, cover, ir.size,ir.size,ir.irank);
//		}
			
	}

	@Override
	public void onNetWorkFinish(ModelUser user) {
		UserPreUtil.updateName(this.mUsername.getText().toString());
		UserPreUtil.updateLocation(this.mLocation.getText().toString());
		UserPreUtil.updateSex(this.mSexValue);
		Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
		this.finish();
	}
	
	public void onCUCover(boolean b) {
		if (b) {
			this.dialog2.dismiss();
			UserPreUtil.updateCover(mCoverName);
			loadUserCover();
			Toast.makeText(this, "修改头像成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**响应点击墙纸,，弹出对话框等*/
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.dialog_album:	//从相册选择并裁剪照片
			Log.d("CreateTownActivity", "test on click!");
			dialog2.dismiss();
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 800);
			intent.putExtra("outputY", 800);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true);			
			             
	        startActivityForResult(intent, CROP_PIC);  
	        break;
		case R.id.dialog_takepic:
			dialog2.dismiss();
			Intent intent_takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
			intent_takepic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent_takepic, TAKE_PIC);
			break;
		case R.id.activity_edituser_ccover:	//打开选择相片方式对话框
			//临时存储照片路径uri
			IMAGE_FILE_LOCATION = this.getExternalCacheDir()+"/"+"temp.jpg";//temp file
			File file = new File(IMAGE_FILE_LOCATION);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("File dir is: ", IMAGE_FILE_LOCATION);
			imageUri = Uri.fromFile(file);
			
			builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_createtown, null);
			View takepic = view.findViewById(R.id.dialog_takepic);
			View album = view.findViewById(R.id.dialog_album);
			takepic.setOnClickListener(this);
			album.setOnClickListener(this);
			
			builder.setView(view);
			dialog2 = builder.create();
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();	
			break;
		case R.id.activity_edituser_usersex:	//选择性别
			builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
			View viewsex = LayoutInflater.from(this).inflate(R.layout.dialog_sex, null);
			View male = viewsex.findViewById(R.id.dialog_sex_male);
			View female = viewsex.findViewById(R.id.dialog_sex_female);
			male.setOnClickListener(this);
			female.setOnClickListener(this);
			
			builder.setView(viewsex);
			dialog2 = builder.create();
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();
			break;
		case R.id.dialog_sex_male:
			this.mSexValue = "m";
			dialog2.dismiss();
			this.mSex.setText("男");
			this.mSexValue = "m";
			break;
		case R.id.dialog_sex_female:
			this.mSexValue = "f";
			dialog2.dismiss();
			this.mSex.setText("女");
			this.mSexValue = "f";
			break;
		}
	}
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_PIC:
			if(imageUri != null && intent != null){
				this.mCoverName = CharacterUtil.getRandomString(32);
				this.imagepath = this.getFilesDir() + "/image/" + mCoverName;
				try {
					File file = new File(imagepath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				//复制图片至目标路径
				FileIO.copyByUri(this,imagepath,imageUri);
				
			    Bitmap bitmap = FileIO.decodeUriAsBitmap(this,imageUri);//decode bitmap
			    this.mCover.setImageBitmap(bitmap);
			    alerLoading(true);
			    mCurrReq = new CUCoverUtil(this,this.progressbar,messhandler,this.mCoverName);
			    mCurrReq.doaction();
			}
			break;
		case TAKE_PIC:
			cropImageUri(imageUri, 800, 480, CROP_PIC);
			break;
		}
	}
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(uri, "image/*");
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 1);
	    intent.putExtra("aspectY", 1);
	    intent.putExtra("outputX", 800);
	    intent.putExtra("outputY", 800);
	    intent.putExtra("scale", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    intent.putExtra("return-data", false);
	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
	    startActivityForResult(intent, CROP_PIC);
	}
	
	public void alerLoading(boolean isccover) {
		if (isccover) {
			Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_progressbar, null);
			progressbar = (ProgressBar)view.findViewById(R.id.dialog_progressbar_bar);
			builder.setView(view);
			builder.setOnKeyListener(new OnKeyListener(){
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK  
	                        && event.getRepeatCount() == 0) {  
						mCurrReq.cancelTask();  
	                }  
					return false;
				}			
			});
			dialog2 = builder.create();
			dialog2.setCanceledOnTouchOutside(false);
			dialog2.show();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
				     "正在处理", Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   LinearLayout toastView = (LinearLayout) toast.getView();
				   ImageView imageCodeProject = new ImageView(getApplicationContext());
				   imageCodeProject.setImageResource(R.drawable.loading_gif);
				   toastView.addView(imageCodeProject, 0);
				   toast.show();
		}
	}
	
	public void onNetworkFail() {
		this.dialog2.dismiss();
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        finish(); 
	        return true; 
	        default:
	        	return true;
	    }  
	}
}