package com.telecomyt.videolibrary.ui.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.R;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.VideoState;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.event.VideoEvent;
import com.telecomyt.videolibrary.manager.VideoNotificationManager;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import static com.telecomyt.videolibrary.Domain.DIRECT_CALL;

/**
 * 点对点视频会议弹窗界面
 */
public class DirectCallActivity extends BaseActivity {

    //    主动呼叫弹窗
    private AlertDialog mDirectCallDialog;
    //    来电弹窗
    private AlertDialog mComingCallDialog;
    //    来电者昵称
    private String mComingCallName = "";
    //    是否是来电
    private boolean isComing;
    //被呼叫者id
    private String callId;
    //json的message
    private String result = "";
    public static final String CALL_ERR_CODE = "-1";
    //    错误弹窗
    private AlertDialog mErrDialog;
    private MediaPlayer mPlayer;
    private String mRoomKey;


    public static Intent newInstance(Context context, String roomKey, String name, boolean isComing, String callId, String result) {
        Intent intent = new Intent(context, DirectCallActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("isComing", isComing);
        intent.putExtra("callId", callId);
        intent.putExtra("result", result);
        intent.putExtra("roomKey", roomKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public int getLayoutID() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getAttributes().flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | getWindow().getAttributes().flags);
        }
        EventBus.getDefault().register(this);
        initIntent();
        return R.layout.activity_coming_call;
    }

    private void initIntent() {
        Intent intent = getIntent();
        mComingCallName = intent.getStringExtra("name");
        callId = intent.getStringExtra("callId");
        result = intent.getStringExtra("result");
        mRoomKey = intent.getStringExtra("roomKey");
        isComing = intent.getBooleanExtra("isComing", true);
    }

    @Override
    public void initView(View view) {
        Config.VIDEO_SDK_IN = true;
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
        if (isComing) {
            showComingDialog();
            showNotification(1, mComingCallName, false);
        } else {
            if (!TextUtils.isEmpty(callId) && !CALL_ERR_CODE.equals(callId)) {
                LogUtils.e("呼叫:" + callId);
//                点对点视频会议
                VideoClient.getInstance().DirectCall(callId);
                showDirectCallDialog();
//                VideoActivity.start(this, mRoomKey, DIRECT_CALL + callId, false);
            } else {
                showErrDialog();
            }
        }
    }

