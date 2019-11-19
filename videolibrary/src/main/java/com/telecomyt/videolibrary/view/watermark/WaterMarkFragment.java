package com.telecomyt.videolibrary.view.watermark;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.view.watermark.params.BaseParams;
import com.telecomyt.videolibrary.view.watermark.view.WaterMarkTextView;


/**
 * 水印
 * <p>
 * Created by Richard on 2017/7/26.
 */

public class WaterMarkFragment extends android.support.v4.app.Fragment {

    private WaterMarkTextView wmt1, wmt2, wmt3, wmt4, wmt5, wmt6;
    private WaterMarkTextView[] waterMarkTextViews = new WaterMarkTextView[]{wmt1, wmt2, wmt3, wmt4, wmt5, wmt6};

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_water_mark, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new BaseParams(view, getContext()).autoBuildParams();   // 尺寸转换
        wmt1 = (WaterMarkTextView) view.findViewById(R.id.wmt_1);
        wmt2 = (WaterMarkTextView) view.findViewById(R.id.wmt_2);
        wmt3 = (WaterMarkTextView) view.findViewById(R.id.wmt_3);
        wmt4 = (WaterMarkTextView) view.findViewById(R.id.wmt_4);
        wmt5 = (WaterMarkTextView) view.findViewById(R.id.wmt_5);
        wmt6 = (WaterMarkTextView) view.findViewById(R.id.wmt_6);
    }

    public void refreshWmtv() {
        for (WaterMarkTextView view : waterMarkTextViews) {
            if (view != null) {
                view.refreshWmText();
            }
        }
    }

}
