package com.putaotown.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ALinearLayout extends LinearLayout
{

	public ALinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public ALinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ALinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	 @SuppressWarnings("unused")
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        // For simple implementation, or internal size is always 0.
	        // We depend on the container to specify the layout size of
	        // our view. We can't really know what it is since we will be
	        // adding and removing different arbitrary views and do not
	        // want the layout to change as this happens.
	        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
	 
	        // Children are just made to fill our space.
	        int childWidthSize = getMeasuredWidth();
	        int childHeightSize = getMeasuredHeight();
	        //高度和宽度一样
	        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }
	
}