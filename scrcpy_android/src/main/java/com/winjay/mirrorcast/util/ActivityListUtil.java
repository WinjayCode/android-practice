package com.winjay.mirrorcast.util;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity集合
 *
 * @author Winjay
 * @date 2019-08-15
 */
public class ActivityListUtil {
    private static Stack<Activity> activityList = new Stack<>();

    public static void addActivity(Activity activity) {
        activityList.push(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static Activity getTopActivity() {
        if (activityList.size() > 0) {
            return activityList.peek();
        }
        return null;
    }

    public static Activity getActivityByIndex(int index) {
        return activityList.get(index);
    }

    public static int getActivityCount() {
        return activityList.size();
    }
}
