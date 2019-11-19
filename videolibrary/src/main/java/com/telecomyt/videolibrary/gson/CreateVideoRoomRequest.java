package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lbx on 2017/9/26.
 */

public class CreateVideoRoomRequest {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("result")
    public Result result;

    public static class Result {

        public String roomKey;
        public String meetingName;
        public String meetingID;
        public String meetingAuthor;//用户创建者工作宝ID
        public List<User> meetingUidInfos;

        public static class User {

            public String userHeader;
            public String userNickName;
            public String userId;

        }
    }

}
