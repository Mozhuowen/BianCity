package com.putaotown.views;

import com.putaotown.tools.LogUtil;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

public class BCEditText extends EditText
{
	int[] coordinate = new int[2];
	ScrollView outSideView;
	
	public BCEditText(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	public BCEditText(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	public BCEditText(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged (int horiz, int vert, int oldHoriz, int oldVert)
	{
		LogUtil.v("BCEditor oScrollChanged!", "horiz: "+horiz+" vert: "+vert+" oldHoriz: "+oldHoriz+" oldVert: "+oldVert);
		this.getLocationInWindow(coordinate);
		if (vert == 0 && outSideView != null)
			outSideView.requestDisallowInterceptTouchEvent(false);
		else if (outSideView != null)
			outSideView.requestDisallowInterceptTouchEvent(true);
		super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float rawY = event.getRawY();
		LogUtil.v("BCEditor onTouchEvent!", "rawY: "+rawY);
		if (event.getAction() == MotionEvent.ACTION_UP && outSideView != null) {
			outSideView.requestDisallowInterceptTouchEvent(false);
		} else if (outSideView != null) {
			outSideView.requestDisallowInterceptTouchEvent(true);
		}
		if (rawY < coordinate[1] && outSideView != null)
			outSideView.requestDisallowInterceptTouchEvent(false);
		return super.onTouchEvent(event);
	}
	
	public void setOutSideView(ScrollView scrollview) {
		this.outSideView = scrollview;
	}
	
}