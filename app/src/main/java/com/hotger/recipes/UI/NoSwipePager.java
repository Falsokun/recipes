package com.hotger.recipes.UI;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Viewpager without swiping
 */
public class NoSwipePager extends ViewPager {

    private boolean isEnabled;

    public NoSwipePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isEnabled && super.onInterceptTouchEvent(event);
    }

    /**
     * Enabled paging
     *
     * @param isEnabled
     */
    public void setPagingEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}