package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * @author lbx
 * @date 2018/1/17.
 */

public class DirectCallTimeOutHelper {

    @SerializedName("apiName")
    public String apiName = "receiveTimeOutMsg";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("identity_card")
    private String identity_card;
    @SerializedName("called_message_id")
    private String called_message_id;

    public DirectCallTimeOutHelper(String gzbId) {
        this.identity_card = Config.UserInfo.getVideoUserName();
        this.called_message_id = gzbId;
    }
}
