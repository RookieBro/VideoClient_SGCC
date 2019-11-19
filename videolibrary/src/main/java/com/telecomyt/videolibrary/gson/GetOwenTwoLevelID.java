package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/10/14.
 */

public class GetOwenTwoLevelID {

    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_1;
    @SerializedName("apiName")
    private String apiName = "getSecondCode";
    @SerializedName("userName")
    private String userName;

    public GetOwenTwoLevelID() {
        userName =  Config.UserInfo.getVideoUserName();
    }
}
