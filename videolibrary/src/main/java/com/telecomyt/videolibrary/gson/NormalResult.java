package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lbx on 2017/9/7.
 */

public class NormalResult {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;

}
