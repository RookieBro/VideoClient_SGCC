package com.telecomyt.videolibrary.bean;

import java.io.Serializable;

/**
 * Created by lbx on 2017/9/8.
 * 参会有人员信息
 */

public class Participant implements Serializable{
    public String userName;
    public String imgUrl;
    public String nickName;
    public String uid;

    public Participant(String userName, String imgUrl, String nickName, String uid) {
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.nickName = nickName;
        this.uid = uid;
    }
}
