package com.telecomyt.videolibrary;

import android.os.Environment;

import com.telecomyt.videolibrary.bean.UserEntityInfo;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;

import java.io.File;

/**
 * Created by lbx on 2017/9/7.
 */

public class Config {

    private static String HTTP_START = "http://";
    private static String HTTP_END = "/directory/action";
    private static String MAIN_IP = "192.168.0.16:8080";
    private static String MAIN_URL = HTTP_START + MAIN_IP + HTTP_END;
    private static String VIDEO_URL = "http://ydjw.telecomyt.com.cn";
    public static boolean GET_ALL_PERMISSION;
    public static boolean VIDEO_LOGIN;
    public static boolean VIDEO_START;
    public static String MEETING_MENU = "MEETING_MENU";
    public static String LISTENER_CAN_USED = "LISTENER_CAN_USED";
    public static String MIC_CAN_USED = "MIC_CAN_USED";
    public static String CAMERA_CAN_USED = "CAMERA_CAN_USED";
    public static String MEETING_SIZE = "MEETING_SIZE";
    public static String twoLevelAreaID = "";
    public static int ACTION_BAR_COLOR = R.color.blue;
    public static boolean VIDEO_ACTIVITY_PAUSE;
    public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String DEFAULT_LOG_FILE_PATH = SD_CARD_PATH + File.separator + "VideoClient" + File.separator + "logs";
    public static String DEFAULT_TAT = "videoClient";
    public static GetVideoRoomRequest.Result.PersonalMeeting currentVideoRoom;

    public static boolean VIDEO_SDK_IN = false;
    /**
     * 是否显示水印
     */
    public static boolean showWaterView = true;
    /**
     * 是否发送推送通知
     */
    public static boolean pushAvailable = true;
    /**
     * 是否显示来电弹窗
     */
    public static boolean showDirectCallDialog = true;

    public static String getMainIp() {
        return MAIN_IP;
    }

    public static void setMainIp(String mainIp) {
        MAIN_IP = mainIp;
        MAIN_URL = HTTP_START + MAIN_IP + HTTP_END;
    }

    public static String getMainUrl() {
        MAIN_URL = HTTP_START + MAIN_IP + HTTP_END;
        return MAIN_URL;
    }

    public static String getVideoUrl() {
        return VIDEO_URL;
    }

    public static void setVideoUrl(String videoUrl) {
        VIDEO_URL = videoUrl;
    }

    /**
     * Created by lbx on 2017/9/7.
     */

    public static class UserInfo {

        private static String nickName = "oop";
        private static String videoUserName = "oop";
        private static String videoPassword = "oop";
        private static String token;
        private static String policeId;
        private static UserEntityInfo userEntityInfo = new UserEntityInfo("", "", "", "");

        public static String getNickName() {
            return nickName;
        }

        public static void setNickName(String nickName) {
            UserInfo.nickName = nickName;
        }

        public static String getVideoUserName() {
            return videoUserName;
        }

        public static void setVideoUserName(String videoUserName) {
            UserInfo.videoUserName = videoUserName;
        }

        public static String getVideoPassword() {
            return videoPassword;
        }

        public static void setVideoPassword(String videoPassword) {
            UserInfo.videoPassword = videoPassword;
        }

        public static String getToken() {
            return token;
        }

        public static void setToken(String token) {
            UserInfo.token = token;
        }

        public static String getPoliceId() {
            return policeId;
        }

        public static void setPoliceId(String policeId) {
            UserInfo.policeId = policeId;
        }

        public static UserEntityInfo getUserEntityInfo() {
            return userEntityInfo;
        }

        public static void setUserEntityInfo(UserEntityInfo userEntityInfo) {
            UserInfo.userEntityInfo = userEntityInfo;
        }
    }
}
