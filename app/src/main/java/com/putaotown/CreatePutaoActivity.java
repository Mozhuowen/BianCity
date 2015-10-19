package com.putaotown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.putaotown.community.SelectPicActivity;
import com.putaotown.fragment.DrafExitWithoutSavedDailogFragent;
import com.putaotown.localio.DraftHelper;
import com.putaotown.localio.FileIO;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.markdown.MDWriter;
import com.putaotown.markdown.PreViewActivity;
import com.putaotown.net.PutaoRequest;
import com.putaotown.net.objects.Image;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.CharacterUtil;
import com.putaotown.tools.DensityUtil;
import com.putaotown.tools.LogUtil;
import com.putaotown.views.BCEditText;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreatePutaoActivity extends SelectPicActivity implements OnItemSelectedListener/* implements OnClickListener*/
{
	/**从草稿箱进入*/
	public static void startAction(Context context,ContentValues values) {
		Intent intent = new Intent(context,CreatePutaoActivity.class);
		//set view data
		cover = values.getAsString("cover");
		title = values.getAsString("title");
		content = values.getAsString("content");
		misDraft = true;
		dbRandomCode = values.getAsString("randomcode");
		draftimages = values.getAsString("images");
		townid = values.getAsInteger("townid");
		context.startActivity(intent);
	}
	private LayoutInflater layoutInflater;
//	private LinearLayout coverlayout;
	private LinearLayout imagelayout;
	//	private LinearLayout imagelayout1;
//	private LinearLayout imagelayout2;
//	private TextView imagetext; //set on parent
	private EditText titletext;
	private BCEditText contexttext;
	private View Draft;
	private ScrollView rootSrollView;
	private List<String> images = new ArrayList<String>();
	//	private ArrayList<Image> imagenames = new ArrayList<Image>(); //set on parent
	private final int CROP_PIC = 200;
	private final int TAKE_PIC = 300;
	private final int CROP_COVER = 400;
	private int dividerwidth = 0;
	private int screenWidth = 0;
	private DisplayMetrics  dm = new DisplayMetrics();	//屏幕分辨率
	private Builder builder;
	private Dialog dialog;
	private Dialog dialog2;
	//	private Uri imageUri;	//set on parent
	private String coverpath;
	private String image;
	private String imagepath;
	private String IMAGE_FILE_LOCATION;//temp file
	private Handler messhandler;
	boolean iscover = false;	//用于判断选择照片是cover还是images
	private static int townid = 0;
	
	/**上传对话框的进度条*/
	ProgressBar progressbar;
	//draft 参数
	private static String containimages;
	private static String title;
	private static String content;
	private static String draftimages;
	private SQLiteDatabase db;		//数据库
	private static String dbRandomCode;	//数据库中的随机码
	private boolean hasSaveToDraft = false;
	private long mLastBacktime = 0;	//记录点击返回的时间
	
	private static boolean misDraft = false;	//记录是否显示的是草稿
	private Spinner mSpinner;
	private List<SpinnerTowns> mSpinnerTowns;
	private View mOk;
	private PutaoRequest mCurrRequestTask;
	
	//mardown
	private MDWriter mMDWriter;
	
	public static void startAction(Context context,int townid,int requestCode) {
		Intent intent = new Intent(context,CreatePutaoActivity.class);
		intent.putExtra("townid", townid);
		misDraft = false;
		((Activity)context).startActivityForResult(intent, requestCode);
	}
	public static void startAction(Context context,int townid) {
		misDraft = false;
		context.startActivity(new Intent(context, CreatePutaoActivity.class));
	}
	
	private SystemBarTintManager mTintManager;
	
	/**
	 * 弹出上传进度条对话框
	 */
	public void alerLoading() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_progressbar, null);
		progressbar = (ProgressBar)view.findViewById(R.id.dialog_progressbar_bar);
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(false);
		dialog2.show();
	}
	
	/**弹出保存草稿对话框*/
	public void alertDraftDialog() {
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_draft, null);
		View savedraft = view.findViewById(R.id.dialog_draft_save);
		View exit = view.findViewById(R.id.dialog_draft_exit);
		savedraft.setOnClickListener(this);
		exit.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();
	}
	/**
	 * 返回事件，可直接在标签绑定
	 */
	public void backEvent(View source){
		if (!this.hasSaveToDraft)
			this.alertDraftDialog();
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
	    if (iscover) {
			intent.putExtra("aspectX", 5);
			intent.putExtra("aspectY", 3);
			intent.putExtra("outputX", 800);
			intent.putExtra("outputY", 480);
		}
//	    intent.putExtra("aspectX", 5);
//	    intent.putExtra("aspectY", 3);
//	    intent.putExtra("outputX", outputX);
//	    intent.putExtra("outputY", outputY);
	    intent.putExtra("scale", true);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    intent.putExtra("return-data", false);
	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("noFaceDetection", true); // no face detection
	    
	    startActivityForResult(intent, requestCode);
		}
	/**清理工作*/
	protected void finishThing() {
		if (this.dialog != null)
			this.dialog.dismiss();
		if (this.dialog2 != null)
			this.dialog2.dismiss();
	}
	public void initDB() {
		db = new DraftHelper(this,"Draft.db",null,1).getWritableDatabase();
		if (this.dbRandomCode == null )
			dbRandomCode = CharacterUtil.getRandomString(6);
	}
	public void initViews() {
		if (townid == 0)
			this.townid = this.getIntent().getIntExtra("townid", 0);
		getWindowManager().getDefaultDisplay().getMetrics(dm);     	   
		screenWidth = dm.widthPixels;
		dividerwidth = (screenWidth-(DensityUtil.dip2px(this, 80)*4))/5;
		layoutInflater = LayoutInflater.from(this);
//		imagelayout = (LinearLayout)findViewById(R.id.activity_create_putao_images); //onCreate()中已设置
		coverlayout = (LinearLayout)findViewById(R.id.activity_create_putao_cover);
		coverlayout.setOnClickListener(new SelectPicActivity.SelectCover(coverlayout,0));
		imagetext = (TextView)findViewById(R.id.activity_createputao_imagetext);
		titletext = (EditText)findViewById(R.id.activity_createputao_title);
		contexttext = (BCEditText)findViewById(R.id.activity_createputao_content);
		Draft = findViewById(R.id.activity_createputao_draft);
		this.rootSrollView = (ScrollView) findViewById(R.id.activity_create_putao_rootscrollview);
		//markdown
		mMDWriter = new MDWriter((EditText)findViewById(R.id.activity_createputao_content));
		this.contexttext.setOutSideView(rootSrollView);
//		this.contexttext.setOnTouchListener(new OnTouchListener(){
//			@Override
//			public boolean onTouch(View v, MotionEvent event ) {
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					rootSrollView.requestDisallowInterceptTouchEvent(false);
//				} else {
//					rootSrollView.requestDisallowInterceptTouchEvent(true);
//				}
//				return false;
//			}
//			
//		});
		
//		imagelayout1 = new LinearLayout(this);
//		imagelayout2 = new LinearLayout(this);
//		imagelayout2.setPadding(0, dividerwidth, 0, 0);
		
//		View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
//		LinearLayout tmplayout = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
//		tmplayout.setPadding(dividerwidth, 0, 0, 0);
//		addimage.setOnClickListener(this);
//		imagelayout1.addView(addimage);
//		imagelayout.addView(imagelayout1);
//		imagelayout.addView(imagelayout2);
		
//		IMAGE_FILE_LOCATION = this.getExternalCacheDir()+"/"+"temp.jpg";//temp file
//		File file = new File(IMAGE_FILE_LOCATION);
//		try {
//			file.createNewFile();
//		} catch (IOException e) {e.printStackTrace();}
//		imageUri = Uri.fromFile(file);
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
					onNetworkFail();
					Toast.makeText(CreatePutaoActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
					onNetworkFail();
				}else if (msg.what == 400){	//更新进度条
					int progress = (int)msg.arg1;
					progressbar.setProgress(progress);
				}  else {
					onNetworkFail();
					String mess = (String)msg.obj;
					Toast.makeText(CreatePutaoActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		//初始化弹出的选择照片对话框
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_createtown, null);
		View takepic = view.findViewById(R.id.dialog_takepic);
		View album = view.findViewById(R.id.dialog_album);
		takepic.setOnClickListener(this);
		album.setOnClickListener(this);
		
		builder.setView(view);
		
//		builder.setView(view);
		this.dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		
		//保存草稿事件
		this.Draft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				saveDraft();
				Toast.makeText(CreatePutaoActivity.this, "保存成功Y(^_^)Y", Toast.LENGTH_SHORT).show();
			}			
		});
		//设置actionbar
		this.setActionBar();
	}
	/**点击ok*/
	public void ok(View v) {
		//获取eidttext
		String title = this.titletext.getText().toString();
		String content = this.contexttext.getText().toString();
		if (title != null && title.length() > 0 && content != null && content.length() > 0) {
			this.saveDraft();
			alerLoading();
			mCurrRequestTask = new PutaoRequest(this, messhandler,this.progressbar,townid, title, content, cover, imagenames);
			mCurrRequestTask.createPutao();
		} else {
			Toast.makeText(this, "请填写完整信息哦#^_^#", Toast.LENGTH_SHORT).show();
		}
	}
	/*@Override*/
	public void onActivityResult_1(int requestCode,int resultCode ,Intent intent) {
		switch(requestCode) {
		case CROP_COVER:
			if(imageUri != null && intent != null) {
				this.cover = CharacterUtil.getRandomString(32);
				coverpath = this.getFilesDir() + "/image/" + cover;
				try {
					File file = new File(coverpath);
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
				//复制图片至目标路径
				FileIO.copyByUri(this,coverpath,imageUri);		
	
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
		case CROP_PIC:
			if(resultCode == RESULT_OK && imageUri != null && intent != null){
				this.image = CharacterUtil.getRandomString(32);
				imagepath = this.getFilesDir() + "/image/" + image;
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
			    View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
			    targetimage.setTag(""+(imagenames.size()-1));
				LinearLayout tmplayout1 = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
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
			break;
		case TAKE_PIC:
			if (resultCode == RESULT_OK) {
				if (!iscover)
					cropImageUri(imageUri, 800, 480, CROP_PIC);
				else 
					cropImageUri(imageUri, 800, 480, CROP_COVER);
			}
			break;
		}
	}
	/*@Override*/
	public void onClick_1(View v) {	
		//启动获取照片
		switch(v.getId()) {
		case R.id.activity_create_putao_cover:	//选择封面
			dialog.dismiss();
			iscover = true;		
			dialog.show();
			break;
		case R.id.putao_addimage_layout:	//点击添加图片按钮
//			dialog.dismiss();
//			iscover = false;
//			dialog.show();	
			//test code
//			Intent intentx = new Intent(this, PhotoSelectorActivity.class);
//			intentx.putExtra(PhotoSelectorActivity.KEY_MAX, 2);
//			intentx.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//			this.startActivityForResult(intentx, 900);
			break;
		case R.id.dialog_album:	//从相册选择并裁剪照片
			dialog.dismiss();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "false");
			if (iscover) {
				intent.putExtra("aspectX", 5);
				intent.putExtra("aspectY", 3);
				intent.putExtra("outputX", 800);
				intent.putExtra("outputY", 480);
			}
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true);			
			if (iscover)             
				startActivityForResult(intent, CROP_COVER);
			else
				startActivityForResult(intent, CROP_PIC);
			break;
		case R.id.dialog_takepic:
			dialog.dismiss();
			Intent intent_takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
			intent_takepic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent_takepic, TAKE_PIC);
			break;
		case R.id.dialog_draft_exit:
			this.dialog2.dismiss();
			this.finish();
			break;
		case R.id.dialog_draft_save:
			Toast.makeText(this, "保存成功Y(^_^)Y", Toast.LENGTH_SHORT).show();
			this.saveDraft();
			this.dialog2.dismiss();
			this.finish();
			break;
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_putao);
		
  		//判断是否被强制关闭
  		if (savedInstanceState != null ) {
  			misDraft = true;
  			cover = (String)savedInstanceState.getSerializable("cover");
  			title = (String)savedInstanceState.getSerializable("title");
  			content = (String)savedInstanceState.getSerializable("content"); 			
  			draftimages = (String)savedInstanceState.getSerializable("images");
  			dbRandomCode = (String)savedInstanceState.getSerializable("dbRandomCode");
  			townid = (Integer)savedInstanceState.getSerializable("townid");
  			super.cover = (String)savedInstanceState.getSerializable("cover");
  			
  		} else {	//从边城进入使用intent传递数据
  			Intent intent = this.getIntent();
  			if (intent != null && intent.getSerializableExtra("townid") != null){
  				int tid = (Integer)intent.getSerializableExtra("townid");
	  			if (tid != 0)
	  				townid = tid;
  			}
  		}
  		//super param
  		context = this;
  		selectImageLayout = (LinearLayout)findViewById(R.id.activity_create_putao_images);
  		super.setImageLayout();
  		
		initDB();
		initViews();
		if (misDraft)
        	this.setDraftView();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCurrRequestTask != null)
			mCurrRequestTask.cancelTask();
	}
	public void onFinishNetwork(List<PackagePutao> putao) {
		dialog2.dismiss();
		Intent intent = new Intent();
		intent.putExtra("putao", (Serializable)putao);
		this.setResult(RESULT_OK,intent);
		updateDraftFinish();
		finish();
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

	public void onNetworkFail() {
		dialog2.dismiss();
	}
	/**点击actionbar*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:
			if (!this.hasSaveToDraft) {
				FragmentManager fm = this.getSupportFragmentManager();
				DrafExitWithoutSavedDailogFragent dialog = new DrafExitWithoutSavedDailogFragent(this);
				dialog.show(fm, "退出");
				return true;
			} else {
				finish();
			}
	        default:
	        	return true;
	    }  
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
//		Toast.makeText(this, "is going to saveInstanceState!", Toast.LENGTH_LONG).show();
		LogUtil.v("CreatePutaoActivity info: ", "onSaveInstanceState!");
		savedInstanceState.putSerializable("cover", cover);
		savedInstanceState.putSerializable("title", title);
		savedInstanceState.putSerializable("content", content);
		savedInstanceState.putSerializable("dbRandomCode", dbRandomCode);
		savedInstanceState.putSerializable("townid", townid);
		savedInstanceState.putSerializable("cover", super.cover);
		StringBuffer strbuff = new StringBuffer();
		for (Image i:imagenames){
			strbuff.append(i.getImagename());
			strbuff.append(",");
		}
		savedInstanceState.putSerializable("draftimages", strbuff.toString());
//		cover = values.getAsString("cover");
//		title = values.getAsString("title");
//		content = values.getAsString("content");
//		misDraft = true;
//		dbRandomCode = values.getAsString("randomcode");
//		draftimages = values.getAsString("images");
//		townid = values.getAsInteger("townid");
	}
	
	/**保存到草稿*/
	public void saveDraft() {
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("randomcode", this.dbRandomCode);
		dbvalues.put("type", 1);
		dbvalues.put("title", titletext.getText().toString());
		dbvalues.put("cover", cover);
		dbvalues.put("content", contexttext.getText().toString());
		dbvalues.put("isfinish", 0);
		dbvalues.put("townid", townid);
		//get and set iamges
		StringBuffer strbuf = new StringBuffer();
		for (Image img: imagenames) {
			strbuf.append(img.getImagename());
			strbuf.append(",");
		}
		dbvalues.put("images", strbuf.toString());
		
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
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_create_story, null);
		actionbar.setCustomView(v);
		this.mSpinner = (Spinner) v.findViewById(R.id.actionbar_spinner);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				setSpinner();
			}
		}, 200);
		mOk = v.findViewById(R.id.actionbar_ok);
		mOk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ok(null);
			}
			
		});
	}
	/**设置spinner,run in delaypost*/
	public void setSpinner() {
		this.mSpinnerTowns = UserPreUtil.getSpinnerTowns();
		List<String> array = new ArrayList<String>();
		for (SpinnerTowns town:mSpinnerTowns) {
			array.add(town.getTowname());
		}
		// Create an ArrayAdapter using the string array and a default spinner layout
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//		        R.array.planets_array, android.R.layout.simple_spinner_item);
		ArrayAdapter<List> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,array);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);
		//if enter from draf then set selection
		if (townid > 0)
			mSpinner.setSelection(this.getTownPosition(townid));
	}
	/**从草稿中设置view*/
	public void setDraftView() {
		this.titletext.setText(title);
		this.contexttext.setText(content);
		coverpath = this.getFilesDir() + "/image/" + cover;
		Bitmap coverbitmap = FileIO.getBitmapFromPath(this, coverpath, 500);
	    ImageView imageview = new ImageView(this);
		imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		imageview.setImageBitmap(coverbitmap);
		imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
	
		imagetext.setVisibility(View.GONE);
		coverlayout.removeAllViews();
		coverlayout.addView(imageview);
		
		//set images buttom
		if (draftimages != null && draftimages.length() > 0 ){
			String[] names = draftimages.split(",");
			for (String str:names) {
				/*String path = this.getFilesDir() + "/image/" + str;
				Image thisimage = new Image(str,FileIO.getMd5(path),FileIO.getFileSize(path),imagenames.size());
				//记录图片名
				imagenames.add(thisimage);
				//避免将全图放置到view以防超内存
				Bitmap bitmap = FileIO.getBitmapFromPath(this, path, 90);
			    
			    View addimage = layoutInflater.inflate(R.layout.view_putao_addimage, null);
			    View targetimage = layoutInflater.inflate(R.layout.view_putao_image, null);
			    targetimage.setTag(""+(imagenames.size()-1));
				LinearLayout tmplayout1 = (LinearLayout)addimage.findViewById(R.id.putao_addimage_layout);
				FrameLayout tmplayout2 = (FrameLayout)targetimage.findViewById(R.id.putao_layout);
				ImageView imagetmp = (ImageView)targetimage.findViewById(R.id.putao_image);
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
				}*/
				String path = this.getFilesDir() + "/image/" + str;
				super.addOneImage(path);
			}
		}				
	}
	/**更新草稿状态为已完成*/
	public void updateDraftFinish() {
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("isfinish", 1);
		this.db.update("Draft", dbvalues, "randomcode=?", new String[]{this.dbRandomCode});
	}
	/**Spinner中的Town对象*/
	public static class SpinnerTowns
	{
		private int townid;
		private String towname;
		public int getTownid() {
			return townid;
		}
		public void setTownid(int townid) {
			this.townid = townid;
		}
		public String getTowname() {
			return towname;
		}
		public void setTowname(String towname) {
			this.towname = towname;
		}
	}
	/**根据townid返回Spinner中的position*/
	public int getTownPosition(int townid) {
		for (int i=0;i<this.mSpinnerTowns.size();i++) {
			if (this.mSpinnerTowns.get(i).townid == townid)
				return i;
		}
		return 0;
	}
	/**Spinner listener method*/
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
//		Toast.makeText(this, ""+pos+" "+id, Toast.LENGTH_SHORT).show();
		townid = this.mSpinnerTowns.get(pos).getTownid();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	/**mardown按钮监听*/
	public void onClickHeader(View v) {
		LogUtil.v("CreatePutaoActivity info: ", "onClickHeader");
        mMDWriter.setAsHeader();
    }
    
    public void onClickCenter(View v) {
        mMDWriter.setAsCenter();
    }
    
    public void onClickList(View v) {
        mMDWriter.setAsList();
    }
    
    public void onClickBold(View v) {
        mMDWriter.setAsBold();
    }
    
    public void onClickQuote(View v) {
        mMDWriter.setAsQuote();
    }
    public void onClickPreView(View v) {
    	String content = this.contexttext.getText().toString();
    	PreViewActivity.startAction(this, content);
    }
		
}
