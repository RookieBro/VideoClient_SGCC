package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * @author lbx
 * @date 2018/1/15.
 */

public class HeartSend {

    @SerializedName("identity_card")
    public String identity_card;
    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("apiName")
    private String apiName = "receivePackageMsg";

    public HeartSend() {
        this.identity_card = Config.UserInfo.getVideoUserName();
    }
}
