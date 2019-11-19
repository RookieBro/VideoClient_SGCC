package com.telecomyt.videolibrary.utils;

import com.telecomyt.videolibrary.VideoClient;

/**
 * Created by lbx on 2017/9/7.
 */

public class Process {

    // 运行在主线程
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程, 直接运行
            r.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程
            VideoClient.getMainHandler().post(r);
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

    // /////////////////判断是否运行在主线程//////////////////////////
    public static boolean isRunOnUIThread() {
        // 获取当前线程id, 如果当前线程id和主线程id相同, 那么当前就是主线程
        int myTid = android.os.Process.myTid();
        if (myTid == VideoClient.getMainThreadId()) {
            return true;
        }
        return false;
    }
}
