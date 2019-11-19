package com.telecomyt.videolibrary.bean;

import com.telecomyt.videolibrary.Config;

import java.io.Serializable;

/**
 * Created by lbx on 2017/9/25.
 */

public class GzbVideoDirect implements Serializable{

    public String userName;
    public String callUserGzbID;
    public String callUserGzbName;

    public GzbVideoDirect(String callUserGzbName,String callUserGzbID) {
        this.userName = Config.UserInfo.getVideoUserName();
        this.callUserGzbName = callUserGzbName;
        this.callUserGzbID = callUserGzbID;
    }
}
