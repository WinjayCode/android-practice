package com.winjay.practice.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

/**
 * 透明Activity
 *
 * @author Winjay
 * @date 2019-09-19
 */
public class TransparentActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

//    @Override
//    protected int getLayoutId() {
//        return R.layout.transparent_activity;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);

        //设置1像素
//        Window window = getWindow();
//        window.setGravity(Gravity.LEFT | Gravity.TOP);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.x = 0;
//        params.y = 0;
//        params.height = 1;
//        params.width = 1;
//        window.setAttributes(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
