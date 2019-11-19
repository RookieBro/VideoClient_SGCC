package com.telecomyt.videolibrary.task;

import android.content.Context;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.gson.HeartSend;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;

/**
 * @author lbx
 * @date 2017/12/6.
 * 上传心跳包的逻辑
 */

public class HeardRunnable implements Runnable {

    private Context mContext;

    public HeardRunnable(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        //上传心跳包
        if (Config.VIDEO_LOGIN) {
            sendHeard();
        }
    }

    /**
     * 上传心跳包
     */
    private void sendHeard() {
        HttpUtil.getInstance().post(new HeartSend(), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                LogUtils.e("上传心跳成功:" + msg);
            }

            @Override
            public void onFailure(String err) {
                LogUtils.e("上传心跳失败:" + err);
            }
        });
    }
}
