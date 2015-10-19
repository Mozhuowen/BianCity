package com.putaotown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.putaotown.net.SearchUtil;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.tools.LogUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class searchActivity extends AppCompatActivity implements OnTouchListener
{
	private boolean isSubmit = false;
	private SearchView mSearchView;  
	private ListView mListView; 
	private TextView mHint;
	InputMethodManager imm ;	//全局输入法控制
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
  		
  		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
  		this.mHint = (TextView) findViewById(R.id.activity_search_hint);
  		this.mListView = (ListView) findViewById(R.id.activity_search_list);
  		this.mListView.setOnTouchListener(this);
  		this.setActionBar();
//  		this.mSearchView = (SearchView) findViewById(R.id.activity_search_content);
  		        
	}
	
	public void setSearchView() {
		if (this.mSearchView == null)
			return;
		mSearchView.setIconifiedByDefault(true);  
        mSearchView.onActionViewExpanded();  
        mSearchView.setFocusable(true);  
        mSearchView.setQueryHint("搜索边城");
        
        mSearchView.setOnQueryTextListener(new OnQueryTextListener(){  
            @Override  
            public boolean onQueryTextChange(String queryText) {  
            	LogUtil.v("searchActivity info: 	", "onQueryTextChange");
            	mListView.setAdapter(null);
            	mHint.setVisibility(View.GONE);
            	if (queryText != null && queryText.length() > 0 && !isSubmit)
            		SearchUtil.getkeyword(searchActivity.this, queryText);          	
                return true;  
            }

			@Override
			public boolean onQueryTextSubmit(String query) {
				LogUtil.v("searchActivity info: 	", "onQueryTextSubmit");
				mListView.setAdapter(null);
				mListView.setOnItemClickListener(null);
				mHint.setVisibility(View.VISIBLE);
				mHint.setText("正在搜索...");
				SearchUtil.searchTown(searchActivity.this, query);				
				return true;
			}
	});
	}
	
	public void onGetKeyWords(final List<String> words) {
		if (words != null && words.size() > 0) {
			List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
			for (int i=0;i<words.size();i++) {
				Map<String,Object> listitem = new HashMap<String,Object>();
				listitem.put("key", words.get(i));
				listItems.add(listitem);
			}
			SimpleAdapter simpleadapter = new SimpleAdapter(this
					,listItems
					,R.layout.listitem_search_keyword
					,new String[]{"key"}
					,new int[]{R.id.listitem_search_keyword});
			this.mListView.setAdapter(simpleadapter);
			//bind click accident
			this.mListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					isSubmit = true;
					mSearchView.setQuery(words.get(position), true);
				}
				
			});
		}
	}
	
	public void onSearchTown(List<ApplyTown> towns) {
		isSubmit = false;
		if (towns != null && towns.size() > 0 ) {
			this.mHint.setVisibility(View.GONE);
			SearchTownAdapter searchadapter = new SearchTownAdapter(this,towns);
			this.mListView.setAdapter(searchadapter);
		} else {
			this.mHint.setVisibility(View.VISIBLE);
			this.mHint.setText("暂无结果");
		}
	}
	
	public static void startAction(Context context) {
		context.startActivity(new Intent(context,searchActivity.class));
	}
	
	public void backEvent(View source){
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
		return false;
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_search, null);
		actionbar.setCustomView(v);
		this.mSearchView = (SearchView) v.findViewById(R.id.actionbar_searchview);
		this.setSearchView();
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