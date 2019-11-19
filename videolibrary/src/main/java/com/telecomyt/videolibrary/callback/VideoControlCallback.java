package com.telecomyt.videolibrary.callback;

/**
 * Created by lbx on 2017/9/8.
 */

public abstract class VideoControlCallback {

    public void personCount(int count) {

    }

    public void signOut() {

    }

    public abstract void callEnded();

    public void comingCall(String name) {

    }

    public void comingCallRefuse() {

    }

    public void comingCallCanceled() {

    }

    public void join() {

    }

    public abstract void callStarted();

    public void cameraSwitch(String name) {

    }
}
