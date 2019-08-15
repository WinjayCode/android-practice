package com.winjay.practice;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class TestJava {
    private static List<Activity> activityList = new ArrayList<>();

    public static void main(String args[]) {
        addActivity(null);
        addActivity(null);
        removeActivity(null);
        removeActivity(null);
        System.out.println("size=" + getActivityCount());
    }

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
