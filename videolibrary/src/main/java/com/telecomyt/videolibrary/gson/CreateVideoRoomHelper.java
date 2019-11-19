package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.bean.UserEntityInfo;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

import java.util.Arrays;

/**
 * Created by lbx on 2017/9/26.
 */

public class CreateVideoRoomHelper {

    @SerializedName("apiName")
    private String apiName = "createVideoRoom";
    @SerializedName("meetingStartTime")
    private String meetingStartTime;
    @SerializedName("meetingEndTime")
    private String meetingEndTime;
    @SerializedName("userName")
    private String userName;
    @SerializedName("meetingName")
    private String meetingName;
    @SerializedName("meetingMessage")
    private String meetingMessage;
    @SerializedName("meetingUids")
    private String[] meetingUids;
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;

    @SerializedName("personOrga")
    private String personOrga;
    @SerializedName("imei")
    private String imei;


    public CreateVideoRoomHelper(String meetingName, String meetingMessage, String meetingStartTime, String meetingEndTime, String[] meetingUids) {
        this.meetingStartTime = meetingStartTime;
        this.meetingName = meetingName;
        this.meetingMessage = meetingMessage;
        this.meetingEndTime = meetingEndTime;
        this.userName = Config.UserInfo.getVideoUserName();
        this.meetingUids = meetingUids;
        UserEntityInfo userEntityInfo = Config.UserInfo.getUserEntityInfo();
        personOrga = userEntityInfo.getAreaId();
        imei = userEntityInfo.getImei();
    }

    @Override
    public String toString() {
        return "CreateVideoRoomHelper{" +
                "apiName='" + apiName + '\'' +
                ", meetingStartTime='" + meetingStartTime + '\'' +
                ", meetingEndTime='" + meetingEndTime + '\'' +
                ", userName='" + userName + '\'' +
                ", meetingName='" + meetingName + '\'' +
                ", meetingMessage='" + meetingMessage + '\'' +
                ", meetingUids=" + Arrays.toString(meetingUids) +
                ", version='" + version + '\'' +
                ", terminalType=" + terminalType +
                ", personOrga='" + personOrga + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
