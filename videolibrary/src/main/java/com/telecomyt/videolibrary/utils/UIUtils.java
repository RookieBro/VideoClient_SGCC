package com.telecomyt.videolibrary.utils;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.telecomyt.videolibrary.R;


public class UIUtils {

    private static Application mApp;
    private static int mMainThreadId;
    private static Handler mHandler;
    private static Toast emptyToast;
    private static Toast toast;

    public static void init(Application app) {
        mApp = app;
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static boolean textIsEmpty(String s, String toast) {
        if (TextUtils.isEmpty(s)) {
            if (emptyToast == null && !TextUtils.isEmpty(toast)) {
                emptyToast = UIUtils.showToast(toast);
                emptyToast = null;
            }
            return true;
        }
        return false;
    }

    public static int getCurrentProcessId() {
        return Process.myPid();
    }

    public static Context getContext() {
        return mApp.getApplicationContext();
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    // /////////////////加载资源文件 ///////////////////////////

    // 获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    // 获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    // 获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    // 获取颜色
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    // 获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);// 返回具体像素值
    }

    // /////////////////dip和px转换//////////////////////////

    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static int px2sp(int px) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }

    public static int sp2px(int px) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }


    // /////////////////加载布局文件//////////////////////////
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    // /////////////////判断是否运行在主线程//////////////////////////
    public static boolean isRunOnUIThread() {
        // 获取当前线程id, 如果当前线程id和主线程id相同, 那么当前就是主线程
        int myTid = Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    // 运行在主线程
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程, 直接运行
            r.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程
            getHandler().post(r);
        }
    }

    // 运行在子线程
    public static void runOnOtherThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程
            new Thread(r).start();
        } else {
            // 如果是子线程
            r.run();
        }
    }

    public static Toast showToast(String text) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public static Toast showToast(@StringRes int textId) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(getContext(), getString(textId), Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public static void showToastInSaveThread(final String text) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                showToast(text);
            }
        });
    }

    public static WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public static int getScreenWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static Dialog dialog = null;

    /**
     * 显示等待提示框
     *
     * @param context
     */
    public static void showProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (dialog != null && dialog.isShowing())
            return;
        dialog = new Dialog(context, R.style.Theme_MyDialog);
        dialog.setContentView(R.layout.dialog_progress_layout);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                closeProgressDialog();
                return true;
            }
        });
    }

    /**
     * 关闭等待提示框
     */
    public static void closeProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
