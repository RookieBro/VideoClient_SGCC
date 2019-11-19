package com.telecomyt.videolibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lover on 2016/4/21.
 */
public class SpUtil {

    public static void putBoolean(String key, boolean b) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, b).commit();
    }

    public static String putString(String key, String s) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key, s).commit();
        return s;
    }

    public static void putInt(String key, int i) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key, i).commit();
    }

    public static void putLong(String key, long i) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putLong(key, i).commit();
    }

    public static boolean getBoolean(String key, boolean b) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key, b);
    }

    public static String getString(String key, String s) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, s);
    }

    public static int getInt(String key, int i) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key, i);
    }

    public static long getLong(String key, long i) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getLong(key, i);
    }


    public static void deleteString( String key) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    public static void deleteInt(String key) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
}
