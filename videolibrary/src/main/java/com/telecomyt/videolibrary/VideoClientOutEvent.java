package com.telecomyt.videolibrary;

import com.telecomyt.videolibrary.bean.VidyoMessage;
import com.telecomyt.videolibrary.callback.VideoControlCallback;
import com.telecomyt.videolibrary.callback.VideoLoginCallback;
import com.telecomyt.videolibrary.event.VideoEvent;
import com.telecomyt.videolibrary.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lbx on 2017/9/13.
 */

public class VideoClientOutEvent {

    private static VideoClientOutEvent mVideoClientOutEvent;
    private static VideoLoginCallback mVideoLoginCallback;
    public static List<VideoControlCallback> mVideoControlCallbackList = new ArrayList<>();

    public static VideoClientOutEvent getInstance() {
        if (mVideoClientOutEvent == null) {
            synchronized (VideoClientOutEvent.class) {
                if (mVideoClientOutEvent == null) {
                    mVideoClientOutEvent = new VideoClientOutEvent();
                }
            }
        }
        return mVideoClientOutEvent;
    }

    private VideoClientOutEvent() {

    }

    public VideoLoginCallback getVideoLoginCallback() {
        return mVideoLoginCallback;
    }

    public void setVideoLoginCallback(VideoLoginCallback mVideoLoginCallback) {
        VideoClientOutEvent.mVideoLoginCallback = mVideoLoginCallback;
    }

