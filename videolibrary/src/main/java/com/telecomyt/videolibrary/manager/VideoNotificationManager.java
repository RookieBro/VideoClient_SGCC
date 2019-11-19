package com.telecomyt.videolibrary.manager;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.ui.activity.VideoActivity;

/**
 * @author lbx
 * @date 2018/1/13.
 */

public class VideoNotificationManager {

    private static Builder mBuilderMiss;
    private static Builder mBuilderComingCall;
    private static Setting mSetting;

    public static Builder getBuilderMiss() {
        return mBuilderMiss;
    }

    public static Builder getBuilderComingCall() {
        return mBuilderComingCall;
    }

    private VideoNotificationManager() {
    }

    public static Setting init(Application app, Builder builder, Builder builder2) {
        mBuilderMiss = builder == null ? getVideoNotificationDefaultBuilder(app) : builder;
        mBuilderComingCall = builder2 == null ? getVideoNotificationDefaultBuilder(app) : builder2;
        return mSetting == null ? mSetting = new Setting() : mSetting;
    }

    public static Setting getSetting() {
        return mSetting;
    }

    public static Builder getVideoNotificationDefaultBuilder(Context context) {
        return new Builder()
                .setContentIntent(contentIntent(context))
                .setLogoRes(R.drawable.ic_launcher)
                .setNotifyTitle("您有一条会议未接听")
                .setContentSplit(Builder.CONTENT_USER_REPLACE + "呼叫的视频会议");
    }

    public static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, VideoActivity.class);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class Setting {
        private boolean isShowNotification = true;

        public boolean isShowNotification() {
            return isShowNotification;
        }

        public void setShowNotification(boolean showNotification) {
            isShowNotification = showNotification;
        }
    }

    public static class Builder {

        public static final String CONTENT_USER_REPLACE = "#USER#";
        private int logoRes;
        private PendingIntent intent;
        private String notifyTitle;
        private String contentSplit;

        public Builder setLogoRes(@DrawableRes int logoRes) {
            this.logoRes = logoRes;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.intent = intent;
            return this;
        }

        public String getNotifyTitle() {
            return notifyTitle;
        }

        public Builder setNotifyTitle(String notifyTitle) {
            this.notifyTitle = notifyTitle;
            return this;
        }

        public String getContentSplit() {
            return contentSplit;
        }

        public Builder setContentSplit(String contentSplit) {
            this.contentSplit = contentSplit;
            return this;
        }

        public int getLogoRes() {
            return logoRes;
        }

        public PendingIntent getIntent() {
            return intent;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "logoRes=" + logoRes +
                    ", intent=" + intent +
                    ", notifyTitle='" + notifyTitle + '\'' +
                    ", contentSplit='" + contentSplit + '\'' +
                    '}';
        }
    }

}
