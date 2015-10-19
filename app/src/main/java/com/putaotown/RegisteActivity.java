package com.putaotown;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.basv.gifmoviewview.widget.GifMovieView;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UserRequest;
import com.putaotown.net.UpImage;
import com.putaotown.tools.CharacterUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RegisteActivity extends Activity implements OnClickListener
{
	private final int CROP_PIC = 200;
	private final int TAKE_PIC = 300;
	
	private Context context;
	private EditText email;
	private EditText username;
	private EditText password;
	private EditText repassword;
	private ViewGroup imagelayout;
	private View choseimage;
	
	private Dialog dialog2;
	
	private Builder builder;
	
	private int screenWidth;	//屏蔽宽度
	private DisplayMetrics  dm = new DisplayMetrics();
	private String IMAGE_FILE_LOCATION;		//裁剪图片的临时temp file
	private Uri imageUri;		//用于裁剪图片
	private boolean finishcropimage = true;	//是否完成裁剪
	private String cover;
	private String localheadimage;
	/**
	 * 用于或phone的信息
	 */
	TelephonyManager tm ;
	/**
	 * IMEI number
	 */
	private String imei;
	/**
	 * 用于吐司的handler
	 */
	Handler messhandler;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registe);
        
        initViews();
	}
	
	private void initViews() {
		this.context = this.getApplicationContext();
		this.choseimage = findViewById(R.id.activity_registe_image);
		this.imagelayout = (ViewGroup)findViewById(R.id.activity_registe_picture);
		this.email = (EditText)findViewById(R.id.activity_registe_email);
		this.username = (EditText)findViewById(R.id.activity_registe_name);
		this.password = (EditText)findViewById(R.id.activity_registe_password);
		this.repassword = (EditText)findViewById(R.id.activity_registe_repassword);
		
		//获取Imei
		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
		//获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
		screenWidth = dm.widthPixels;    
        
		//临时存储照片路径uri
		IMAGE_FILE_LOCATION = context.getExternalCacheDir()+"/"+"temp_headimage.jpg";
		/*IMAGE_FILE_LOCATION = context.getCacheDir()+"/"+"temp_headimage.jpg";*/
		File file = new File(IMAGE_FILE_LOCATION);
		try {
			file.createNewFile();
		} catch (IOException e) {e.printStackTrace();}
		Log.d("File dir is: ", IMAGE_FILE_LOCATION);
		imageUri = Uri.fromFile(file);
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(context, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	/**
	 * ok完成时间
	 */
	public void ok(View source) {
		alerLoading();
		String emailstr = email.getText().toString();
		String usernamestr = username.getText().toString();
		String passwordstr = password.getText().toString();
		String repasswordstr = repassword.getText().toString();
		
		Log.d("RegisteActivity ok: ", usernamestr+" "+passwordstr+" "+emailstr+" "+this.cover+" "+this.localheadimage);

		UserRequest netrequest = new UserRequest(this,messhandler,usernamestr, passwordstr, emailstr,this.cover,this.localheadimage);
		netrequest.registe();
	
	}
	
	public void alerLoading() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
		final GifMovieView gif1 = (GifMovieView)view.findViewById(R.id.dialog_loading_gif);
		gif1.setMovieResource(R.drawable.loading_big);
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();
	}
	/**
	 * 网络请求成功，返回前一个activity
	 * @param name
	 * @param token
	 * @param cover
	 */
	public void onFinishNetwork(int userid,String name,String token,String cover,String email) {
		dialog2.dismiss();
		Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
		UserPreUtil.updateUser(userid,name, token,cover,email);	//更新preference
		
		setResult(RESULT_OK,new Intent());
		finish();
	}
	
	public void onNetworkFail() {
		dialog2.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.dialog_album:	//从相册选择并裁剪照片
			Log.d("CreateTownActivity", "test on click!");
			dialog2.dismiss();
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", screenWidth);
			intent.putExtra("outputY", screenWidth);
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
		}
	}
	
	public void onChoseImage(View v) {
		//打开选择相片方式对话框
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_createtown, null);
		View takepic = view.findViewById(R.id.dialog_takepic);
		View album = view.findViewById(R.id.dialog_album);
		takepic.setOnClickListener(this);
		album.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.show();	
	}
	/**
	 * 选择照片返回
	 */
	@Override
	public void onActivityResult(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_PIC:
			if(imageUri != null){
				this.cover = CharacterUtil.getRandomString(32);
				localheadimage = context.getFilesDir() + "/image/" + cover;
				try {
					File file = new File(localheadimage);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				
				copyByUri(localheadimage,imageUri);
				
			    Bitmap bitmap = decodeUriAsBitmap(imageUri);//decode bitmap
			    if (bitmap == null){
			    	finishcropimage = false;
			    	break;
			    }
			    ImageView imageview = new ImageView(context);
				imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
				imageview.setImageBitmap(bitmap);
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			
				choseimage.setVisibility(View.GONE);
				imagelayout.addView(imageview);
			}
			break;
		case TAKE_PIC:
			cropImageUri(imageUri, screenWidth, screenWidth, CROP_PIC);
			break;
		}
	}
	/**
	 *  将URI地址转换为bitmap图片
	 * @param uri
	 * @return
	 */
	private Bitmap decodeUriAsBitmap(Uri uri){
	    Bitmap bitmap = null;
	    try {
	    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
	        } catch (FileNotFoundException e) {e.printStackTrace();
	        return null;
	        }
	    if (bitmap==null){
	    	Log.d("Registeactivity", "bitmap is null!");
	    	Toast.makeText(context, "获取照片失败", Toast.LENGTH_SHORT).show();
	    	return null;
	    }
	    return bitmap;
	    }
	/**
	 * 根据uri复制文件
	 * @param targetpath
	 * @param uri
	 */
	private void copyByUri(String targetpath,Uri uri) {
		try {
			InputStream in = getContentResolver().openInputStream(uri);
			FileOutputStream out = new FileOutputStream(targetpath);
			byte[] bbuf = new byte[1024];
			int hasRead = 0;
			while ((hasRead = in.read(bbuf)) > 0) {
				out.write(bbuf);
			}
			in.close();
			out.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	/**
	 * 剪切照片
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(uri, "image/*");
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 1);
	    intent.putExtra("aspectY", 1);
	    intent.putExtra("outputX", outputX);
	    intent.putExtra("outputY", outputY);
	    intent.putExtra("scale", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    intent.putExtra("return-data", false);
	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
	    startActivityForResult(intent, requestCode);
		}
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context1) {
		Intent intent = new Intent(context1,RegisteActivity.class);
		((Activity)context1).startActivityForResult(intent,MainActivity.STARTACTION_REGISTE);
	}

	
}