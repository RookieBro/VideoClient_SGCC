package com.telecomyt.videolibrary.bean;

/**
 * Created by Administrator on 2017/7/12.
 */

public class VidyoShowComingCall {

    public String name;
    public boolean busy;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VidyoShowComingCall(String name,boolean busy) {
        this.name = name;
        this.busy = busy;
    }
}
