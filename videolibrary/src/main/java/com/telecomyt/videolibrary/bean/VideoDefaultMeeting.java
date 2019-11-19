package com.telecomyt.videolibrary.bean;

/**
 * @author lbx
 * @date 2017/10/13.
 */

public class VideoDefaultMeeting {

    public String meetingName;
    public String roomKey;
    public String meetingID;

    public VideoDefaultMeeting(String meetingName, String roomKey, String meetingID) {
        this.meetingName = meetingName;
        this.roomKey = roomKey;
        this.meetingID = meetingID;
    }
}
