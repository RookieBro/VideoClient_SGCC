package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/9/28.
 */

public class DeleteVideoRoomHelper {

    @SerializedName("apiName")
    private String apiName = "deleteMeeting";
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("meetingID")
    private String meetingID;
    @SerializedName("userName")
    private String userName;

    public DeleteVideoRoomHelper(String meetingID) {
        this.meetingID = meetingID;
        this.userName =  Config.UserInfo.getVideoUserName();
    }
}
