package com.telecomyt.videolibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author lbx
 */
public class RoomInfo implements Serializable {

    private String meetingAuthor;
    private String creatorName;
    private String roomKey;
    private String meetingName;
    private List<User> meetingUidInfos;

    public String getMeetingAuthor() {
        return meetingAuthor;
    }

    public void setMeetingAuthor(String meetingAuthor) {
        this.meetingAuthor = meetingAuthor;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public List<User> getMeetingUidInfos() {
        return meetingUidInfos;
    }

    public void setMeetingUidInfos(List<User> meetingUidInfos) {
        this.meetingUidInfos = meetingUidInfos;
    }

    public class User implements Serializable {
        private String avatar;
        private String custom_id;
        private String name;
        private String status;
        private String user_id;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCustom_id() {
            return custom_id;
        }

        public void setCustom_id(String custom_id) {
            this.custom_id = custom_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

}