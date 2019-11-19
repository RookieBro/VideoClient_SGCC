package com.telecomyt.videolibrary.thread;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * @author lbx
 * @date 2017/12/6.
 * 心跳线程
 */

public class HeardThread implements ThreadFactory {

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("heard thread");
        return thread;
    }

}
