package com.telecomyt.videolibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.telecomyt.videolibrary.bean.GzbVideoDirect;
import com.telecomyt.videolibrary.bean.UserEntityInfo;
import com.telecomyt.videolibrary.callback.CreateRoomCallBack;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.callback.TempVideoCallback;
import com.telecomyt.videolibrary.callback.VideoControlCallback;
import com.telecomyt.videolibrary.callback.VideoLoginCallback;
import com.telecomyt.videolibrary.event.VideoEvent;
import com.telecomyt.videolibrary.gson.CreateTempVideoHelper;
import com.telecomyt.videolibrary.gson.CreateTempVideoRequest;
import com.telecomyt.videolibrary.gson.GzbVideoDirectCallHelper;
import com.telecomyt.videolibrary.gson.GzbVideoDirectCallHelperV1_1;
import com.telecomyt.videolibrary.gson.GzbVideoDirectCallRequest;
import com.telecomyt.videolibrary.gson.GzbVideoRoom;
import com.telecomyt.videolibrary.gson.JoinGzbVideoRoomHelper;
import com.telecomyt.videolibrary.gson.JoinGzbVideoRoomRequest;
import com.telecomyt.videolibrary.gson.NotificationJson;
import com.telecomyt.videolibrary.gson.ReceiveVideoLoginMsgHelper;
import com.telecomyt.videolibrary.http.VideoHttpConfig;
import com.telecomyt.videolibrary.manager.VideoRoomJoinClickManager;
import com.telecomyt.videolibrary.service.VideoService;
import com.telecomyt.videolibrary.ui.activity.DirectCallActivity;
import com.telecomyt.videolibrary.ui.activity.VideoActivity;
import com.telecomyt.videolibrary.utils.GsonUtils;
import com.telecomyt.videolibrary.utils.HttpUtil;
import com.telecomyt.videolibrary.utils.LogUtils;
import com.telecomyt.videolibrary.utils.PermissionUtils;
import com.telecomyt.videolibrary.utils.UIUtils;
import com.telecomyt.videolibrary.view.watermark.WaterMarkConfig;
import com.telecomyt.videolibrary.view.watermark.view.WaterMarkTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import static com.telecomyt.videolibrary.Domain.DIRECT_CALL;


/**
 * Created by lbx on 2017/9/7.
 */

public class VideoClient {

    private static VideoClient mVideoClient;
    private static int mMainThreadId;
    private static VideoRoomJoinClickManager mVideoRoomJoinClickManager;
    /**
     * 证书
     */
    private String caFileName;
    private long address;
    private static Handler mMainHandler;

    public static VideoClient getInstance() {
        if (mVideoClient == null) {
            synchronized (VideoClient.class) {
                if (mVideoClient == null) {
                    mVideoClient = new VideoClient();
                }
            }
        }
        return mVideoClient;
    }

    private VideoClient() {
    }

    public void setActionBarColor(@ColorRes int actionBarColor) {
        Config.ACTION_BAR_COLOR = actionBarColor;
    }

    /**
     * onPause是否渲染
     */
    public void setVideoActivityPause(boolean videoActivityPause) {
        Config.VIDEO_ACTIVITY_PAUSE = videoActivityPause;
    }

    /**
     * 刷新水印
     */
    public void refreshWaterMark(String text) {
        WaterMarkConfig.waterMarkText = text;
        refreshWaterMark();
    }

//    public void refreshWaterMark(String text, int textSize) {
//        WaterMarkConfig.waterMarkText = text;
//        WaterMarkConfig.waterMarkTextSize = textSize;
//        refreshWaterMark();
//    }
//
//    public void refreshWaterMark(String text, int textSize, int textColor) {
//        WaterMarkConfig.waterMarkText = text;
//        WaterMarkConfig.waterMarkTextSize = textSize;
//        WaterMarkConfig.waterMarkTextColor = textColor;
//        refreshWaterMark();
//    }

