package com.winjay.mirrorcast.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

public class HandlerManager {
    private Handler mMainHandler;
    private Handler mSubHandler;
    private HandlerThread mSubHandlerThread;

    private HandlerManager() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mSubHandlerThread = new HandlerThread("mSubHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mSubHandlerThread.start();
        mSubHandler = new Handler(mSubHandlerThread.getLooper());
    }

    private static class InnerHolder {
        private static HandlerManager mInstance = new HandlerManager();
    }

    public static HandlerManager getInstance() {
        return InnerHolder.mInstance;
    }

    public Handler getmMainHandler() {
        return mMainHandler;
    }

    public Handler getSubHandler() {
        return mSubHandler;
    }


    public void postOnMainThread(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    public void postOnSubThread(Runnable runnable) {
        mSubHandler.post(runnable);
    }

    public void postDelayedOnMainThread(Runnable runnable, long delayMillis) {
        mMainHandler.postDelayed(runnable, delayMillis);
    }

    public void postDelayedOnSubThread(Runnable runnable, long delayMillis) {
        mSubHandler.postDelayed(runnable, delayMillis);
    }
}