    /**
     * 相机变化回调
     */
    public void cameraSwitchCallback(String name) {
        LogUtils.writeLogFileByDate("video cameraSwitchCallback：" + name);
        LogUtils.e("cameraSwitchCallback");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.cameraSwitch(name);
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventCameraChange(name));
    }

    /**
     * 参会人员数量回调
     */
    public void meetingPersonCountGet(int count) {
        LogUtils.writeLogFileByDate("video meetingPersonCountGet：" + count);
        LogUtils.e("meetingPersonCountGet = " + count);
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.personCount(count);
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventParticipantsChange(count));
    }

    /**
     * 获取会议列表的显示人员数
     */
    public void meetingPersonCountShow(int count) {
        LogUtils.writeLogFileByDate("video meetingPersonCountShow：" + count);
        LogUtils.e("meetingPersonCountShow:" + count);
    }

    /**
     * 退出登录回调
     */
    public void LogOffMustCallback(int error) {
        LogUtils.writeLogFileByDate("video LogOffMustCallback");
        LogUtils.e("LogOffMustCallback" + error);
        Config.VIDEO_LOGIN = false;
        if (mVideoLoginCallback != null) {
            mVideoLoginCallback.loginFailed(error + "");
        }
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.signOut();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventLogout(error));
    }

    public void messageBox(String s) {
        LogUtils.e("messageBox");
    }

    /**
     * 结束会议回调
     */
    public void callEndedCallback() {
        LogUtils.writeLogFileByDate("video callEndedCallback");
        Config.VIDEO_START = false;
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.callEnded();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoEnded());
    }

    //P2P来电
    public void ComingCallCallback(String name) {
        LogUtils.writeLogFileByDate("video ComingCallCallback:" + name);
        LogUtils.e("ComingCallCallback:" + name);
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.comingCall(name);
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventDirectCallComing(name));
    }

    //对方取消了呼叫
    public void ComingCallCanceledCallback() {
        LogUtils.writeLogFileByDate("video ComingCallCanceledCallback");
        LogUtils.e("ComingCallCanceledCallback");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.comingCallCanceled();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventDirectCallCanceled());
    }

    //P2P来电对方挂掉
    public void ComingCallEndCallback() {
        LogUtils.writeLogFileByDate("video ComingCallEndCallback");
        LogUtils.e("ComingCallEndCallback");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.comingCallRefuse();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventDirectCallOtherDown());
    }

    //P2P呼叫结束
    public void EndingCallCallback() {
        LogUtils.writeLogFileByDate("video EndingCallCallback");
        LogUtils.e("EndingCallCallback");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.comingCallRefuse();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventDirectCallEnded());
    }

    public void JoinCallBack() {
        LogUtils.writeLogFileByDate("video JoinCallBack");
        LogUtils.e("JoinCallBack");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.join();
                }
            }
        }
        VideoClient.getInstance().SetPreviewModeON(true);
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoJoin());
    }

    /**
     * 开始会议回调
     */
    public void callStartedCallback() {
        LogUtils.writeLogFileByDate("video callStartedCallback");
        Config.VIDEO_START = true;
        LogUtils.e("callStartedCallback");
        if (!mVideoControlCallbackList.isEmpty()) {
            for (VideoControlCallback c : mVideoControlCallbackList) {
                if (c != null) {
                    c.callStarted();
                }
            }
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoStart());
    }

    /**
     * 登陆成功回调
     */
    public void loginSuccessfulCallback() {
        LogUtils.writeLogFileByDate("video loginSuccessfulCallback");
        Config.VIDEO_LOGIN = true;
        VideoInit.fontName();
        VideoClient.getInstance().SetCameraDevice(1, 1);
        LogUtils.e("loginSuccessfulCallback");
        if (mVideoLoginCallback != null) {
            mVideoLoginCallback.loginSuccess();
        }
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoLoginSuccess());
    }

    /**
     * 登录失败回调
     */
    public void loginFailCallback() {
        LogUtils.writeLogFileByDate("video loginFailCallback");
        LogUtils.e("loginFailCallback");
        Config.VIDEO_LOGIN = false;
        if (mVideoLoginCallback != null) {
            mVideoLoginCallback.loginFailed("video login failed");
        }
    }

    public void libraryStartedCallback() {
        //禁止自动登陆
        VideoClient.getInstance().DisableAutoLogin();
        LogUtils.e("libraryStartedCallback");
    }

    /**
     * 群组聊天接收消息
     */
    public void ComingMsgGCallback(String uri, String name, String msg) {
        LogUtils.e("ComingMsgGCallback:" + "\nuri = " + uri + "\nname = " + name + "\nmsg = " + msg);
        EventBus.getDefault().post(new VidyoMessage(name, msg, System.currentTimeMillis(), 0));
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoMessageBox(uri, name, msg));
    }

    /**
     * 扬声器
     */
    public void mutedAudioOutCallback(boolean isMute) {
        LogUtils.writeLogFileByDate("video mutedAudioOutCallback:" + isMute);
        LogUtils.e("mutedAudioOut:" + isMute);
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoMuteAudioOut(isMute));
    }

    /**
     * 自己禁止了mic
     */
    public void mutedAudioInCallback(boolean isMute) {
        LogUtils.writeLogFileByDate("video mutedAudioInCallback:" + isMute);
        LogUtils.e("mutedAudioIn:" + isMute);
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoMuteAudioIn(isMute));
    }

    /**
     * 服务器禁止了mic
     */
    public void mutedAudioInByServerCallback(boolean isMute) {
        LogUtils.writeLogFileByDate("video mutedAudioInByServerCallback:" + isMute);
        LogUtils.e("mutedAudioInByServerCallback:" + isMute);
        EventBus.getDefault().post(new VideoEvent.VideoEventVideoMuteAudioInByServer(isMute));
    }

    /**
     * 服务器禁止了视频
     */
    public void mutedVideoByServerCallback(boolean isMute) {
        LogUtils.writeLogFileByDate("video mutedVideoByServerCallback:" + isMute);
        LogUtils.e("mutedVideoByServerCallback:" + isMute);
        EventBus.getDefault().post(new VideoEvent.VideoEventMuteVideoByServer(isMute));
    }

    public void joinRoomWrongCodeCallback(int error) {
        LogUtils.e("error======"+error);
        EventBus.getDefault().post(new VideoEvent.VideoEventJoinRoomErrorCode(error));
    }

    public void directCallWrongCodeCallback(int error) {
        LogUtils.e("directCallWrongCodeCallback error======"+error);

    }
}