    public void refreshWaterMark() {
        Map<WaterMarkTextView, WaterMarkTextView> waterMarkCacheMap = WaterMarkConfig.getWaterMarkCacheMap();
        if (waterMarkCacheMap != null) {
            for (WaterMarkTextView view : waterMarkCacheMap.values()) {
                if (view != null) {
                    view.refreshWmText();
                }

            }
        }
    }

    /**
     * 第一步  初始化 必须在主线程
     *
     * @param app           Application
     * @param mainUrl       主服务器ip
     * @param videoUrl      video服务器url
     * @param videoUserName 登录账号
     * @param videoPassword 登录密码
     */
    public static void init(Application app, String mainUrl, String videoUrl, String videoUserName, String videoPassword) {
        Config.setMainIp(mainUrl);
        Config.setVideoUrl(videoUrl);
        Config.UserInfo.setVideoUserName(videoUserName);
        Config.UserInfo.setVideoPassword(videoPassword);
        System.loadLibrary("VidyoClientApp");
        System.loadLibrary("VideoClient");
        mMainThreadId = android.os.Process.myTid();
        mMainHandler = new Handler(Looper.getMainLooper());
        UIUtils.init(app);
        //全局捕获异常
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.getInstance(app));
        registerVideoJoinClick(new VideoRoomJoinClickManager());
    }

    public static void registerVideoJoinClick(VideoRoomJoinClickManager videoRoomJoinClickManager) {
        mVideoRoomJoinClickManager = videoRoomJoinClickManager;
    }

    public static VideoRoomJoinClickManager getVideoRoomJoinClickManager() {
        return mVideoRoomJoinClickManager;
    }

    public static void init(Application app, String mainUrl, String videoUrl) {
        init(app, mainUrl, videoUrl, "", "");
    }

    public static void start(final Activity ac, final String videoUsername, String videoPassword, String userNickName,
                             String policeNum, UserEntityInfo userEntityInfo, final VideoLoginCallback callback) {
        //第一步 获取权限
        if (!VideoClient.getInstance().checkVideoPermission(ac)) {
            if (callback != null) {
                callback.permissionErr("permission may be null");
            }
            return;
        }
        //第二步 检查网络 必要的初始化
        if (!VideoClient.getInstance().getNetState(ac)) {
            if (callback != null) {
                callback.netErr("network may be null");
            }
            return;
        }
        if (Config.pushAvailable && userEntityInfo != null) {
            Config.UserInfo.setUserEntityInfo(userEntityInfo);
        }
        Config.UserInfo.setVideoUserName(videoUsername);
        Config.UserInfo.setVideoPassword(videoPassword);
        Config.UserInfo.setNickName(userNickName);
        Config.UserInfo.setPoliceId(policeNum);
        WaterMarkConfig.setNickName(userNickName);
        WaterMarkConfig.setPoliceNum(policeNum);
        ac.startService(new Intent(ac, VideoService.class));
        //登录
        LogUtils.e("登录:" + videoUsername + "  " + videoPassword + "  " + Config.getVideoUrl());
        VideoClient.getInstance().login(Config.getVideoUrl(), videoUsername, videoPassword, new VideoLoginCallback() {
            @Override
            public void loginSuccess() {
                Config.VIDEO_LOGIN = true;
                sendLoginResult(videoUsername);
                if (callback != null) {
                    callback.loginSuccess();
                }
            }

            @Override
            public void loginFailed(String err) {
                Config.VIDEO_LOGIN = false;
                if (callback != null) {
                    callback.loginFailed(err);
                }
            }

            @Override
            public void permissionErr(String err) {
                if (callback != null) {
                    callback.permissionErr(err);
                }
            }

            @Override
            public void netErr(String err) {
                if (callback != null) {
                    callback.netErr(err);
                }
            }
        });
    }

    private static void sendLoginResult(String videoUsername) {
        HttpUtil.getInstance().post(new ReceiveVideoLoginMsgHelper(videoUsername), new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {

            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    public static void start(Activity ac, String userName, String userNickName, String policeNum, UserEntityInfo userEntityInfo, final VideoLoginCallback callback) {
        String videoUserName = Config.UserInfo.getVideoUserName();
        if (!TextUtils.isEmpty(userName) && userName.equals(videoUserName)) {
            start(ac, videoUserName, Config.UserInfo.getVideoPassword(), userNickName, policeNum, userEntityInfo, callback);
        } else {
            if (callback != null) {
                callback.loginFailed("no password");
            }
        }
    }


    public static Handler getMainHandler() {
        return mMainHandler;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }


    public boolean checkVideoPermission(Activity ac) {
        return PermissionUtils.getInstance().checkPermission(ac);
    }

    public void setPushAvailable(boolean available) {
        Config.pushAvailable = available;
    }

    public void removeLoginCallback() {
        VideoClientOutEvent.getInstance().setVideoLoginCallback(null);
    }

    public void login(String vidyoportalName, String userName, String passwordName, VideoLoginCallback callback) {
        VideoClientOutEvent.getInstance().setVideoLoginCallback(callback);
        Login(vidyoportalName, userName, passwordName);
    }

    public void registerVideoCallback(VideoControlCallback videoControlCallback) {
        LogUtils.writeLogFileByDate("VideoClient registerVideoCallback");
        VideoClientOutEvent.mVideoControlCallbackList.add(videoControlCallback);
    }

    public void unregisterVideoCallback(VideoControlCallback videoControlCallback) {
        if (VideoClientOutEvent.mVideoControlCallbackList.contains(videoControlCallback)) {
            VideoClientOutEvent.mVideoControlCallbackList.remove(videoControlCallback);
            LogUtils.writeLogFileByDate("VideoClient unregisterVideoCallback");
        }
    }

    public void setShowWaterView(boolean isShow) {
        Config.showWaterView = isShow;
        LogUtils.writeLogFileByDate("VideoClient setShowWaterView:" + isShow);
    }

    /**
     * 获取网络状态和初始化等
     */
    public boolean getNetState(Activity activity) {
        if (!Config.GET_ALL_PERMISSION) {
            LogUtils.writeLogFileByDate("VideoClient getNetState no net");
            return false;
        }
        //获取网络状态
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //获取证书
        String caFileName = VideoInit.writeCaCertificates(activity);
        this.caFileName = caFileName;
        //netInfo 手机的网络状态  Wifi or 2G/3G/4G or others
        if (netInfo == null || !netInfo.isConnected()) {
            return false;
            //初始化地址  如果为长度为0，返回false
        } else if (!initialize(activity)) {
            return false;
        }

        if (!Config.VIDEO_LOGIN) {
            //native__隐藏ToolBar？？
            HideToolBar(false);
            //native__回声抵消？？？
            SetEchoCancellation(true);
        }
        VideoClient.getInstance().DisableAutoLogin();
        return true;
    }

    /**
     * 加载vidyo证书初始化
     */
    public boolean initialize(Activity activity) {
        String pathDir;
        try {//data/data/com.example.comchangruicamera/files/ca-certificates.crt
            pathDir = VideoInit.getAndroidInternalMemDir(activity);
//			pathDir = getAndroidSDcardMemDir();
        } catch (Exception e) {
            pathDir = "/data/data/com.vidyo.vidyosample/app_marina/";
        }

        String logDir;
        try {//data/data/com.example.comchangruicamera/cache/
            logDir = VideoInit.getAndroidCacheDir(activity);
//			logDir = getAndroidSDcardMemDir();
        } catch (Exception e) {
            logDir = "/data/data/com.vidyo.vidyosample/app_marina/";
        }
        if (TextUtils.isEmpty(caFileName)) {
            caFileName = VideoInit.writeCaCertificates(activity);
        }
        address = Construct(caFileName, logDir, pathDir, activity);
        return address != 0;
    }

    public void directCallDialogAvailable(boolean available) {
        Config.showDirectCallDialog = available;
    }

    public void joinVideoRoom(final Context context, final GzbVideoRoom room, final CreateRoomCallBack createRoomCallBack) {
        LogUtils.writeLogFileByDate("VideoClient joinVideoRoom");
        if (!TextUtils.isEmpty(room.getRoomKey())) {
            VideoActivity.start(context, room, Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
        HttpUtil.getInstance().post(Config.getMainUrl(), new JoinGzbVideoRoomHelper(room.gzbChatRoomId
                        , room.gzbChatRoomName, room.joinUids),
                new HttpPostCallback<String>() {
                    @Override
                    public void onSuccess(String responseInfo) {
                        JoinGzbVideoRoomRequest request = new Gson().fromJson(responseInfo, JoinGzbVideoRoomRequest.class);
                        int code = request.code;
                        LogUtils.writeLogFileByDate("VideoClient joinVideoRoom join room onSuccess code " + code);
                        if ((code == 0 || code == 1) && !TextUtils.isEmpty(request.roomInfo.getRoomKey())) {
                            room.setData(request.roomInfo.getRoomKey());
                            if (createRoomCallBack != null) {
                                createRoomCallBack.success(request);
                            }
                            VideoActivity.start(context, room, Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            if (createRoomCallBack != null) {
                                createRoomCallBack.err(request.message);
                            }
                            LogUtils.writeLogFileByDate("joinVideoRoom join room failed :" + request.message);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        LogUtils.writeLogFileByDate("joinVideoRoom join room onFailure :" + msg);
                        if (createRoomCallBack != null) {
                            createRoomCallBack.err(msg);
                        }
                    }
                });
        }
    }

    /**
     * 点呼
     */
    public void directCall(final Context context, final GzbVideoDirect direct) {
        LogUtils.writeLogFileByDate("directCall start");
        UserEntityInfo info = Config.UserInfo.getUserEntityInfo();
        Object obj;
        if (Config.pushAvailable && info != null) {
            obj = new GzbVideoDirectCallHelperV1_1(direct.userName, direct.callUserGzbID, info.getAreaId(), info.getImei());
        } else {
            obj = new GzbVideoDirectCallHelper(direct.userName, direct.callUserGzbID);
        }
        HttpUtil.getInstance().post(Config.getMainUrl(), obj, new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                GzbVideoDirectCallRequest request = new Gson().fromJson(responseInfo, GzbVideoDirectCallRequest.class);
                if (request != null) {
                    int code = request.code;
                    LogUtils.writeLogFileByDate("directCall code:" + code);
                    String roomKey = request.roomKey;
                    if (code == 1) {
                        if (startVideoMeeting(roomKey, context, direct)) {
                            LogUtils.writeLogFileByDate("directCall code 1 : startVideoMeeting");
                            EventBus.getDefault().post(new VideoEvent.VideoDirectCallResult(
                                    VideoEvent.VideoDirectCallResult.DirectCallStatusType.SUCCESS));
                            return;
                        }
                    } else if (code == 2) {
                        EventBus.getDefault().post(new VideoEvent.VideoDirectCallResult(
                                VideoEvent.VideoDirectCallResult.DirectCallStatusType.BUSY));
                        LogUtils.e("directCall====用户在忙碌中");
                        LogUtils.writeLogFileByDate("directCall busy");
                        UIUtils.showToast("用户忙碌中，请稍后再试");
                        return;
                    } else if (code == 3 || code == 4) {
                        LogUtils.e(code == 3 ? "directCall====用户未上线" : "directCall====其他原因呼叫失败");
                        VideoEvent.VideoDirectCallResult.DirectCallStatusType status = code == 3 ?
                                VideoEvent.VideoDirectCallResult.DirectCallStatusType.DIS_ONLINE :
                                VideoEvent.VideoDirectCallResult.DirectCallStatusType.ERR;
                        EventBus.getDefault().post(new VideoEvent.VideoDirectCallResult(status));
                        LogUtils.writeLogFileByDate("directCall disOnline or otherErr:" + code);
                        if (startVideoMeeting(roomKey, context, direct)) {
                            LogUtils.writeLogFileByDate("directCall code 3 or 4 : startVideoMeeting : " + code);
                            return;
                        }
                    }
                    LogUtils.writeLogFileByDate("directCall Config.showDirectCallDialog");
                    if (Config.showDirectCallDialog) {
                        context.startActivity(DirectCallActivity.newInstance(context, roomKey,
                                direct.callUserGzbName, false, "u0", request.message));
                    }
                } else {
                    EventBus.getDefault().post(new VideoEvent.VideoDirectCallResult(
                            VideoEvent.VideoDirectCallResult.DirectCallStatusType.ERR_SERVER));
                    LogUtils.writeLogFileByDate("join room failed request may be null");
                }
            }

            @Override
            public void onFailure(String msg) {
                LogUtils.writeLogFileByDate("join room onFailure :" + msg);
                EventBus.getDefault().post(new VideoEvent.VideoDirectCallResult(
                        VideoEvent.VideoDirectCallResult.DirectCallStatusType.ERR_SERVER));
            }

            @Override
            public void onFinished(boolean isSuccess) {
                super.onFinished(isSuccess);
                LogUtils.writeLogFileByDate("directCall finish : " + isSuccess);
            }
        });
    }

    /**
     * 直接进入会议页面
     */
    private boolean startVideoMeeting(String roomKey, Context context, GzbVideoDirect direct) {
        LogUtils.writeLogFileByDate("joinVideoRoom startVideoMeeting");
        if (!TextUtils.isEmpty(roomKey)) {
            VideoActivity.start(context, roomKey, DIRECT_CALL + direct.callUserGzbID, false);
            return true;
        }
        return false;
    }

    public void startTempVideo(final Context context, final CreateTempVideoHelper createTempVideoHelper, final TempVideoCallback callback) {
        if (TextUtils.isEmpty(createTempVideoHelper.meetingString)) {
            return;
        }
        HttpUtil.getInstance().post(createTempVideoHelper, new HttpPostCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                LogUtils.writeLogFileByDate("joinVideoRoom startTempVideo onSuccess");
                CreateTempVideoRequest request = GsonUtils.getInstance().fromJson(msg, CreateTempVideoRequest.class);
                LogUtils.writeLogFileByDate("joinVideoRoom startTempVideo onSuccess code " + request.code);
                if (request.code == 1) {
                    VideoActivity.start(context, request);
                    if (callback != null) {
                        callback.success();
                    }
                } else {
                    UIUtils.showToast(request.message);
                    if (callback != null) {
                        callback.err();
                    }
                }
            }

            @Override
            public void onFailure(String err) {
                LogUtils.writeLogFileByDate("joinVideoRoom startTempVideo onFailure " + err);
                UIUtils.showToast(err);
                if (callback != null) {
                    callback.err();
                }
            }
        });
    }

    private void showAlertDialog(Context context, final NotificationJson notifi) {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (notifi.msgType == VideoHttpConfig.NOTIFICATION_ADD_USER) {
            builder.setMessage(notifi.message + context.getString(R.string.into_room_list));
        }
        builder.setTitle("");
        builder.setPositiveButton(context.getString(R.string.know), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.e("conf notify click");
                if (notifi.msgType == VideoHttpConfig.NOTIFICATION_ADD_USER) {
                    EventBus.getDefault().post(VideoHttpConfig.VIDEO_ROOM_REFRESH);
                }
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                LogUtils.e("conf notify onkey");
                if (notifi.msgType == VideoHttpConfig.NOTIFICATION_ADD_USER) {
                    EventBus.getDefault().post(VideoHttpConfig.VIDEO_ROOM_REFRESH);
                }
                dialog.dismiss();
                return false;
            }
        });
    }

    /**
     * 初始化
     */
    public native long Construct(String caFileName, String logDir, String pathDir, Activity activity);

    /**
     * Dispose
     */
    public native void Dispose();

    /**
     * AutoStartMicrophone
     */
    public native void AutoStartMicrophone(boolean autoStart);

    /**
     * AutoStartCamera
     */
    public native void AutoStartCamera(boolean autoStart);

    /**
     * AutoStartSpeaker
     */
    public native void AutoStartSpeaker(boolean autoStart);

    /**
     * 登录
     */
    private native void Login(String vidyoPortalName, String userName, String passwordName);

    /**
     * 开始渲染
     */
    public native void Render();

    /**
     * 结束渲染
     */
    public native void RenderRelease();

    /**
     * 隐藏会议界面的默认工具栏
     */
    public native void HideToolBar(boolean disableBar);

    /**
     * 设置相机
     */
    public native void SetCameraDevice(int camera, int selfView);

    /**
     * 设置预览模式启动
     */
    public native void SetPreviewModeON(boolean pip);

    /**
     * 禁止自动登陆
     */
    public native void DisableAutoLogin();

    /**
     * 设置渲染画面的长宽
     */
    public native void Resize(int width, int height);

    public native int SendAudioFrame(byte[] frame, int numSamples,
                                     int sampleRate, int numChannels, int bitsPerSample);

    public native int GetAudioFrame(byte[] frame, int numSamples,
                                    int sampleRate, int numChannels, int bitsPerSample);

    public native int SendVideoFrame(byte[] frame, String fourcc, int width,
                                     int height, int orientation, boolean mirrored);

    /**
     * 渲染view的触摸事件
     */
    public native void TouchEvent(int id, int type, int x, int y);

    /**
     * 渲染view的旋转方向
     */
    public native void SetOrientation(int orientation);

    /**
     * 禁用相机
     */
    public native void MuteCamera(boolean muteCamera);

    /**
     * 停止视频的流
     */
    public native void DisableAllVideoStreams();

    /**
     * 开始视频的流
     */
    public native void EnableAllVideoStreams();

    /**
     * 启动会议媒体
     */
    public native void StartConferenceMedia();

    /**
     * 回声抵消
     */
    public native void SetEchoCancellation(boolean aecenable);

    /**
     * 设置speaker音量
     */
    public native void SetSpeakerVolume(int volume);

    /**
     * 禁用共享事件
     */
    public native void DisableShareEvents();

    public native void sendConference();

    /**
     * 加入会议
     */
    public native void joinRoomConference(String pro, String roomKey, String name,
                                          boolean muteCamera, boolean muteMicrophone,
                                          boolean muteSpeaker,String pin);

    public native void setAllOrientation();

    /**
     * 结束会议
     */
    public native void stopConference();

    /**
     * 禁用麦克
     */
    public native void micDisenable();

    /**
     * 启用麦克
     */
    public native void micEnable();

    public native void SimpleConference();

    /**
     * 启用扬声器
     */
    public native void listenerEnable();

    /**
     * 禁用扬声器
     */
    public native void ListenerDisenable();

    /**
     * 启用相机
     */
    public native void CameraEnable();

    /**
     * 禁用相机
     */
    public native void CameraDisenable();

    /**
     * 切换相机设备
     */
    public native void changeCamera();

    /**
     * 注销
     */
    public native void SignOff();

    public native void ParticipantsLimit(int maxNum);

    /**
     * 加入ex会议
     */
    public native void joinRoomConferenceEX(String pro, String key, String name);

    /**
     * 设置清晰度
     */
    public native void SetVideoResolution(int a);

    public native void DriveUsed(int camera, int speaker, int microphone);

    /**
     * 接听p2p来电会议
     */
    public native void Answer();

    /**
     * 拒绝接听p2p来电会议
     */
    public native void Decline();

    /**
     * 进行p2p呼叫
     */
    public native void DirectCall(String who);

    /**
     * 代理
     */
    public native void SetVidyoProxy(String proxyAddress,String port);//172.19.3.199:443


    public native void SendMsgG(String ndkmsg);

    /**
     * 设置字体
     */
    public native void fontName(String fileDir);

    /**
     * 获取用户的uri
     */
    public native String getUri(int pos);

    /**
     * 获取用户的uri
     */
    public native String getDetail(int id);

    /**
     * 置顶
     */
    public native boolean setVideoTop(String uri);

    /**
     * 取消置顶
     */
    public native boolean cancelVideoTop(String uri);

    /**
     * 设置背景色
     */
    public native boolean setBackgroundColor(int red, int green, int blue);

//    public native String[] getDetails();

}
