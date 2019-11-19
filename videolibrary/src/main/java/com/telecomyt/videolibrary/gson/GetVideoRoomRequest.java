package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.bean.VideoDefaultMeeting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取聊天会议粗 返回结果
 */

public class GetVideoRoomRequest implements Serializable {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("result")
    public Result result;


    public static class Result implements Serializable {

        public List<PersonalMeeting> personalMeeting;
        public List<OneLevelMeeting> oneLevelMeeting;
        public List<TwoLevelMeeting> twoLevelMeeting;
        public List<LeaderLevelMeeting> leaderLevelMeeting;
        public String oneLevelName;
        public String twoLevelName;
        public String leaderLevelName;

        public static class PersonalMeeting implements Serializable {

            public String meetingName;
            public String roomKey;
            public String meetingMessage;
            public String meetingAuthorCardId;
            public String meetingAuthorGzbId;
            public String meetingAuthor;//name
            public String creatorAuthor;//头像
            public String meetingID;
            public List<User> meetingUidInfos;
            public String meetingStartTime;
            public String meetingEndTime;
            public String replayUrl;
            public String duration;
            public long myStartTime;

            public static class User implements Serializable {
                public String userHeader;
                public String userNickName;
                public String userId;

                public User(String userHeader, String userNickName, String userId) {
                    this.userHeader = userHeader;
                    this.userNickName = userNickName;
                    this.userId = userId;
                }
            }

            public PersonalMeeting(String meetingName, String meetingMessage, String roomKey, String meetingAuthorCardId, String meetingAuthorGzbId,
                                   String meetingAuthor, String creatorAuthor, String meetingID, List<User> meetingUidInfos,
                                   String meetingStartTime, String meetingEndTime, String replayUrl, String duration, long myStartTime) {
                this.meetingName = meetingName;
                this.meetingMessage = meetingMessage;
                this.roomKey = roomKey;
                this.meetingAuthorCardId = meetingAuthorCardId;
                this.meetingAuthorGzbId = meetingAuthorGzbId;
                this.meetingAuthor = meetingAuthor;
                this.creatorAuthor = creatorAuthor;
                this.meetingID = meetingID;
                this.meetingUidInfos = meetingUidInfos;
                this.meetingStartTime = meetingStartTime;
                this.meetingEndTime = meetingEndTime;
                this.replayUrl = replayUrl;
                this.duration = duration;
                this.myStartTime = myStartTime;
            }

            public static PersonalMeeting create(CreateVideoRoomRequest room) {
                CreateVideoRoomRequest.Result result = room.result;
                List<CreateVideoRoomRequest.Result.User> users = result.meetingUidInfos;
                List<PersonalMeeting.User> list = new ArrayList<>();
                for (CreateVideoRoomRequest.Result.User u : users) {
                    list.add(new PersonalMeeting.User(u.userHeader, u.userNickName, u.userId));
                }
                return new PersonalMeeting(result.meetingName, "", result.roomKey, Config.UserInfo.getVideoUserName(), result.meetingAuthor, "", "", result.meetingID, list, "", "", "", "", 0);
            }

            @Override
            public String toString() {
                return "PersonalMeeting{" +
                        "meetingName='" + meetingName + '\'' +
                        ", roomKey='" + roomKey + '\'' +
                        ", meetingMessage='" + meetingMessage + '\'' +
                        ", meetingAuthorCardId='" + meetingAuthorCardId + '\'' +
                        ", meetingAuthorGzbId='" + meetingAuthorGzbId + '\'' +
                        ", meetingAuthor='" + meetingAuthor + '\'' +
                        ", creatorAuthor='" + creatorAuthor + '\'' +
                        ", meetingID='" + meetingID + '\'' +
                        ", meetingUidInfos=" + meetingUidInfos +
                        ", meetingStartTime='" + meetingStartTime + '\'' +
                        ", meetingEndTime='" + meetingEndTime + '\'' +
                        ", replayUrl='" + replayUrl + '\'' +
                        ", duration='" + duration + '\'' +
                        ", myStartTime=" + myStartTime +
                        '}';
            }
        }


        public static class TwoLevelMeeting extends VideoDefaultMeeting implements Serializable {

            public TwoLevelMeeting(String meetingName, String roomKey, String meetingID) {
                super(meetingName, roomKey, meetingID);
            }

            public static VideoDefaultMeeting create(TwoLevelMeeting twoLevelMeeting) {
                return new VideoDefaultMeeting(twoLevelMeeting.meetingName, twoLevelMeeting.roomKey, twoLevelMeeting.meetingID);
            }

        }

        public static class OneLevelMeeting extends VideoDefaultMeeting implements Serializable {

            public OneLevelMeeting(String meetingName, String roomKey, String meetingID) {
                super(meetingName, roomKey, meetingID);
            }

            public static VideoDefaultMeeting create(OneLevelMeeting oneLevelMeeting) {
                return new VideoDefaultMeeting(oneLevelMeeting.meetingName, oneLevelMeeting.roomKey, oneLevelMeeting.meetingID);
            }
        }

        public static class LeaderLevelMeeting extends VideoDefaultMeeting implements Serializable {

            public LeaderLevelMeeting(String meetingName, String roomKey, String meetingID) {
                super(meetingName, roomKey, meetingID);
            }

            public static VideoDefaultMeeting create(LeaderLevelMeeting leaderLevelMeeting) {
                return new VideoDefaultMeeting(leaderLevelMeeting.meetingName, leaderLevelMeeting.roomKey, leaderLevelMeeting.meetingID);
            }
        }

    }


}

