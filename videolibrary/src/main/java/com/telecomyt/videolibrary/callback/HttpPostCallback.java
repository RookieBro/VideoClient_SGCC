package com.telecomyt.videolibrary.callback;

/**
 * Created by lbx on 2017/9/7.
 */

public abstract class HttpPostCallback<T> {

    public void onStart() {
    }

    public void onCancelled() {
    }

    public void onLoading(long total, long current, boolean isUploading) {
    }

    public abstract void onSuccess(T msg);

    public abstract void onFailure(String err);

    public void onFinished(boolean isSuccess) {

    }
}
