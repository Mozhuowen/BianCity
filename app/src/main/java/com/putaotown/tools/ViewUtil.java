package com.putaotown.tools;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtil
{
	/**
	 * 重置listv的高度
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
//        params.height = 692;
        Log.d("list view height", ""+params.height);
        listView.setLayoutParams(params);  
    }
	
	public static void setListViewHeightBasedOnChildren(ListView listView,int add) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + add;  
//        params.height = 692;
        Log.d("list view height", ""+params.height);
        listView.setLayoutParams(params);  
    }
}