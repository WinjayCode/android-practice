package com.winjay.practice.jetpack.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.winjay.practice.utils.LogUtil;

public class MyLifecycleObserver implements LifecycleObserver {

    private static final String TAG = "MyLifecycleObserver";

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void onResume() {
        LogUtil.d(TAG);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        LogUtil.d(TAG);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtil.d(TAG);
    }
}

