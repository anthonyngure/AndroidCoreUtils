/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Tosh on 6/1/2016.
 */
public class NoSwipeViewPager extends ViewPager {

    private boolean swipeEnabled;

    public NoSwipeViewPager(Context context) {
        super(context);
    }

    public NoSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.swipeEnabled){
            return super.onTouchEvent(ev);
        } else {
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Utils.log("onInterceptTouchEvent");
        if (this.swipeEnabled){
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }
}
