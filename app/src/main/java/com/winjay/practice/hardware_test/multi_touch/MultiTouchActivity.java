package com.winjay.practice.hardware_test.multi_touch;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * Multi-Touch test
 *
 * @author Winjay
 * @date 2022/07/25
 */
public class MultiTouchActivity extends BaseActivity {
    private static final String TAG = "MultiTouchActivity";

    TouchView multi_touch_view;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi_touch;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        multi_touch_view = findViewById(R.id.multi_touch_view);
        multi_touch_view.setPointerCountListener(pointId -> {
            toast((pointId + 1) + "指触控");
        });
    }
}
