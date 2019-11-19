package com.telecomyt.videoclient;

import android.app.Application;

import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.manager.VideoNotificationManager;

/**
 * @author lbx
 * @date 2017/9/11.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 第一步  初始化 必须在主线程
         *
         * @param app  Application
         * @param mainUrl  主服务器ip(电信易通)
         * @param videoUrl  video服务器url
         * @param videoUserName  登录账号
         * @param videoPassword  登录密码
         */
//        VideoClient.init(this, "20.1.11.91", "http://ydjw.telecomyt.com.cn");
        VideoClient.init(this, "20.1.11.91", "http://ezvp.telecomyt.com.cn");
//        VideoClient.init(this, "192.168.43.254", "http://ydjw.telecomyt.com.cn");
        //设置页面里actionBar的颜色  默认为"#2F3FA0"
        VideoClient.getInstance().setActionBarColor(R.color.blue_title);
//        VideoClient.registerVideoJoinClick(new MyVideoRoomJoinClickManager());
        //未接会议的通知格式  设置图标  title  内容和点击事件等
        VideoNotificationManager.Builder builder = new VideoNotificationManager.Builder();
        builder.setLogoRes(R.drawable.easy_icon)
                .setContentIntent(null)
                .setNotifyTitle("未接会议")
                .setContentSplit("来自" + VideoNotificationManager.Builder.CONTENT_USER_REPLACE + "的未接会议");

        //正在呼叫时的会议的通知格式  设置图标  title  内容和点击事件等
        VideoNotificationManager.Builder builder2 = new VideoNotificationManager.Builder();
        builder2.setLogoRes(R.drawable.easy_icon)
                .setContentIntent(VideoNotificationManager.contentIntent(this))
                .setNotifyTitle("会议通知")
                .setContentSplit(VideoNotificationManager.Builder.CONTENT_USER_REPLACE + "正在呼叫您");
        //设置通知
        VideoNotificationManager.Setting setting = VideoNotificationManager.init(this, builder, builder2);
        //将通知功能打开
        setting.setShowNotification(true);

        //是否显示水印
        VideoClient.getInstance().setShowWaterView(true);
        VideoClient.getInstance().setPushAvailable(true);
        //不弹出点呼的窗口
        VideoClient.getInstance().directCallDialogAvailable(false);
    }
}
