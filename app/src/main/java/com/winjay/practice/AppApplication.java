package com.winjay.practice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // multiDex方案三选一即可
        // 1.manifest文件中指定Application为MultiDexApplication(android:name="androidx.multidex.MultiDexApplication")
        // 2.应用的Application继承MultiDexApplication
        // 3.重写attachBaseContext方法，并且调用MultiDex.install(this);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityLifeCycle = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(mActivityLifeCycle);

        //注册App生命周期观察者
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleObserver());

        // 设置异常处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
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

    /**
     * Application生命周期观察，提供整个应用进程的生命周期
     *
     * Lifecycle.Event.ON_CREATE只会分发一次，Lifecycle.Event.ON_DESTROY不会被分发。
     *
     * 第一个Activity进入时，ProcessLifecycleOwner将分派Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME。
     * 而Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP，将在最后一个Activit退出后后延迟分发。如果由于配置更改而销毁并重新创建活动，则此延迟足以保证ProcessLifecycleOwner不会发送任何事件。
     *
     * 作用：监听应用程序进入前台或后台
     */
    private static class ApplicationLifecycleObserver implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        private void onAppForeground() {
            LogUtil.w(TAG, "ApplicationObserver: app moved to foreground");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        private void onAppBackground() {
            LogUtil.w(TAG, "ApplicationObserver: app moved to background");
        }
    }
}
