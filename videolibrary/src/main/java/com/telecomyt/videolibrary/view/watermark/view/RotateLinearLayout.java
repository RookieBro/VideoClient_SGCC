package com.telecomyt.videolibrary.view.watermark.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.telecomyt.videolibrary.R;


/**
 * 会旋转的线性布局(水印布局)
 * 一些数据都是自己目测的，不精准
 * <p>
 * Created by liut2 on 2017/7/27.
 */

public class RotateLinearLayout extends LinearLayout {

    private static final int DEFAULT_DEGREES = 0;
    private int mDegrees = 90;
    private int res;
    float translate = 0;


    public RotateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView);
        mDegrees = a.getInteger(R.styleable.RotateTextView_degree, DEFAULT_DEGREES);
        translate = a.getDimension(R.styleable.RotateTextView_translate, 0f);
        a.recycle();
    }

    /**
     * 重新测量，使高宽为屏幕对角线
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        res = (int) Math.sqrt(h * h + w * w);

        int resMS = MeasureSpec.makeMeasureSpec(res, wMode);
        super.onMeasure(resMS, resMS);
    }

    /**
     * 操作画布，实现平移旋转
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, translate);
        canvas.rotate(mDegrees);
        super.onDraw(canvas);
    }

}
