package com.telecomyt.videolibrary.bean;

/**
 * Created by Administrator on 2017/7/12.
 */

public class VidyoMessage {

    public String name;
    public String msg;
    public long time;
    public int isMine;  //0false  1true

    public VidyoMessage(String name, String msg, long time, int isMine) {
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.isMine = isMine;
    }
}
