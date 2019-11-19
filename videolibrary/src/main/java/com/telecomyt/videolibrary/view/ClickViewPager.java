package com.telecomyt.videolibrary.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.UIUtils;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/6/15.
 */

public class ClickViewPager extends ViewPager {

    public ClickViewPager(Context context) {
        this(context, null);
    }

    public ClickViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(UIUtils.getScreenHeight() / 4, mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            LogUtils.e("异常被活捉了1");
        }
        return false;
    }

    public void scrollPager(MotionEvent ev) {
        try {
            super.onTouchEvent(ev);
        } catch (Exception e) {
            LogUtils.e("异常被活捉了2");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            LogUtils.e("异常被活捉了3");
        }
        return false;
    }

    private int getActivePointerId() {
        int i = -1;
        try {
            Class clazz = ViewPager.class;
            Field mActivePointerId = clazz.getDeclaredField("mActivePointerId");
            mActivePointerId.setAccessible(true);
            i = (int) mActivePointerId.get(ClickViewPager.this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return i;
    }
}
