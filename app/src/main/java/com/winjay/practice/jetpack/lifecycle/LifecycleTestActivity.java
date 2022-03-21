package com.winjay.practice.jetpack.lifecycle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

public class LifecycleTestActivity extends AppCompatActivity {
    private static final String TAG = "LifecycleTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);
        setContentView(R.layout.activity_lifecycle_test);
        //Lifecycle 生命周期
        getLifecycle().addObserver(new MyLifecycleObserver());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}

