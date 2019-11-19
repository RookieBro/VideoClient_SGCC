package com.telecomyt.videolibrary.callback;

/**
 * Created by lbx on 2017/9/7.
 */

public abstract class VideoLoginCallback {

    public abstract void loginSuccess();

    public abstract void loginFailed(String err);

    public abstract void permissionErr(String err);

    public abstract void netErr(String err);

}
