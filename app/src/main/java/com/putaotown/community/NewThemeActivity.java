package com.putaotown.community;

import com.putaotown.R;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.markdown.MDWriter;
import com.putaotown.markdown.PreViewActivity;
import com.putaotown.net.community.NewThemeUtil;
import com.putaotown.tools.LogUtil;
import com.putaotown.views.BCEditText;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class NewThemeActivity extends SelectPicActivity
{
	public static int communityid;
	private EditText mTitle;
	private BCEditText mContent;
	private ScrollView mRootScrollView;
	//mardown
	private MDWriter mMDWriter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_createtheme);
		
		context = this;
		selectImageLayout = (LinearLayout)findViewById(R.id.activity_community_images);
		super.setImageLayout();
		initViews();
	}
	
	private void initViews() {
		this.setActionBar();
		this.mTitle = (EditText)findViewById(R.id.activity_community_createtheme_title);
		this.mContent = (BCEditText)findViewById(R.id.activity_community_createtheme_content);
		this.mRootScrollView = (ScrollView) findViewById(R.id.activity_community_createtheme_rootscrollview);
		this.mContent.setOutSideView(mRootScrollView);
		//markdown
		mMDWriter = new MDWriter((EditText)findViewById(R.id.activity_community_createtheme_content));
	}
	/**提交上传*/
	public void ok(View v){
		String title = this.mTitle.getText().toString();
		String content = this.mContent.getText().toString();
		if (title != null && content != null && title.length()>0 && content.length()>0) {
			super.submit();
			LogUtil.v("NewTheme upload info: ", title+"　"+content+"　"+imagenames.size());
			NewThemeUtil req = new NewThemeUtil(this,messhandler,communityid,title,content,imagenames);
			req.submit();
		} else {
			Toast.makeText(this, "标题和内容都需要填写完整哦", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onFinishSubmit(boolean isok) {
		super.finishSubmit();
		if (isok){
			Toast.makeText(this, "发帖成功", Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			this.finish();
		}
	}
	
	public static void startAction(Community context,int community_id) {
		communityid = community_id;
		context.startActivityForResult(new Intent(context,NewThemeActivity.class),200);
	}
	public void backEvent(View source){
		finish();
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
	    	finish();
	        return true; 
	        default:
	        	return true;
	    }  
	}
	
	/**mardown按钮监听*/
	public void onClickHeader(View v) {
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
    	String content = this.mContent.getText().toString();
    	PreViewActivity.startAction(this, content);
    }
}