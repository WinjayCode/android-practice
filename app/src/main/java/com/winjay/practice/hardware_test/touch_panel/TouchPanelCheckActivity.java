package com.winjay.practice.hardware_test.touch_panel;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;

/**
 * 触摸面板测试
 *
 * @author Winjay
 * @date 2022-07-27
 */
public class TouchPanelCheckActivity extends BaseActivity {

    @BindView(R.id.touch_panel_check_view)
    TouchPanelView touch_panel_check_view;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_touch_panel_check;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        touch_panel_check_view.setTestSuccessListener(() -> toast("测试OK！"));
    }
}
