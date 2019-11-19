package com.telecomyt.videolibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lbx on 2017/9/8.
 * 会议室信息
 */

public class VideoRoomInfo implements Serializable {

    public String subject;
    public String desc;
    public String startTime;
    public String endTime;
    public String roomKey;
    public List<Participant> participants;

    public VideoRoomInfo(String subject, String desc, String startTime, String endTime, String roomKey, List<Participant> participants) {
        this.subject = subject;
        this.desc = desc;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomKey = roomKey;
        this.participants = participants;
    }
}
