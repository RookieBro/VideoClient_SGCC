package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/9/26.
 */

public class UpdateVideoRoomHelper {

    @SerializedName("apiName")
    private String apiName = "updateMeeting";
    @SerializedName("operationType")
    private int operationType;
    @SerializedName("userName")
    private String userName;
    @SerializedName("meetingID")
    private String meetingID;
    @SerializedName("chatName")
    private String chatName;
    @SerializedName("uids")
    private String[] uids;
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;

    public UpdateVideoRoomHelper(String chatName, int operationType, String meetingID, String[] uids) {
        this.chatName = chatName;
        this.operationType = operationType;
        this.meetingID = meetingID;
        this.uids = uids;
        this.userName = Config.UserInfo.getVideoUserName();
    }
}
