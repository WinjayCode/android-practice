package com.winjay.practice.hardware_test.tp_test;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;

/**
 * 屏幕划线测试
 *
 * @author Winjay
 * @date 2022-07-27
 */
public class TPTestActivity extends BaseActivity {

    @BindView(R.id.tp_test_view)
    TPTestView tp_test_view;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tp_test;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tp_test_view.setTestSuccessListener(() -> toast("测试OK！"));
    }
}
