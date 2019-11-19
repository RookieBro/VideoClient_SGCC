package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * @author lbx
 * @date 2018/1/17.
 */

public class ReceiveVideoLoginMsgHelper {

    @SerializedName("apiName")
    public String apiName = "receiveVideoLoginMsg";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("identity_card")
    public String identity_card;

    public ReceiveVideoLoginMsgHelper(String identity_card) {
        this.identity_card = identity_card;
    }
}
