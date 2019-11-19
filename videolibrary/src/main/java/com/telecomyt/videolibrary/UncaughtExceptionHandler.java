package com.telecomyt.videolibrary;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.telecomyt.videolibrary.utils.CloseUtils;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * 全局捕获异常
 */
class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private static UncaughtExceptionHandler mHandler;

    public static UncaughtExceptionHandler getInstance(Context context) {
        if (mHandler == null) {
            synchronized (UncaughtExceptionHandler.class) {
                if (mHandler == null) {
                    mHandler = new UncaughtExceptionHandler(context);
                }
            }
        }
        return mHandler;
    }

    private UncaughtExceptionHandler(Context c) {
        mContext = c;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        //收集崩溃日志
        String info = collectPhoneInfo(mContext);
        String result = getErr(ex);
        info += result;
        LogUtils.writeLogFileByDate("未捕获异常 \n start：\n" + info + "\n end \n");
        saveFile(info);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void saveFile(String info) {
        String time = TimeUtils.formatNowTime("yyyy-MM-dd HH-mm-ss-SSS");
        String fileName = "crash_" + time + "ERR";
        String path = Config.DEFAULT_LOG_FILE_PATH + File.separator + "errs";
        FileOutputStream stream = null;
        try {
            File file = new File(path);
            if (!file.exists() || !file.isDirectory()) file.mkdirs();
            stream = new FileOutputStream(new File(path, fileName));
            stream.write(info.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                CloseUtils.close(stream);
            }
        }
    }

    private String getErr(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter err = new PrintWriter(writer);
        ex.printStackTrace(err);
//        Throwable t = ex.getCause();
//        while (t != null) {
//            t.printStackTrace(err);
//            t = ex.getCause();
//        }
        err.close();
        return writer.toString();
    }


    private String collectPhoneInfo(Context c) {
        String s = "";
        try {
            String packageInfo = packageInfo(c);
            s += packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String phoneInfo = getPhoneInfo();
            s += phoneInfo;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return s;
    }

    private String getPhoneInfo() throws IllegalAccessException {
        String s = "";
        Field[] fields = Build.class.getFields();
        if (fields != null && fields.length > 0) {
            for (Field f : fields) {
                f.setAccessible(true);
                s += f.getName() + ":" + f.get(null).toString() + "\n";
            }
        }
        return s;
    }

    private String packageInfo(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(
                context.getPackageName(), PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            String packageName = packageInfo.packageName;
            return "packageName:" + packageName + "\n"
                    + "versionName:" + versionName + "\n"
                    + "versionCode:" + versionCode + "\n"
                    + "userName:" + Config.UserInfo.getVideoUserName() + "\n";
        }
        return "";
    }
}