package com.telecomyt.videolibrary.callback;

import com.telecomyt.videolibrary.gson.JoinGzbVideoRoomRequest;

import java.io.Serializable;

/**
 * Created by lbx on 2017/9/28.
 */

public abstract class CreateRoomCallBack implements Serializable{

    public abstract void success(JoinGzbVideoRoomRequest request);

    public abstract void err(String err);
}
