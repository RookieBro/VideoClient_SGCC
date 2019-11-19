package com.telecomyt.videolibrary.view.watermark.params;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 适配 基类(无需考虑方形),若竖屏,则定宽720,若横屏,则定宽1280,单位长度等于单位宽度,不提供参数保存,效率高
 * 
 * @author shaojr & wangxf
 * 
 */
public class BaseParams {

    private static final String TAG = "BaseParams";
    private static float oldWidth;

    private View convertView;
    private Context context;
    private static float scale = 0.0f;

    /**
     * 当前设备宽度
     */
    protected static float screenWidth;

    /**
     * 适配Activity
     * @param context
     */
    public BaseParams(Context context) {
        this(null, context);
    }

    /**
     * 适配View
     * @param convertView
     */
    public BaseParams(View convertView) {
        this(convertView, null);
    }

    public BaseParams(View convertView, Context context) {
        DensityManager densityManager = DensityManager.getInstance(context);
        if (densityManager == null) {
            Log.e(TAG, "The densityManager has not initialized,please use first.");
        } else {
            this.convertView = convertView;
            this.context = context;
            scale = densityManager.getScale();
            screenWidth = densityManager.getWidth();
            if (densityManager.isPortrait()) {
                oldWidth = 720;
            } else {
                oldWidth = 1280;
            }
        }
    }

    /**
     * 转换 长度 的比例
     * 
     * @param
     * @return
     */
    public static int transLength(float len) {
        float nowWidth = len / oldWidth * screenWidth + 0.5f;
        return (int) nowWidth;
    }

