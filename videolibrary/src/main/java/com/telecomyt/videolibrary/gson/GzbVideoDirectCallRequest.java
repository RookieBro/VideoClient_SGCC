package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lbx
 * @date 2017/9/11.
 */

public class GzbVideoDirectCallRequest {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("result")
    public String result;
    @SerializedName("room_key")
    public String roomKey;

}
