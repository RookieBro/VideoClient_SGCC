package com.telecomyt.videolibrary.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by lbx on 2017/9/7.
 * Activity管理类  收到注销时 结束所有Activity
 */
public class ActivityUtil {

    public static ArrayList<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null && runningTaskInfos.size() > 0)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return null;
    }

    public static boolean activityExit(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        return null != pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }
}
