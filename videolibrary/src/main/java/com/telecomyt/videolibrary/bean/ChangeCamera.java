package com.telecomyt.videolibrary.bean;

public class ChangeCamera {

    private int usedCamera;

    public ChangeCamera(int usedCamera){
        this.usedCamera = usedCamera;
    }

    public int getUsedCamera() {
        return usedCamera;
    }

    public void setUsedCamera(int usedCamera) {
        this.usedCamera = usedCamera;
    }
}
