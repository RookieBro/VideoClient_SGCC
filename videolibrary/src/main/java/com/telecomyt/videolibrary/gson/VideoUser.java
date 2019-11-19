package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lbx on 2017/9/8.
 */

public class VideoUser {

    @SerializedName("name")
    public String name;
    @SerializedName("uid")
    public String uid;

    public VideoUser(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }
}
