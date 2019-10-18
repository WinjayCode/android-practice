package com.winjay.practice;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.winjay.practice.utils.LogUtil;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 透明Activity
 *
 * @author Winjay
 * @date 2019-09-19
 */
public class TransparentActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
        // 设置透明沉浸状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            // 使背景图与状态栏融合到一起，这里需要在setcontentview前执行
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        setContentView(R.layout.activity_main);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
    }
}
