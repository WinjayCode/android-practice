package com.winjay.practice.intent_filter;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * 隐式启动activity
 *
 * @author Winjay
 * @date 2019-08-24
 */
public class IntentFilterActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.intent_filter_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
