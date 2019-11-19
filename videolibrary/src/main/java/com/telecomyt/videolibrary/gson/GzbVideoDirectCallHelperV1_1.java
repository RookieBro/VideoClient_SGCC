package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 *
 * @author lbx
 * @date 2017/9/11
 */

public class GzbVideoDirectCallHelperV1_1 {

    @SerializedName("userName")
    public String userName;
    @SerializedName("callUserID")
    public String callUserID;
    @SerializedName("personOrga")
    public String personOrga;
    @SerializedName("imei")
    public String imei;
    @SerializedName("apiName")
    public String apiName = "videoDirectCall";
    @SerializedName("version")
    public String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_1;
    @SerializedName("terminalType")
    public int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;

    public GzbVideoDirectCallHelperV1_1(String userName, String callUserGzbID,String personOrga,String imei) {
        this.userName = userName;
        this.callUserID = callUserGzbID;
        this.personOrga = personOrga;
        this.imei = imei;
    }
}
