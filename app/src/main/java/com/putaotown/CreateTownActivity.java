package com.putaotown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.basv.gifmoviewview.widget.GifMovieView;
import com.google.gson.Gson;
import com.photoselector.ui.PhotoSelectorActivity;
import com.putaotown.community.SelectPicActivity;
import com.putaotown.geographic.GeoInfo;
import com.putaotown.localio.DraftHelper;
import com.putaotown.localio.FileIO;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.TownRequest;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.tools.CharacterUtil;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateTownActivity extends SelectPicActivity /*implements OnClickListener*/
{
	private Context context;
	/**屏幕分辨率*/
	private DisplayMetrics  dm = new DisplayMetrics();
	/**屏幕宽度*/	   
	int screenWidth;               		   
	/**弹出选择相片对话框*/
	Dialog dialog2;
	/**上个activity传来的geoinfo*/
	private static Intent geoinfo_intent;
	private static GeoInfo geoinfo;
	/**resultcode*/
	private static int resultCode = RESULT_OK;
	/**views*/
	private TextView geotext;
	private EditText townname;
	private EditText descri;
	private TextView tview;	
	private View Draft;
	/**上传对话框的进度条*/
	ProgressBar progressbar;
	
	private Builder builder;
	/**事件判断*/
	private final int CROP_PIC = 200;
	private final int TAKE_PIC = 300;	
	private int SELECT_PIC = 1;	
	
	private String IMAGE_FILE_LOCATION;//temp file
	private Uri imageUri = null;
	/**cover的文件名和路径*/
//	private static String coverpath;
//	private static String cover;
	private static String mTowname;
	private static String mDescri;
	private SQLiteDatabase db;		//数据库
	private static String dbRandomCode;	//数据库中的随机码
	private boolean hasSaveToDraft = false;
	
	private Handler messhandler;
	private static TownRequest mCurrReq;
	private long mLastBacktime = 0;	//记录点击返回的时间
	private static boolean misDraft = false;	//记录是否显示的是草稿
	
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable("cover", cover);
		savedInstanceState.putSerializable("mTowname", mTowname);
		savedInstanceState.putSerializable("mDescri", mDescri);
		savedInstanceState.putSerializable("geoinfo", geoinfo);
		savedInstanceState.putSerializable("dbRandomCode", dbRandomCode);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtown);
  		
        this.context = this.getApplicationContext();
      //判断是否被强制关闭
  		if (savedInstanceState != null ) {
  			LogUtil.v("CreateTownActivity info:", "resume! from savedInstanceState!");
  			misDraft = true;
  			cover = (String)savedInstanceState.getSerializable("cover");
  			mTowname = (String)savedInstanceState.getSerializable("mTowname");
  			mDescri = (String)savedInstanceState.getSerializable("mDescri");
  			geoinfo = (GeoInfo)savedInstanceState.getSerializable("geoinfo");
  			dbRandomCode = (String)savedInstanceState.getSerializable("dbRandomCode");
  		}
        
        initDB();
        initViews();
        if (misDraft)
        	this.setDraftView();
	}
	
	public void initDB() {
		db = new DraftHelper(this,"Draft.db",null,1).getWritableDatabase();
		if (this.dbRandomCode == null )
			dbRandomCode = CharacterUtil.getRandomString(6);
	}
	
	public void initViews() {
//		geoinfo = (GeoInfo)geoinfo_intent.getSerializableExtra("geoinfo");
		
		this.geotext = (TextView)findViewById(R.id.activity_createtown_geotext);
		this.townname = (EditText)findViewById(R.id.activity_createtown_townname);
		this.descri = (EditText)findViewById(R.id.activity_createtown_descri);
		this.Draft = findViewById(R.id.activity_createtown_draft);
		//设置坐标信息
		if (geoinfo.getProvince() != null && geoinfo.getCity() != null)
			geotext.setText(geoinfo.getProvince()+geoinfo.getCity()+geoinfo.getFreeaddr());
		else
			geotext.setText(geoinfo.getFreeaddr());
		//获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
		screenWidth = dm.widthPixels;    
        
		//临时存储照片路径uri
		IMAGE_FILE_LOCATION = context.getExternalCacheDir()+"/"+"temp.jpg";//temp file
		File file = new File(IMAGE_FILE_LOCATION);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("File dir is: ", IMAGE_FILE_LOCATION);
		imageUri = Uri.fromFile(file);
        
        super.imagetext = (TextView)findViewById(R.id.activity_createtown_image);
        coverlayout = (LinearLayout)findViewById(R.id.activity_createtown_picture);
        coverlayout.setOnClickListener(new SelectPicActivity.SelectCover(coverlayout, 1));
        
        //保存草稿时间
        this.Draft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				saveDraft();
				Toast.makeText(CreateTownActivity.this, "保存成功Y(^_^)Y", Toast.LENGTH_SHORT).show();
			}       	
        });
        
        //handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(context, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				} else if (msg.what == 400){	//更新进度条
					int progress = (int)msg.arg1;
					progressbar.setProgress(progress);
				} else {
					onNetworkFail();
					String mess = (String)msg.obj;
					Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
		//set actionbar
		this.setActionBar();
	}
	
	public void ok(View view) {
		String townname = this.townname.getText().toString();
		String descri = this.descri.getText().toString();
		//检查信息不为空
		if (townname != null
				&& descri != null
				&& this.cover != null
				&& geoinfo.getScreenpng()!=null
				&& townname.length() > 0
				&& descri.length() > 0 )
		{
			this.alerLoading();
			saveDraft();
			//第四个参数已废弃
			mCurrReq = new TownRequest(this,this.progressbar,messhandler,null,descri,townname,cover,this.geoinfo);
			mCurrReq.apply();
		} else {
			Toast.makeText(this, "请填写完整信息哦#^_^#", Toast.LENGTH_SHORT).show();
		}			
	}
	
	public void setDraftView() {
		this.townname.setText(mTowname);
		this.descri.setText(mDescri);
		String coverpath = context.getFilesDir() + "/image/" + cover;
		Bitmap bitmap = FileIO.getBitmapFromPath(this, coverpath, 500);
	    ImageView imageview = new ImageView(context);
		imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		imageview.setImageBitmap(bitmap);
		imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
	
		coverlayout.removeAllViews();
		super.imagetext.setVisibility(View.GONE);
		coverlayout.addView(imageview);
		
	}
	
	public void alertDraftDialog() {
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_draft, null);
		View savedraft = view.findViewById(R.id.dialog_draft_save);
		View exit = view.findViewById(R.id.dialog_draft_exit);
		savedraft.setOnClickListener(this);
		exit.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();
	}
	/**保存内容至草稿箱*/
	public void saveDraft() {
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("randomcode", this.dbRandomCode);
		dbvalues.put("type", 0);
		dbvalues.put("title", townname.getText().toString());
		dbvalues.put("cover", cover);
		dbvalues.put("content", descri.getText().toString());
		String geo = new Gson().toJson(this.geoinfo);
		dbvalues.put("geoinfo", geo);
		dbvalues.put("isfinish", 0);
		
		LogUtil.v("CreateTownActivity info: ", "randomcode: "+this.dbRandomCode);
		//先查询数据库中该条数据有没有
		Cursor cursor = this.db.query("Draft", null, "randomcode=?", new String[]{this.dbRandomCode}, null, null, null);
		if (cursor.getCount() == 0){					
			LogUtil.v("CrateTownActivity info: ", "database has not this item!");
			this.db.insert("Draft", null, dbvalues);
		} else if (cursor.getCount() == 1) {
			this.db.update("Draft", dbvalues, "randomcode=?", new String[]{this.dbRandomCode});
		}
		cursor.close();
		this.hasSaveToDraft = true;
	}
	/**将草稿箱中的town设为完成，也就是不可见*/
	public void updateDraftFinish() {
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("isfinish", 1);
		this.db.update("Draft", dbvalues, "randomcode=?", new String[]{this.dbRandomCode});
	}
	
	/**
	 * 网络请求成功，返回前一个activity
	 * @param name
	 * @param token
	 * @param cover
	 */
	public void onFinishNetwork(ApplyTown at) {
		dialog2.dismiss();
		Toast.makeText(this, "成功创建边城", Toast.LENGTH_SHORT).show();
		UserPreUtil.updateMyTowns(at);
		ModelUser u = new ModelUser();
		u.setCover(UserPreUtil.getCover());
		u.setName(UserPreUtil.getUsername());
		u.setFans(0);
		TownActivity.startAction(this, at, u,true);
		//update draft
		updateDraftFinish();
		
		this.finish();
	}
	
	public void onNetworkFail() {
		dialog2.dismiss();
	}
	
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		//判断并提示保存到草稿
		if (this.hasSaveToDraft)
			finish();
		else {
			alertDraftDialog();
		}
	}

	/*@Override*/
	public void onClick_1(View v) {
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
//			cropImageUri(imageUri, 800, 800, CROP_PIC);
			break;
		case R.id.activity_createtown_picture:	//打开选择相片方式对话框			
			builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_createtown, null);
			View takepic = view.findViewById(R.id.dialog_takepic);
			View album = view.findViewById(R.id.dialog_album);
			takepic.setOnClickListener(this);
			album.setOnClickListener(this);
			
			builder.setView(view);
			dialog2 = builder.create();
			dialog2.setCanceledOnTouchOutside(true);
			dialog2.show();	
			break;
		case R.id.dialog_draft_exit:
			this.dialog2.dismiss();
			this.finish();
			break;
		case R.id.dialog_draft_save:
			Toast.makeText(CreateTownActivity.this, "保存成功Y(^_^)Y", Toast.LENGTH_SHORT).show();
			this.saveDraft();
			this.dialog2.dismiss();
			this.finish();
			break;
		}
		
	}
	
	/*@Override*/
	public void onActivityResult_1(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_PIC:
			if(resultCode == RESULT_OK && imageUri != null && intent != null){
				this.cover = CharacterUtil.getRandomString(32);
				String coverpath = context.getFilesDir() + "/image/" + cover;
				try {
					File file = new File(coverpath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				//复制图片至目标路径
				copyByUri(coverpath,imageUri);
				
			    Bitmap bitmap = decodeUriAsBitmap(imageUri);//decode bitmap
			    ImageView imageview = new ImageView(context);
				imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
				imageview.setImageBitmap(bitmap);
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			
				coverlayout.removeAllViews();
				tview.setVisibility(View.GONE);
				coverlayout.addView(imageview);
			}
			break;
		case TAKE_PIC:
			LogUtil.v("CreateTownActivity info: ", "onActivityResult resultCode: "+resultCode + " intent: "+intent);
			if (resultCode == RESULT_OK)
				cropImageUri(imageUri, 800, 800, CROP_PIC);
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
	        } catch (FileNotFoundException e) {

	    e.printStackTrace();
	        return null;
	        }
	    return bitmap;
	    }
	/**
	 * 弹出缓存等待对话框
	 */
	public void alerLoading() {
		Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
//		DialogBack.Builder builder = new DialogBack.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
//		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
//		final GifMovieView gif1 = (GifMovieView)view.findViewById(R.id.dialog_loading_gif);
//		gif1.setMovieResource(R.drawable.loading_big);
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
	public static void startAction(Context context,GeoInfo geoi) {
		geoinfo = geoi;
		misDraft = false;
		cover = null;
		mTowname = null;
		mDescri = null;
		dbRandomCode = null;	//static变量要设置Null;
		
		Intent intent = new Intent(context,CreateTownActivity.class);
		context.startActivity(intent);
	}
	public static void startAction(Context context,int resultcode,Intent intentx) {
		geoinfo_intent = intentx;
		resultCode = resultcode;
		Intent intent = new Intent(context,CreateTownActivity.class);
		((Activity)context).startActivityForResult(intent,600);
	}
	/**从草稿箱进入*/
	public static void startAction(Context context,ContentValues values) {
		Intent intent = new Intent(context,CreateTownActivity.class);
		//set view data
		cover = values.getAsString("cover");
		mTowname = values.getAsString("title");
		mDescri = values.getAsString("content");
		String jsonstr = values.getAsString("geoinfo");
		geoinfo = new Gson().fromJson(jsonstr, GeoInfo.class);
		misDraft = true;
		dbRandomCode = values.getAsString("randomcode");
		context.startActivity(intent);
		
	}
	/**监听返回事件*/
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {   
        	if (!this.hasSaveToDraft)
        		this.alertDraftDialog();
        	else 
        		finish();
        	return false;
        } else           
        	return false;           
    }
	/**清理工作*/
	@Override
	protected void finishThing() {
		if (!hasSaveToDraft)
			this.dialog2.dismiss();
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_okview, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		View okv = v.findViewById(R.id.actionbar_ok);
		okv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ok(null);
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	    	if (this.hasSaveToDraft)
				finish();
			else {
				alertDraftDialog();
			}
	        return true; 
	        default:
	        	return true;
	    }  
	}
}