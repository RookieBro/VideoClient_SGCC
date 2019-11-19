package com.telecomyt.videolibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.telecomyt.videolibrary.Config;

/**
 * Created by lbx on 2017/9/11.
 */

public class PermissionUtils {

    private static PermissionUtils mPermissionUtils;

    public static PermissionUtils getInstance() {
        if (mPermissionUtils == null) {
            synchronized (PermissionUtils.class) {
                if (mPermissionUtils == null) {
                    mPermissionUtils = new PermissionUtils();
                }
            }
        }
        return mPermissionUtils;
    }

    private PermissionUtils() {

    }

    /**
     * 6.0+申请权限
     */
    public boolean checkPermission(Activity ac) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ac, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.BODY_SENSORS)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ac, Manifest.permission.WAKE_LOCK)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ac, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.BODY_SENSORS}, 0);
                return false;
            }
        }
        Config.GET_ALL_PERMISSION = true;
        return true;
    }
}