    /**
     * 错误提示弹窗
     */
    private void showErrDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_MyDialog);
        mErrDialog = builder.setCancelable(false)
                .setTitle("视频会议")
                .setMessage("呼叫失败: " + result + "  " + mComingCallName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LogUtils.writeLogFileByDate("showErrDialog dismiss");
                        finishWithAnim();
                    }
                })
                .create();
        mErrDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayer.release();
                    mPlayer = null;
                }
            }
        });
        mErrDialog.show();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void onClick(View view, int id) {

    }

    /**
     * 弹出来电弹窗
     */
    private void showComingDialog() {
        int timeL = String.valueOf(System.currentTimeMillis()).length();
        int nameL = mComingCallName.length();
        if (nameL > timeL) {
            mComingCallName = mComingCallName.substring(0, nameL - timeL);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_MyDialog);
        mComingCallDialog = builder.setCancelable(false)
                .setTitle("视频会议")
                .setMessage("来自 " + mComingCallName + " 的会议请求")
                .setPositiveButton("接听", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        VideoClient.getInstance().Answer();
                        EventBus.getDefault().post(new VideoEvent.DirectCallAnswer());
//                        VideoActivity.start(DirectCallActivity.this, null,-1);
                        LogUtils.writeLogFileByDate("showComingDialog sure dismiss");
                        finishWithAnim();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.e("拒绝接听P2P");
                        VideoClient.getInstance().Decline();
                        dialog.dismiss();
                        EventBus.getDefault().post(new VideoEvent.DirectCallDecline());
                        LogUtils.writeLogFileByDate("showComingDialog cancel dismiss");
                        finishWithAnim();
                    }
                })
                .create();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        initPlayer();
        mComingCallDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayer.release();
                    mPlayer = null;
                }
            }
        });
        mComingCallDialog.show();
    }

    private void initPlayer() {
        try {
            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(R.raw.notifi);
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            afd.close();
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlayer.setVolume(1000, 1000);
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主动呼叫弹窗
     */
    private void showDirectCallDialog() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        initPlayer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_MyDialog);
        mDirectCallDialog = builder.setCancelable(false)
                .setTitle("视频会议")
                .setMessage("正在呼叫 " + mComingCallName)
                .setPositiveButton("取消呼叫", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.e("取消呼叫P2P");
                        VideoClient.getInstance().stopConference();
                        dialog.dismiss();
                        LogUtils.writeLogFileByDate("showDirectCallDialog cancel dismiss");
                        finishWithAnim();
                    }
                })
                .create();
        mDirectCallDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayer.release();
                    mPlayer = null;
                }
            }
        });
        mDirectCallDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void directCall(String s) {
        LogUtils.e("directCall = " + s);
        if (VideoState.VIDEO_COMING_CALL_DISAGREE.equals(s)) {
            LogUtils.e("对方挂了P2P会议");
            showNotification(0, mComingCallName, true);
            LogUtils.writeLogFileByDate("eventBus directCall decline dismiss");
            finishWithAnim();
        } else if (VideoState.VIDEO_JOIN.equals(s)) {
            LogUtils.e("对方接受点对点会议");
            LogUtils.writeLogFileByDate("eventBus directCall answer dismiss");
            finishWithAnim();
            VideoActivity.start(this, null, -1);
        }
    }

    //P2P呼叫对方，对方拒接的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void directCallRefused(VideoEvent.VideoEventDirectCallOtherDown videoEventDirectCallOtherDown) {
        LogUtils.writeLogFileByDate("eventBus directCall decline dismiss");
        UIUtils.showToast("对方忙碌中");
        finishWithAnim();
    }

    //P2P呼叫对方，对方拒接的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void directCallRefused(VideoEvent.VideoEventDirectCallCanceled callCanceled) {
        LogUtils.writeLogFileByDate("eventBus directCall decline dismiss");
        UIUtils.showToast("对方取消了呼叫");
        finishWithAnim();
    }

    //P2P呼叫结束
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void directCallEended(VideoEvent.VideoEventDirectCallEnded callEnded) {
//        UIUtils.showToast("呼叫结束");
        finishWithAnim();
    }

    //P2P对方接听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void directCallJoined(VideoEvent.VideoEventVideoJoin callJoin) {
        finishWithAnim();
    }


    private void dismissComingDialog() {
        if (mComingCallDialog != null && mComingCallDialog.isShowing()) {
            mComingCallDialog.dismiss();
        }
    }

    private void dismissDirectCallDialog() {
        if (mDirectCallDialog != null && mDirectCallDialog.isShowing()) {
            mDirectCallDialog.dismiss();
        }
    }

    private void dismissErrDialogDialog() {
        if (mErrDialog != null && mErrDialog.isShowing()) {
            mErrDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(1);
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayer.stop();
            mPlayer.release();
        }
        super.onDestroy();
        Config.VIDEO_SDK_IN = false;
    }

    private void finishWithAnim() {
        dismissComingDialog();
        dismissDirectCallDialog();
        dismissErrDialogDialog();
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * missCall true为点呼未接的通知,没有点击事件。false为呼叫时的即时通知，点击跳入页面
     */
    private void showNotification(int code, String userName, boolean missCall) {
        VideoNotificationManager.Setting setting = VideoNotificationManager.getSetting();
        if (setting == null || !setting.isShowNotification()) {
            return;
        }
        VideoNotificationManager.Builder builder = missCall ? VideoNotificationManager.getBuilderMiss() : VideoNotificationManager.getBuilderComingCall();
        if (builder == null) {
            builder = VideoNotificationManager.getVideoNotificationDefaultBuilder(this);
        }
        String contentSpit = builder.getContentSplit();
        if (!TextUtils.isEmpty(contentSpit) && contentSpit.contains(VideoNotificationManager.Builder.CONTENT_USER_REPLACE)) {
            contentSpit = contentSpit.replace(VideoNotificationManager.Builder.CONTENT_USER_REPLACE, userName);
        }
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int logoRes = builder.getLogoRes();
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(builder.getNotifyTitle())
                        .setContentText(contentSpit)
                        .setSmallIcon(logoRes)
                        // 点击消失
                        .setAutoCancel(true)
                        // 设置该通知优先级
                        .setPriority(Notification.PRIORITY_MAX)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), logoRes))
                        .setTicker("收到一条会议请求")
                        // 通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())
                        // 通知产生的时间，会在通知信息里显示
                        // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND);
        PendingIntent pendingIntent = builder.getIntent();
        if (!missCall && pendingIntent != null) {
            notifyBuilder.setContentIntent(pendingIntent);
        }
        notifyManager.notify(code, notifyBuilder.build());
    }

}
