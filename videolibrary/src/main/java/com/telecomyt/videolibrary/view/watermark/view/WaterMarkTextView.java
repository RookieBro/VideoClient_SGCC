package com.telecomyt.videolibrary.view.watermark.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.telecomyt.videolibrary.view.watermark.WaterMarkConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * 水印文字
 * <p>
 * Created by liut2 on 2017/7/28.
 */

public class WaterMarkTextView extends AppCompatTextView {

    public WaterMarkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine(true);
        refreshWmText();
        setTextSize(WaterMarkConfig.waterMarkTextSize);
        Map<WaterMarkTextView, WaterMarkTextView> waterMarkCacheMap = WaterMarkConfig.getWaterMarkCacheMap();
        if (waterMarkCacheMap != null) {
            waterMarkCacheMap.put(this, this);
        }
    }

    /**
     * 请按需修改
     */
    public void refreshWmText() {
//        String userName;
//        String policeNo;
//        String serialNum;
//        String nowTime;
//        userName = WaterMarkConfig.getNickName();
//        policeNo = WaterMarkConfig.getPoliceNum();
//        serialNum = Build.SERIAL + " ";
//        nowTime = getTime() + " ";
//        setText(userName);
//        append(policeNo);
//        append(serialNum);
//        append(nowTime);
        setTextColor(WaterMarkConfig.waterMarkTextColor);
        setText(WaterMarkConfig.waterMarkText);
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Map<WaterMarkTextView, WaterMarkTextView> waterMarkCacheMap = WaterMarkConfig.getWaterMarkCacheMap();
        if (waterMarkCacheMap != null) {
            waterMarkCacheMap.remove(this);
        }
    }
}
