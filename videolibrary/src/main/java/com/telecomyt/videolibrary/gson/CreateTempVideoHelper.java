package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/9/29.
 */

public class CreateTempVideoHelper {

    @SerializedName("apiName")
    public String apiName = "createTempMeeting";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("userName")
    public String userName;
    @SerializedName("meetingString")
    public String meetingString;

    public CreateTempVideoHelper(String meetingString) {
        this.meetingString = meetingString;
        userName =  Config.UserInfo.getVideoUserName();
    }
}
