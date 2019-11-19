package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lbx on 2017/9/29.
 */

public class CreateTempVideoRequest implements Serializable {

    @SerializedName("message")
    public String message;
    @SerializedName("code")
    public int code;
    @SerializedName("result")
    public Info result;

    public static class Info implements Serializable {

        public String roomKey;
    }

}
