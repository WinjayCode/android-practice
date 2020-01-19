package com.winjay.practice.design_mode;

import android.content.Intent;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.design_mode.mvc.controller.MVCActivity;
import com.winjay.practice.design_mode.mvp.view.MVPActivity;
import com.winjay.practice.design_mode.mvvm.MVVMActivity;

import butterknife.OnClick;

/**
 * Android架构设计模式
 *
 * @author Winjay
 * @date 2020-01-15
 */
public class DesignModeActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.design_mode_activity;
    }

    @OnClick(R.id.mvc_mode_btn)
    public void mvc() {
        Intent intent = new Intent(this, MVCActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mvp_mode_btn)
    public void mvp() {
        Intent intent = new Intent(this, MVPActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mvvm_mode_btn)
    public void mvvm() {
        Intent intent = new Intent(this, MVVMActivity.class);
        startActivity(intent);
    }
}
