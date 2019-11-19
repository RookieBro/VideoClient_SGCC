package com.telecomyt.videolibrary.view.watermark;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.view.watermark.params.BaseParams;
import com.telecomyt.videolibrary.view.watermark.view.WaterMarkTextView;

/**
 * @author lbx
 * @date 2017/11/15.
 */

public class WaterMarkView extends FrameLayout {

    private WaterMarkTextView wmt1, wmt2, wmt3, wmt4, wmt5, wmt6;
    private WaterMarkTextView[] waterMarkTextViews = new WaterMarkTextView[]{wmt1, wmt2, wmt3, wmt4, wmt5, wmt6};

    public WaterMarkView(@NonNull Context context) {
        this(context, null);
    }

    public WaterMarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterMarkView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.fragment_water_mark, null);
        new BaseParams(view, getContext()).autoBuildParams();   // 尺寸转换
        wmt1 = (WaterMarkTextView) view.findViewById(R.id.wmt_1);
        wmt2 = (WaterMarkTextView) view.findViewById(R.id.wmt_2);
        wmt3 = (WaterMarkTextView) view.findViewById(R.id.wmt_3);
        wmt4 = (WaterMarkTextView) view.findViewById(R.id.wmt_4);
        wmt5 = (WaterMarkTextView) view.findViewById(R.id.wmt_5);
        wmt6 = (WaterMarkTextView) view.findViewById(R.id.wmt_6);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void refreshWmtv() {
        for (WaterMarkTextView view : waterMarkTextViews) {
            if (view != null) {
                view.refreshWmText();
            }
        }
    }
}
