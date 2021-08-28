package com.winjay.practice.crash;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.OnClick;

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

    @OnClick(R.id.crash_test_btn)
    void crashTest() {
        throw new RuntimeException("崩溃测试，这是app自己抛出的异常！");
    }
}
