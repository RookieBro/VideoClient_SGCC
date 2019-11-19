package com.telecomyt.videoclient;

import android.content.Context;
import android.widget.Toast;

import com.telecomyt.videolibrary.bean.VideoDefaultMeeting;
import com.telecomyt.videolibrary.gson.GetVideoRoomRequest;
import com.telecomyt.videolibrary.manager.VideoRoomJoinClickManager;

/**
 * @author lbx
 * @date 2018/2/5.
 */

public class MyVideoRoomJoinClickManager extends VideoRoomJoinClickManager {

    private boolean voiping;

    /**
     * 默认会议室
     */
    @Override
    public void onDefaultVideoClick(Context context, VideoDefaultMeeting info) {
        voiping = true;
        if (!voiping) {
            super.onDefaultVideoClick(context, info);
        } else {
            Toast.makeText(context, "正在通话", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 普通创建的会议室
     */
    @Override
    public void onPersonalVideoClick(Context context, GetVideoRoomRequest.Result.PersonalMeeting personalMeeting) {
        voiping = false;
        if (!voiping) {
            super.onPersonalVideoClick(context, personalMeeting);
        } else {
            Toast.makeText(context, "正在通话", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建完成会议后的回调
     */
    @Override
    public void onCreateVideoClick(Context context, String roomKey, String title) {
        if (!voiping) {
            super.onCreateVideoClick(context, roomKey, title);
        } else {
            Toast.makeText(context, "正在通话", Toast.LENGTH_SHORT).show();
        }
    }
}
