package com.telecomyt.videolibrary.view.watermark;

import com.telecomyt.videolibrary.view.watermark.view.WaterMarkTextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lbx
 * @date 2017/11/15.
 */

public class WaterMarkConfig {

    public static final String DEFAULT_VALUE = "#empty#";
    private static String nickName = DEFAULT_VALUE;
    private static String policeNum = DEFAULT_VALUE;
    public static int waterMarkTextSize = 42;
    public static int waterMarkTextColor = 0x33666666;
    public static String waterMarkText = "";
    private static Map<WaterMarkTextView, WaterMarkTextView> mMap = new HashMap<>();

    public static String getNickName() {
        return nickName;
    }

    public static void setNickName(String nickName) {
        WaterMarkConfig.nickName = nickName;
    }

    public static String getPoliceNum() {
        return policeNum;
    }

    public static void setPoliceNum(String policeNum) {
        WaterMarkConfig.policeNum = policeNum;
    }

    public static Map<WaterMarkTextView, WaterMarkTextView> getWaterMarkCacheMap() {
        return mMap;
    }
}
