package com.telecomyt.videolibrary.gson;


import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.http.VideoHttpConfig;

/**
 * Created by 61096 on 2016/7/6.
 * 向服务器请求查询聊天会议组
 */
public class GetVideoRoomHelper {

    @SerializedName("terminalType")
    private int terminalType = VideoHttpConfig.HTTP_ANDROID_TERMINAL_TYPE;
    @SerializedName("version")
    private String version = VideoHttpConfig.HTTP_ANDROID_VERSION_V1_1;
    @SerializedName("apiName")
    private String apiName = "getVideoRoomList";
    @SerializedName("userName")
    private String userName;
    @SerializedName("twoLevelAreaID")
    private String twoLevelAreaID;
    @SerializedName("meetingType")
    private int meetingType;  // 0 查询即将召开会议按照创建时间排序  1 查询历史会议

    public GetVideoRoomHelper(int meetingType) {
        this.meetingType = meetingType;
        this.twoLevelAreaID =Config.twoLevelAreaID;
        userName =  Config.UserInfo.getVideoUserName();
    }
}
