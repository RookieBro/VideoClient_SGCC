package com.jzxiang.pickerview.config;

import android.graphics.Color;

import com.jzxiang.pickerview.data.Type;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.utils.UIUtils;

/**
 * Created by jzxiang on 16/5/15.
 */
public class DefaultConfig {
    public static final Type TYPE = Type.mALL;
    public static final int COLOR = Color.parseColor("#ece6e6");
    public static final int TITLEBAR_COLOR = UIUtils.getColor(R.color.white_main);
//    public static final int COLOR = Color.parseColor("#ffffff");
    public static final int TOOLBAR_TV_COLOR = 0xFFFFFFFF;
    public static final int TV_NORMAL_COLOR = Color.parseColor("#9a9a9a");
    public static final int TV_SELECTOR_COLOR = Color.parseColor("#03a9f4");
    public static final int TV_SIZE = 16;
    public static final boolean CYCLIC = true;
    public static String CANCEL = "";//取消
    public static String SURE = "确定";
    public static String TITLE = "";
    public static String YEAR = "年";
    public static String MONTH = "月";
    public static String DAY = "日";
    public static String HOUR = "：";//时
    public static String MINUTE = "";//分


}
