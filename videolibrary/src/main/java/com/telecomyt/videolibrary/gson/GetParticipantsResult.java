package com.telecomyt.videolibrary.gson;

import com.google.gson.annotations.SerializedName;
import com.telecomyt.videolibrary.bean.VideoParticipantBean;

import java.util.List;

/**
 * @author lbx
 * @date 2018/1/24.
 */

public class GetParticipantsResult {
    //0失败 1成功 2没人
    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("entity")
    public List<VideoParticipantBean> participantsList;
}
