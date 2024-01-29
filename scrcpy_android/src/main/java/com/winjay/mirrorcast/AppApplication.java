package com.winjay.mirrorcast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.winjay.mirrorcast.util.ActivityListUtil;
import com.winjay.mirrorcast.util.LogUtil;

public class AppApplication extends Application {
    private static final String TAG = AppApplication.class.getSimpleName();
    public static Context context;
    private ActivityLifeCycle mActivityLifeCycle;

    // 1.WIFI-Direct 2.AOA
    public static int connectType = 0;

    public static String destDeviceIp;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        mActivityLifeCycle = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(mActivityLifeCycle);
    }

    public static class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//            LogUtil.d(TAG, "activity=" + activity.getClass().getSimpleName());
            ActivityListUtil.addActivity(activity);
//            LogUtil.d(TAG, "activity.size=" + ActivityListUtil.getActivityCount());
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
//            LogUtil.d(TAG, "activity=" + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
//            LogUtil.d(TAG, "activity=" + activity.getClass().getSimpleName());
            ActivityListUtil.removeActivity(activity);
//            LogUtil.d(TAG, "activity.size=" + ActivityListUtil.getActivityCount());
        }
    }
}
