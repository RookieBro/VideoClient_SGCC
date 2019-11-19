package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by lbx on 2017/9/11.
 */

public class GzbVideoDirectCallHelper {

    @SerializedName("userName")
    public String userName;
    @SerializedName("callUserID")
    public String callUserID;
    @SerializedName("apiName")
    public String apiName = "videoDirectCall";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_0;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;

    public GzbVideoDirectCallHelper(String userName,String callUserGzbID) {
        this.userName = userName;
        this.callUserID = callUserGzbID;
    }
}
