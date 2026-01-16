package com.winjay.practice.test_android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.LogUtil;

public class TestService extends Service {
    private final String TAG = TestService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TestBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate()");

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(TestService.this, CardViewActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }, 2000);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent2 = new Intent(TestService.this, TestActivity.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent2);
//            }
//        }, 4000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand()_startId=" + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    public void testFun() {
        LogUtil.d(TAG);
    }

    // 用来和绑定端交互
    public class TestBinder extends Binder {
        private static final String TAG = "TestBinder";

        public void testFun1() {
            LogUtil.d(TAG);
        }

        public void testFun2() {
            LogUtil.d(TAG);
        }

        public TestService getService() {
            return TestService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
    }
}
