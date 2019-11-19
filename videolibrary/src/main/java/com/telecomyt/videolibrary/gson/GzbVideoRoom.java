package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;

import java.io.Serializable;

/**
 * Created by lbx on 2017/9/11.
 */

public class GzbVideoRoom implements Serializable {

    @SerializedName("gzbChatRoomId")
    public String gzbChatRoomId;   //chatID
    @SerializedName("gzbChatRoomName")
    public String gzbChatRoomName;
    @SerializedName("videoUserName")
    public String videoUserName;
    @SerializedName("videoPassWord")
    public String videoPassWord;
    @SerializedName("roomKey")
    private String roomKey;//roomKey
    @SerializedName("callName")
    public String callName;
    @SerializedName("comingCallName")
    private String comingCallName;
    @SerializedName("joinUids")
    public String[] joinUids;


    public GzbVideoRoom(String gzbChatRoomId, String gzbChatRoomName, String[] joinUids) {
        this.gzbChatRoomId = gzbChatRoomId;
        this.joinUids = joinUids;
        videoUserName = Config.UserInfo.getVideoUserName();
        this.gzbChatRoomName = gzbChatRoomName;
        videoPassWord = Config.UserInfo.getVideoPassword();
    }

    public void setData(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }


    @Override
    public String toString() {
        return "GzbVideoRoom{" +
                "gzbChatRoomId='" + gzbChatRoomId + '\'' +
                ", callUserGzbName='" + gzbChatRoomName + '\'' +
                ", videoUserName='" + videoUserName + '\'' +
                ", videoPassWord='" + videoPassWord + '\'' +
                ", roomKey='" + roomKey + '\'' +
                '}';
    }

    public String getComingCallName() {
        return comingCallName;
    }

    public void setComingCallName(String comingCallName) {
        this.comingCallName = comingCallName;
    }
}
