package com.telecomyt.videolibrary.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.utils.UIUtils;


/**
 * Created by 61096 on 2016/6/29.
 * 自定义控件：参会人员
 */
public class PersonView extends LinearLayout {

    public String entityId;
    public String name;
    public boolean haveW;

    public PersonView(Context context) {
        this(context, null);
        haveW = true;
    }

    public void setHaveW(boolean haveW) {
        this.haveW = haveW;
    }

    public PersonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setPersonInfo(Context ctx, String icon, String name, String entityId, boolean isAddBtn) {
        this.entityId = entityId;
        this.name = name;
        this.setOrientation(VERTICAL);
//        CircleImageView circleImageView = new CircleImageView(getContext());
//        circleImageView.setBorderColor(Color.parseColor("#cccccc"));
//        if (haveW) {
//            circleImageView.setBorderWidth(UIUtils.dip2px(2));
//        } else {
//            circleImageView.setBorderWidth(0);
//        }
        CircleTextView circleImageView = new CircleTextView(getContext());
        if (isAddBtn) {
            circleImageView.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.add_person));
        } else {
//            circleImageView.setImageDrawable(icon);
//            circleImageView.setImageResource(R.mipmap.easy_icon);
//            BitmapUtil.getInstance(ctx).display(circleImageView, icon);
            circleImageView.setTextWithColorAndSize(name.length() <= 2 ? name :
                    name.substring(name.length() - 2, name.length()), R.color.white_main, 18);
        }
        setPadding(5, 5, 5, 5);
        int c = UIUtils.dip2px(53);
        addView(circleImageView, 0, new LayoutParams(c, c));
        TextView textView = new TextView(getContext());
        textView.setText(name);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setVisibility(GONE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(10);
        addView(textView, 1, params);
        invalidate();
    }
}
