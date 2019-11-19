package com.telecomyt.videolibrary.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.utils.UIUtils;

/**
 * Created by lbx on 2017/9/27.
 */

public class CircleTextView extends android.support.v7.widget.AppCompatTextView {

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundDrawable(UIUtils.getDrawable(R.drawable.circle_textview_bg));
        setGravity(Gravity.CENTER);
        TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
    }

    public void setTextWithColorAndSize(String s, @ColorRes int textColor, int size) {
        setText(s);
        setTextColor(UIUtils.getColor(textColor));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }
}
