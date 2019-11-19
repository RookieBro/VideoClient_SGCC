package com.telecomyt.videolibrary.manager;

import android.content.Context;

import com.telecomyt.videolibrary.bean.VideoDefaultMeeting;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.ui.activity.VideoActivity;

/**
 * @author lbx
 * @date 2018/2/5.
 */

public class VideoRoomJoinClickManager {

    public void onDefaultVideoClick(Context context, VideoDefaultMeeting info) {
        VideoActivity.start(context, info.roomKey, info.meetingName, false);
    }

    public void onPersonalVideoClick(Context context, GetVideoRoomRequest.Result.PersonalMeeting personalMeeting) {
        VideoActivity.start(context, personalMeeting);
    }

    public void onCreateVideoClick(Context context, String roomKey, String title) {
        VideoActivity.start(context, roomKey, title, true);
    }

}
