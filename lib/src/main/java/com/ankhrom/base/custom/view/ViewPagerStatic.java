package com.ankhrom.base.custom.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerStatic extends ViewPager {

    public ViewPagerStatic(Context context) {
        super(context);
    }

    public ViewPagerStatic(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //disable touch - so disable swipe
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //disable touch - so disable swipe
        return false;
    }
}
