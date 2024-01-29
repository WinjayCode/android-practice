package com.winjay.mirrorcast.app_socket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.util.HandlerManager;
import com.winjay.mirrorcast.util.LogUtil;

import java.util.concurrent.CountDownLatch;

/**
 * @author Winjay
 * @date 2022-11-29
 */
public class AppSocketServerManager {
    private static final String TAG = AppSocketServerManager.class.getSimpleName();
    private static volatile AppSocketServerManager mInstance;
    private AppSocketService.MyBinder myBinder;

    private CountDownLatch mCountDownLatch;

    private AppSocketServerManager() {
        Intent intent = new Intent(AppApplication.context, AppSocketService.class);
        AppApplication.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG);
            myBinder = (AppSocketService.MyBinder) service;
            if (mCountDownLatch != null) {
                actionResume();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG);
            myBinder = null;
        }

        @Override
        public void onNullBinding(ComponentName name) {
            LogUtil.d(TAG);
            myBinder = null;
            if (mCountDownLatch != null) {
                actionResume();
            }
        }
    };

    public static AppSocketServerManager getInstance() {
        if (mInstance == null) {
            synchronized (AppSocketServerManager.class) {
                if (mInstance == null) {
                    mInstance = new AppSocketServerManager();
                }
            }
        }
        return mInstance;
    }

    public void startServer() {
        LogUtil.d(TAG);
        if (mCountDownLatch == null) {
            HandlerManager.getInstance().postOnSubThread(new Runnable() {
                @Override
                public void run() {
                    actionAwait();

                    HandlerManager.getInstance().postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            startServer();
                        }
                    });
                }
            });
            return;
        }

        if (myBinder != null) {
            myBinder.getService().startServer();
        }
    }

    public void setAppSocketServerListener(AppSocketServer.OnAppSocketServerListener listener) {
        if (myBinder != null) {
            myBinder.getService().setAppSocketServerListener(listener);
        }
    }

    public void stopServer() {
        if (myBinder != null) {
            myBinder.getService().stopServer();
        }

        mCountDownLatch = null;

        if (myBinder != null) {
            try {
                AppApplication.context.unbindService(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (myBinder != null) {
            myBinder.getService().sendMessage(message);
        }
    }

    private void actionAwait() {
        LogUtil.d(TAG);
        mCountDownLatch = new CountDownLatch(1);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void actionResume() {
        if (mCountDownLatch != null) {
            LogUtil.d(TAG);
            mCountDownLatch.countDown();
        }
    }
}
