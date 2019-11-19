package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 *
 * @author lbx
 * @date 2017/9/29
 */

public class GetVideoParticipantsHelper {

    @SerializedName("apiName")
    public String apiName = "getMeetingParticipants";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("roomKey")
    public String roomKey;

    public GetVideoParticipantsHelper(String roomKey) {
        this.roomKey = roomKey;
    }
}
