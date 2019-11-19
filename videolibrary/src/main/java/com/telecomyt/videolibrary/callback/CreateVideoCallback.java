package com.telecomyt.videolibrary.callback;

import com.telecomyt.videolibrary.bean.GzbVideoUser;

/**
 * Created by lbx on 2017/9/26.
 */

public interface CreateVideoCallback {

    void selectUserClick();

    GzbVideoUser[] addVideoRoomUser(String title, String dec, int videoRoomType, long startTime, long endTime);

    void createSuccess(String title, String dec, int videoRoomType, long startTime, long endTime);
}