    /**
     * 可自动适配
     * 
     * @author wangxf
     * @param
     */
    public void autoBuildParams() {
        View view = null;
        if (convertView == null) {
            if (context != null && context instanceof Activity) {
                view = ((Activity) context).getWindow().getDecorView();
            }
        } else {
            view = convertView;
        }
        if (view == null) {
            Log.e(TAG, "The view is null,maybe context is null or context is not Activity.");
            return;
        }
        autoBuild(view);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            autoViewGroup(viewGroup);
        }
    }

    /**
     * 对容器布局进行遍历
     * 
     * @author wangxf
     * @param viewGroup
     */
    private static void autoViewGroup(ViewGroup viewGroup) {
        int length = viewGroup.getChildCount();
        for (int i = 0; i < length; i++) {
            View layout = viewGroup.getChildAt(i);
            autoBuild(layout);
            if (!(layout instanceof DatePicker || layout instanceof TimePicker || layout instanceof NumberPicker)
                    && layout instanceof ViewGroup) {
                autoViewGroup((ViewGroup) layout);
            }
        }
    }

    /**
     * 适配gridview
     * 
     * @param res_id
     * @param columnWidth
     *            0表示不设置每个item宽度
     * @param horizontalSpacing
     * @param verticalSpacing
     */
    public void buildGridViewParam(int res_id, int columnWidth, int horizontalSpacing, int verticalSpacing) {
        View view;
        if (convertView == null) {
            view = ((Activity) context).findViewById(res_id);
        } else {
            view = convertView.findViewById(res_id);
        }
        if (view instanceof GridView) {
            buildGridViewParam((GridView) view, columnWidth, horizontalSpacing, verticalSpacing);
        }
    }

    /**
     * 
     * @param view
     * @param columnWidth
     *            0表示不设置每个item宽度
     * @param horizontalSpacing
     * @param verticalSpacing
     */
    public void buildGridViewParam(GridView view, int columnWidth, int horizontalSpacing, int verticalSpacing) {
        if (columnWidth > 0)
            view.setColumnWidth(transLength(columnWidth));
        if (horizontalSpacing >= 0)
            view.setHorizontalSpacing(transLength(horizontalSpacing));
        if (verticalSpacing >= 0)
            view.setVerticalSpacing(transLength(verticalSpacing));
    }

    private static void autoBuild(View layout) {
        if (layout instanceof CheckBox || layout instanceof RadioButton) {
            // Do not support the label CheckBox or RadioButton.
        } else {
            int paddingLeft = getOriginNum(layout.getPaddingLeft());
            int transPaddingLeft = transLength(paddingLeft);
            int paddingTop = getOriginNum(layout.getPaddingTop());
            int transPaddingTop = transLength(paddingTop);
            int paddingRight = getOriginNum(layout.getPaddingRight());
            int transPaddingRight = transLength(paddingRight);
            int paddingBottom = getOriginNum(layout.getPaddingBottom());
            int transPaddingBottom = transLength(paddingBottom);
            layout.setPadding(transPaddingLeft, transPaddingTop, transPaddingRight, transPaddingBottom);
            if (layout instanceof ListView) {
                int dividerHight = getOriginNum(((ListView) layout).getDividerHeight());
                int dh = transLength(dividerHight);
                ((ListView) layout).setDividerHeight(dh);
            }
        }
        if (layout instanceof TextView) {
            TextView tv = (TextView) layout;
            float oSize = tv.getTextSize();
            int size = getOriginNum(oSize);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, transLength(size));
        }
        ViewParent parent = layout.getParent();
        if (parent instanceof LinearLayout || parent instanceof RelativeLayout || parent instanceof FrameLayout) {
            MarginLayoutParams lp = (MarginLayoutParams) layout.getLayoutParams();
            int width = getOriginNum(lp.width);
            int height = getOriginNum(lp.height);
            int top = getOriginNum(lp.topMargin);
            int left = getOriginNum(lp.leftMargin);
            int right = getOriginNum(lp.rightMargin);
            int bottom = getOriginNum(lp.bottomMargin);

//            //support rtl layout 2016/12/20
//            int start = getOriginNum(lp.getMarginStart());
//            int end = getOriginNum(lp.getMarginEnd());

            if (width > 0) {
                lp.width = transLength(width);
            }
            if (height > 0) {
                lp.height = transLength(height);
            }
            if (top != 0) {
                lp.topMargin = transLength(top);
            }
            if (left != 0) {
                lp.leftMargin = transLength(left);
            }
            if (right != 0) {
                lp.rightMargin = transLength(right);
            }
            if (bottom != 0) {
                lp.bottomMargin = transLength(bottom);
            }
//            if (start != 0) {
//                lp.setMarginStart(start);
//            }
//            if (end != 0) {
//                lp.setMarginEnd(end);
//            }
            layout.setLayoutParams(lp);
        } else {
            if (parent instanceof ViewGroup) {
                ViewGroup.LayoutParams lp = layout.getLayoutParams();
                int width = getOriginNum(lp.width);
                int height = getOriginNum(lp.height);
                if (width > 0) {
                    lp.width = transLength(width);
                }
                if (height > 0) {
                    lp.height = transLength(height);
                }
                layout.setLayoutParams(lp);
            }
        }

    }

    /**
     * 自定义view宽高
     * 
     * @param view
     * @param width
     * @param height
     */
    public static void buildParamsByManual(View view, int width, int height, boolean flag) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (width > 0) {
                lp.width = flag ? width : transLength(width);
            }
            if (height > 0) {
                lp.height = flag ? height : transLength(height);
            }
            view.setLayoutParams(lp);
        }
    }

    /**
     * 自定义view宽高及margin属性
     * 
     * @param view
     * @param width
     * @param height
     */
    public static void buildParamsByManual(View view, int width, int height, int top, int right, int bottom, int left) {
        ViewParent parent = view.getParent();
        if (parent instanceof LinearLayout || parent instanceof RelativeLayout || parent instanceof FrameLayout) {
            MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
            if (width > 0) {
                lp.width = transLength(width);
            }
            if (height > 0) {
                lp.height = transLength(height);
            }
            if (top != 0) {
                lp.topMargin = transLength(top);
            }
            if (left != 0) {
                lp.leftMargin = transLength(left);
            }
            if (right != 0) {
                lp.rightMargin = transLength(right);
            }
            if (bottom != 0) {
                lp.bottomMargin = transLength(bottom);
            }
            view.setLayoutParams(lp);
        }
    }

    /**
     * 设置字体大小
     * 
     * @param textView
     * @param textSize
     */
    public static void buildTextSize(TextView textView, int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, transLength(textSize));
    }

    /**
     * 获取 density=1 时的长度, 即除当前设备的密度； 最后得到 px
     * 
     * @param len
     * @return
     */
    private static int getOriginNum(float len) {
        if (len > 0) {
            return (int) (len / scale);
        }
        return (int) len;
    }

}
