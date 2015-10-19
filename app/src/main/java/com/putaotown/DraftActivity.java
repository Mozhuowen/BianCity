package com.putaotown;

import java.util.ArrayList;
import java.util.List;

import com.putaotown.localio.DraftHelper;
import com.putaotown.localio.FileIO;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import me.maxwin.view.XListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class DraftActivity extends AppCompatActivity implements OnClickListener
{
	private LayoutInflater mLayoutInflater;
	private XListView mXList;
	private TextView mTitle;
	private List<View> mAdapterList = new ArrayList<View>();
	private MyListAdapter mAdapter;
	private Dialog dialog2;
	private Builder builder;
	private SQLiteDatabase db;		//数据库
	
	private static String mCurrToDelcode;
	private SystemBarTintManager mTintManager;
	
	@Override
	public void onStart() {
		super.onStart();
		
		getDraft();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_draft);
		//设置状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//		{
//			Window win = getWindow();
//			WindowManager.LayoutParams winParams = win.getAttributes();
//			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//			winParams.flags |= bits;
//			win.setAttributes(winParams);
//		}
//  		mTintManager = new SystemBarTintManager(this);
//  		mTintManager.setStatusBarTintEnabled(true);
//  		mTintManager.setNavigationBarTintEnabled(true);
//  		mTintManager.setTintColor(this.getResources().getColor(R.color.basecolor));
		
		initView();
		this.setActionBar();
	}
	
	public void initView() {
		this.mXList = (XListView)findViewById(R.id.activity_draft_xlistview);
		this.mXList.setPullLoadEnable(false);
		this.mXList.setPullRefreshEnable(false);
		
		this.mAdapter = new MyListAdapter(this,this.mAdapterList);
		this.mXList.setAdapter(mAdapter);
		
		db = new DraftHelper(this,"Draft.db",null,1).getWritableDatabase();
	}
	/**从本地数据库中获取草稿*/
	public void getDraft() {
		List<ContentValues> valuelist = FileIO.getDraftList(this);
		mAdapterList.removeAll(mAdapterList);
		for (int i = 0;i<valuelist.size();i++) {
			mAdapterList.add(new ListItemDraft(this,valuelist.get(i)).getView());
		}
		mAdapter.notifyDataSetChanged();
	}
	
	public void backEvent(View source){
		finish();
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,DraftActivity.class);
		context.startActivity(intent);
	}
	
	/**弹出确认删除对话框*/
	public void alerDeleteDialog() {
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
		View delete = view.findViewById(R.id.dialog_delete_delete);
		View cancel = view.findViewById(R.id.dialog_delete_cancel);
		delete.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();	
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.dialog_delete_delete:
			dialog2.dismiss();
			updateDraftFinish();
			getDraft();
			break;
		case R.id.dialog_delete_cancel:
			dialog2.dismiss();
			break;
		case R.id.listitem_draft_delete:
			mCurrToDelcode = (String)v.getTag();
			alerDeleteDialog();
			break;
		}
	}
	/**将草稿箱中的town设为完成，也就是不可见*/
	public void updateDraftFinish() {
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("isfinish", 1);
		LogUtil.v("DraftActivity info: ", "mCurrToDelcode "+mCurrToDelcode);
		this.db.update("Draft", dbvalues, "randomcode=?", new String[]{this.mCurrToDelcode});
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