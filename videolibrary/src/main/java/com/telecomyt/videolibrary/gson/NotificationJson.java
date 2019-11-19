package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 推送相关
 */

public class NotificationJson {

    @SerializedName("msgType")
    public int msgType;//1 普通会议 2 群组 3 创建行动小组 4 公告 5 临时会议 6 结束行动小组
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public class Data {
        public String content;
        public String title;
        public String noticeID;
        public String actionGroup;

        @Override
        public String toString() {
            return "Data{" +
                    "content='" + content + '\'' +
                    ", title='" + title + '\'' +
                    ", noticeID='" + noticeID + '\'' +
                    ", actionGroup='" + actionGroup + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NotificationJson{" +
                "msgType=" + msgType +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
