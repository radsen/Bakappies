package com.udacity.bakappies.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by radsen on 5/16/17.
 */

public class NonSwipeableViewPager extends ViewPager {
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Disables the swipe with the finger
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Disables the swipe with the finger
        return false;
    }
}
