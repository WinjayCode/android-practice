package com.winjay.practice;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.winjay.practice.crash.CrashHandler;
import com.winjay.practice.utils.ActivityListUtil;
import com.winjay.practice.utils.LogUtil;

/**
 * Application
 *
 * @author winjay
 * @date 2019-08-15
 */
public class AppApplication extends MultiDexApplication {
    private static final String TAG = AppApplication.class.getSimpleName();
    private ActivityLifeCycle mActivityLifeCycle;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityLifeCycle = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(mActivityLifeCycle);

        // 设置异常处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtil.d(TAG, "onActivityCreated()_activity=" + activity.getClass().getSimpleName());
            ActivityListUtil.addActivity(activity);
            LogUtil.d(TAG, "onActivityCreated()_activity.size=" + ActivityListUtil.getActivityCount());
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtil.d(TAG, "onActivityResumed()_activity=" + activity.getClass().getSimpleName());
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
            LogUtil.d(TAG, "onActivityDestroyed()_activity=" + activity.getClass().getSimpleName());
            ActivityListUtil.removeActivity(activity);
            LogUtil.d(TAG, "onActivityDestroyed()_activity.size=" + ActivityListUtil.getActivityCount());
        }
    }
}
