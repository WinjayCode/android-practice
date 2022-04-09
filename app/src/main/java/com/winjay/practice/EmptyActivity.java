package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;

/**
 * 空页面
 *
 * @author Winjay
 * @date 2020/4/9
 */
public class EmptyActivity extends AppCompatActivity {
    private static final String TAG = EmptyActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "111");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        LogUtil.d(TAG, "222");
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "111");
        super.onResume();
        LogUtil.d(TAG, "222");
        // 统计布局渲染时间
        final long start = System.currentTimeMillis();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                LogUtil.d(TAG, "onRender cost:" + (System.currentTimeMillis() - start));
                return false;
            }
        });
    }

    private long costTime() {
        long time = 0;
        Intent intent = getIntent();
        if (intent.hasExtra("time")) {
            long startTime = intent.getLongExtra("time", 0);
            time = System.currentTimeMillis() - startTime;
        }
        return time;
    }
}
