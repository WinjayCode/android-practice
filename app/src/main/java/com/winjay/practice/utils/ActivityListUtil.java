package com.winjay.practice.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity集合
 *
 * @author Winjay
 * @date 2019-08-15
 */
public class ActivityListUtil {
    private static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static Activity getTopActivity() {
        if (activityList.size() > 0) {
            return activityList.get(0);
        }
        return null;
    }

    public static int getActivityCount() {
        return activityList.size();
    }
}
