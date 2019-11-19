package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.bean.RoomInfo;

import java.io.Serializable;

/**
 * @author lbx
 * @date 2017/9/11.
 */

public class JoinGzbVideoRoomRequest implements Serializable {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    //roomkey
    @SerializedName("result")
    public RoomInfo roomInfo;

}
