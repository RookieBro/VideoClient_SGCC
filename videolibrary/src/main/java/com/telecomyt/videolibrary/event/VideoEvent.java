package com.telecomyt.videolibrary.event;

import com.telecomyt.videolibrary.bean.VideoParticipantBean;

/**
 * @author lbx
 * @date 2017/11/17.
 */

public class VideoEvent {

    /**
     * 摄像头变化
     */
    public static class VideoEventCameraChange {

        public VideoEventCameraChange(String camera) {
            this.camera = camera;
        }

        private String camera;

        public String getCamera() {
            return camera;
        }

    }

    /**
     * 当前会议参会人员变化
     */
    public static class VideoEventParticipantsChange {

        public VideoEventParticipantsChange(int num) {
            this.participantsNum = num;
        }

        private int participantsNum;

        public int getParticipantsNum() {
            return participantsNum;
        }
    }

    /**
     * 登出/登录失败
     */
    public static class VideoEventLogout {

        public VideoEventLogout(int err) {
            this.err = err;
        }

        private int err;

        public int getErr() {
            return err;
        }
    }


    /**
     * P2P来电
     */
    public static class VideoEventDirectCallComing {
        private String name;

        public VideoEventDirectCallComing(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 会议结束
     */
    public static class VideoEventVideoEnded {

    }



    /**
     * P2P呼叫对方，对方拒接的回调
     */
    public static class VideoEventDirectCallOtherDown {

    }

    /**
     * P2P呼叫对方，取消呼叫
     */
    public static class VideoEventDirectCallCanceled {

    }
    /**
     * P2P会议结束
     */
    public static class VideoEventDirectCallEnded {

    }

    /**
     * 加入会议
     */
    public static class VideoEventVideoJoin {

    }

    /**
     * 会议开始
     */
    public static class VideoEventVideoStart {

    }

    /**
     * 登陆成功
     */
    public static class VideoEventVideoLoginSuccess {

    }

    /**
     * 禁用扬声器
     */
    public static class VideoEventVideoMuteAudioOut {
        private boolean isMute;

        public boolean isMute() {
            return isMute;
        }

        public VideoEventVideoMuteAudioOut(boolean isMute) {
            this.isMute = isMute;
        }
    }

    /**
     * 禁用麦克
     */
    public static class VideoEventVideoMuteAudioIn {
        private boolean isMute;

        public boolean isMute() {
            return isMute;
        }

        public VideoEventVideoMuteAudioIn(boolean isMute) {
            this.isMute = isMute;
        }
    }

    /**
     * 服务器禁止了mic
     */
    public static class VideoEventVideoMuteAudioInByServer {
        private boolean isMute;

        public boolean isMute() {
            return isMute;
        }

        public VideoEventVideoMuteAudioInByServer(boolean isMute) {
            this.isMute = isMute;
        }
    }

    /**
     * 服务器禁止了视频
     */
    public static class VideoEventMuteVideoByServer {
        private boolean isMute;

        public boolean isMute() {
            return isMute;
        }

        public VideoEventMuteVideoByServer(boolean isMute) {
            this.isMute = isMute;
        }
    }

    public static class VideoEventJoinRoomErrorCode {
        private int errorCode;


        public VideoEventJoinRoomErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * 会议中文本聊天接收到的消息
     */
    public static class VideoEventVideoMessageBox {
        private String uri;
        private String name;
        private String msg;

        public VideoEventVideoMessageBox(String uri, String name, String msg) {
            this.uri = uri;
            this.name = name;
            this.msg = msg;
        }

        public String getUri() {
            return uri;
        }

        public String getName() {
            return name;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 点击"接听"按钮的回调
     * startActivity(ConferenceActivity.newInstance(context,null, "", "", "", "", ""));
     */
    public static class DirectCallAnswer {

    }

    /**
     * 点击"拒绝"按钮的回调
     */
    public static class DirectCallDecline {

    }

    /**
     * 点击参会人员的会议列表item回调
     */
    public static class VideoParticipantItemClick {

        private VideoParticipantBean videoParticipantBean;
        private int itemPos;

        public VideoParticipantBean getVideoParticipantBean() {
            return videoParticipantBean;
        }

        public VideoParticipantItemClick(VideoParticipantBean videoParticipantBean, int itemPos) {
            this.videoParticipantBean = videoParticipantBean;
            this.itemPos = itemPos;
        }

        public int getItemPos() {
            return itemPos;
        }
    }

    /**
     * 点呼结果
     */
    public static class VideoDirectCallResult {

        public enum DirectCallStatusType {
            /**
             * SUCCESS 成功
             * BUSY 对方忙碌
             * DIS_ONLINE 对方不在线
             * ERR 其他错误
             * ERR_SERVER 服务端错误
             */
            SUCCESS, BUSY, DIS_ONLINE, ERR, ERR_SERVER
        }

        private DirectCallStatusType callStatus;

        public VideoDirectCallResult(DirectCallStatusType callStatus) {
            this.callStatus = callStatus;
        }

        public DirectCallStatusType getCallStatus() {
            return callStatus;
        }
    }

}
