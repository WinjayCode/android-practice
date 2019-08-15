package com.winjay.practice;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.winjay.practice.utils.ActivityListUtil;

/**
 * Application
 *
 * @author winjay
 * @date 2019-08-15
 */
public class AppApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityListUtil.addActivity(activity);
                Log.d(TAG, "onActivityCreated()_activity.size=" + ActivityListUtil.getActivityCount());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "onActivityResumed()_topActivity=" + ActivityListUtil.getTopActivity().getClass().getSimpleName());
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
                ActivityListUtil.removeActivity(activity);
                Log.d(TAG, "onActivityDestroyed()_activity.size=" + ActivityListUtil.getActivityCount());
            }
        });
    }
}
