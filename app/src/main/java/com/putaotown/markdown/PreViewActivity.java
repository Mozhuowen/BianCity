package com.putaotown.markdown;

import com.putaotown.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class PreViewActivity extends AppCompatActivity
{
	private static String showContent;
	private TextView mTextView;
    private MDReader mMDReader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_markdown_preview);
		this.setActionBar();
		
		mTextView = (TextView)findViewById(R.id.activity_markdown_preview_text);
        mMDReader = new MDReader(showContent);
        mTextView.setTextKeepState(mMDReader.getFormattedContent(),BufferType.SPANNABLE);
	}
	
	public static void startAction(Context context,String content) {
		showContent = content;
		context.startActivity(new Intent(context,PreViewActivity.class));
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