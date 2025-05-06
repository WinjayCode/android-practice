package com.winjay.practice.crash;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * 崩溃信息手机测试
 *
 * @author Winjay
 * @date 2021-08-28
 */
public class CrashTestActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.crash_test_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.crash_test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crashTest();
            }
        });
    }

    void crashTest() {
        throw new RuntimeException("崩溃测试，这是app自己抛出的异常！");
    }
}
