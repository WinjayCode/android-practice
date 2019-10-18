package com.winjay.practice.common;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * 公共基类Activity
 *
 * @author Winjay
 * @date 2019-08-24
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        // 此种方式针对Activity或FragmentActivity
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏标题栏（针对AppCompatActivity）
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 沉浸效果
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutId();

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
//        hideBottomNav();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideBottomNav() {
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
