package com.telecomyt.videolibrary.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.VideoState;
import com.telecomyt.videolibrary.callback.HttpPostCallback;
import com.telecomyt.videolibrary.http.Header;

/**
 * Created by lbx on 2017/9/7.
 */
public class HttpUtil {

    private static HttpUtils httpUtils;
    private static HttpUtil httpUtil;
    private static final int TIME_OUT = 1000 * 60;

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                if (httpUtil == null) {
                    httpUtil = new HttpUtil();
                    httpUtils = new HttpUtils();
                    httpUtils.configCurrentHttpCacheExpiry(TIME_OUT);//设置当前网络请求的缓存超时时间
                    httpUtils.configTimeout(TIME_OUT);//设置连接超时时间
                    httpUtils.configSoTimeout(TIME_OUT);//设置连接超时时间
                }
            }
        }
        return httpUtil;
    }

    private HttpUtil() {
    }

    public HttpHandler<String> post(Object obj, final HttpPostCallback<String> listener) {
        return post(Config.getMainUrl(), obj, listener);
    }

    public HttpHandler<String> post(String url, Object obj, final HttpPostCallback<String> listener) {
        if (!NetUtil.netIsConnect(UIUtils.getContext())) {
            if (listener != null) {
                listener.onFailure(VideoState.NETWORk_NONE);
                listener.onFinished(false);
            }
            UIUtils.showToast("网络没有连接，请重新再试！");
            return null;
        }
        String json = makeJson(obj);
        LogUtils.e("发送json = " + json);
        RequestParams params = new RequestParams();
        params.addBodyParameter(Header.requestStr, json);
        return httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (listener != null)
                    listener.onStart();
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                if (listener != null)
                    listener.onCancelled();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                if (listener != null)
                    listener.onLoading(total, current, isUploading);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.e("接收 成功：" + result);
                if (TextUtils.isEmpty(result)) {
                    if (listener != null)
                        listener.onFailure("服务器错误");
                    return;
                }
                if (listener != null) {
                    listener.onSuccess(result);
                    listener.onFinished(true);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                String e = error.toString();
                LogUtils.e("接收 失败：" + Config.getMainUrl() + "    " + e);
                if (listener != null) {
                    listener.onFailure(e);
                    listener.onFinished(false);
                }
            }
        });
    }

    public HttpHandler<String> post(String header, String[] keys, String[] params, final HttpPostCallback<String> listener) {
        return post(Config.getMainUrl(), header, keys, params, listener);
    }

    public HttpHandler<String> post(String url, String header, String[] keys, String[] params, final HttpPostCallback<String> listener) {
        if (!NetUtil.netIsConnect(UIUtils.getContext())) {
            if (listener != null) {
                listener.onFailure(VideoState.NETWORk_NONE);
                listener.onFinished(false);
            }
            UIUtils.showToast("网络没有连接，请重新再试！");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        RequestParams param = new RequestParams();
        for (int i = 0; i < keys.length; i++) {
            param.addBodyParameter(keys[i], params[i]);
            sb.append("|" + keys[i] + "=" + params[i]);
        }
        LogUtils.e("发送数据 = " + sb.toString());
        String s = url.endsWith("/") ? url + header : url + "/" + header;
        return httpUtils.send(HttpMethod.POST, s, param, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (listener != null)
                    listener.onStart();
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                if (listener != null)
                    listener.onCancelled();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                if (listener != null)
                    listener.onLoading(total, current, isUploading);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                LogUtils.e("接收 成功：" + result);
                if (TextUtils.isEmpty(result)) {
                    if (listener != null) {
                        listener.onFailure("服务器错误");
                        listener.onFinished(true);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                String e = error.toString();
                LogUtils.e("接收 失败：" + Config.getMainUrl() + "    " + e);
                if (listener != null) {
                    listener.onFailure(e);
                    listener.onFinished(false);
                }
            }
        });
    }

    @Nullable
    private static String makeJson(Object obj) {
        String json = new Gson().toJson(obj);
        if (!TextUtils.isEmpty(json))
            json = json.replace("\\u003d", "=");
        return json;
    }
}
