package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/9/11.
 */

public class JoinGzbVideoRoomHelper {

    @SerializedName("chatId")
    public String chatId;
    @SerializedName("userName")
    public String userName;
    @SerializedName("meetingName")
    public String meetingName;
    @SerializedName("joinUids")
    public String[] joinUids;
    @SerializedName("apiName")
    public String apiName = "joinVideoMany";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;

    public JoinGzbVideoRoomHelper(String gzbChatRoomId, String meetingName, String[] joinUids) {
        chatId = gzbChatRoomId;
        this.meetingName = meetingName;
        this.joinUids = joinUids;
        userName = Config.UserInfo.getVideoUserName();
    }
}
