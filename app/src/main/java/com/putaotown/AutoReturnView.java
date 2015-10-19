package com.putaotown;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AutoReturnView extends ViewGroup
{
	private final static int VIEW_MARGIN=2;

    public AutoReturnView(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("AutoReturnView-TAG", "widthMeasureSpec = "+widthMeasureSpec+" heightMeasureSpec"+heightMeasureSpec);
        
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            // measure
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        Log.d("AutoReturnView-TAG", "changed = "+arg0+" left = "+arg1+" top = "+arg2+" right = "+arg3+" botom = "+arg4);
        final int count = getChildCount();
        int row=0;// which row lay you view relative to parent
        int lengthX=arg1;    // right position of child relative to parent
        int lengthY=arg2;    // bottom position of child relative to parent
        for(int i=0;i<count;i++){
            
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lengthX+=width+VIEW_MARGIN;
            lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+arg2;
            //if it can't drawing on a same line , skip to next line
            if(lengthX>arg3){
                lengthX=width+VIEW_MARGIN+arg1;
                row++;
                lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+arg2;
                
            }
            
            child.layout(lengthX-width, lengthY-height, lengthX, lengthY);
        }

    }
}