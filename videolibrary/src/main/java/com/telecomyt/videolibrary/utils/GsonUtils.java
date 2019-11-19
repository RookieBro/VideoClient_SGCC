package com.telecomyt.videolibrary.utils;

import com.google.gson.Gson;

/**
 * Created by lbx on 2017/9/20.
 */

public class GsonUtils {

    private static GsonUtils mGsonUtils;
    private static Gson mGson;

    public static GsonUtils getInstance() {
        if (mGsonUtils == null) {
            synchronized (GsonUtils.class) {
                if (mGsonUtils == null) {
                    mGsonUtils = new GsonUtils();
                }
            }
        }
        return mGsonUtils;
    }

    private GsonUtils() {
    }

    private Gson getGson() {
        return mGson == null ? mGson = new Gson() : mGson;
    }

    public String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    public <T> T fromJson(String json, Class<? extends T> clazz) {
        return getGson().fromJson(json, clazz);
    }
}
