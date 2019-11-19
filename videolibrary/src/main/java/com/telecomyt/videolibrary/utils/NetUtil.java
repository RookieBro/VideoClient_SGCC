package com.telecomyt.videolibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lbx on 2017/10/15.
 */

public class NetUtil {

    public static boolean netIsConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) return false;
        return netInfo.isConnected();
    }
}
