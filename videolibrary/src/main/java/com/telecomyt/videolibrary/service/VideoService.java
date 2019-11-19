package com.telecomyt.videolibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.Domain;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.VideoState;
import com.telecomyt.videolibrary.bean.ChangeCamera;
import com.telecomyt.videolibrary.bean.VidyoComingCall;
import com.telecomyt.videolibrary.bean.VidyoShowComingCall;
import com.telecomyt.videolibrary.callback.VideoControlCallback;
import com.telecomyt.videolibrary.task.HeardRunnable;
import com.telecomyt.videolibrary.thread.HeardThread;
import com.telecomyt.videolibrary.ui.activity.DirectCallActivity;
import com.telecomyt.videolibrary.ui.activity.VideoActivity;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.telecomyt.videolibrary.Config.VIDEO_LOGIN;

/**
 * Created by lbx on 2017/9/11.
 */

public class VideoService extends Service {

    /**
     * 上传心跳包的循环线程(池)
     */
    private ScheduledThreadPoolExecutor mHeardService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("videoClient", "VideoService start");
        EventBus.getDefault().register(this);
        LogUtils.writeLogFileByDate("VideoService start");
        //设置会议事件回调  必须
        VideoClient.getInstance().registerVideoCallback(videoControlCallback);
        //上传心跳包
//        sendHeard();
    }

    private VideoControlCallback videoControlCallback = new VideoControlCallback() {

        @Override
        public void callEnded() {
            LogUtils.e("callEnded");
            LogUtils.writeLogFileByDate("VideoService callEnded");
            //结束会议
            EventBus.getDefault().post(VideoState.VIDEO_ENDED);
        }

        @Override
        public void callStarted() {
            LogUtils.writeLogFileByDate("VideoService callStarted");
            //开始会议
            EventBus.getDefault().post(Domain.INIT_VIDEO_ACTIVITY);
        }

        @Override
        public void personCount(int count) {
            super.personCount(count);
            LogUtils.writeLogFileByDate("VideoService personCount:" + count);
            //在当前会议里的人数 会议人员变化的时候才会回调
        }

        @Override
        public void signOut() {
            super.signOut();
            LogUtils.writeLogFileByDate("VideoService signOut");
            //登出事件 如被T掉等
            VIDEO_LOGIN = false;
        }

        @Override
        public void comingCall(String name) {
            super.comingCall(name);
            LogUtils.writeLogFileByDate("VideoService comingCall:" + name);
            //点对点会议来电
            EventBus.getDefault().post(VideoState.VIDEO_COMING_CALL);
            EventBus.getDefault().post(new VidyoComingCall(name));
        }

        @Override
        public void comingCallRefuse() {
            super.comingCallRefuse();
            LogUtils.writeLogFileByDate("VideoService comingCallRefuse");
            //点对点会议对方拒接
            EventBus.getDefault().post(VideoState.VIDEO_COMING_CALL_DISAGREE);
        }

        @Override
        public void join() {
            super.join();
            LogUtils.writeLogFileByDate("VideoService join");
            //加入会议
            EventBus.getDefault().post(VideoState.VIDEO_JOIN);
        }

        @Override
        public void cameraSwitch(String name) {
            super.cameraSwitch(name);
            LogUtils.writeLogFileByDate("VideoService cameraSwitch:" + name);
            //切换相机回调
            if (!TextUtils.isEmpty(name) && VideoActivity.usedCamera == 1) {
                EventBus.getDefault().post(new ChangeCamera(0));
            } else if (TextUtils.isEmpty(name) && VideoActivity.usedCamera == 0) {
                EventBus.getDefault().post(new ChangeCamera(-1));
            } else if (!TextUtils.isEmpty(name) && VideoActivity.usedCamera == -1) {
                EventBus.getDefault().post(new ChangeCamera(1));
            }
        }
    };

    /**
     * 上传心跳包
     */
    private void sendHeard() {
        if (mHeardService == null) {
            mHeardService = new ScheduledThreadPoolExecutor(1, new HeardThread());
            //每3秒上传一次心跳包
            mHeardService.scheduleWithFixedDelay(new HeardRunnable(this), 0, 5, TimeUnit.SECONDS);
            mHeardService.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            LogUtils.writeLogFileByDate("VideoService sendHeard");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoClient.getInstance().unregisterVideoCallback(videoControlCallback);
        LogUtils.writeLogFileByDate("VideoService destroy");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 0 )
    public void comingCall(VidyoShowComingCall comingCall) {
        if(comingCall != null){
            if(comingCall.isBusy()){
                UIUtils.showToast("对方忙碌中");
            }else {
                if (Config.showDirectCallDialog) {
                    startActivity(DirectCallActivity.newInstance(getBaseContext(), "", comingCall.getName(), true, "", ""));
                }
            }
        }
    }
}
