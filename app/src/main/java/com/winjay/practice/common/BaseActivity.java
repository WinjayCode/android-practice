package com.winjay.practice.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.ToastUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 公共基类Activity
 *
 * @author Winjay
 * @date 2019-08-24
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder unbinder;
    private final int RC_PERMISSION = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // 使用Activity过渡动画
//        supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

//        getWindow().setEnterTransition(new Slide());
//        getWindow().setExitTransition(new Slide());

//        getWindow().setEnterTransition(new Fade());
//        getWindow().setExitTransition(new Fade());

//        LogUtil.d(TAG, "onCreate()");
        // 此种方式针对Activity或FragmentActivity
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏标题栏（针对AppCompatActivity）
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 沉浸效果
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        unbinder = ButterKnife.bind(this);
    }

    protected abstract int getLayoutId();

    @Override
    protected void onResume() {
        super.onResume();
//        LogUtil.d(TAG, "onResume()");
//        hideBottomNav();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtil.d(TAG, "onDestroy()");
        unbinder.unbind();
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

    protected void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            ToastUtils.show(this, text);
        }
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    protected String[] permissions() {
        return new String[]{};
    }

    protected void permissionGranted() {}

    protected boolean hasPermissions() {
        if (permissions().length > 0) {
            return EasyPermissions.hasPermissions(this, permissions());
        }
        return false;
    }

    @AfterPermissionGranted(RC_PERMISSION)
    public void requestPermissions() {
        if (permissions().length > 0) {
            if (EasyPermissions.hasPermissions(this, permissions())) {
                LogUtil.d(TAG, "Already have permission, do the thing!");
                // Already have permission, do the thing
                permissionGranted();
            } else {
                LogUtil.w(TAG, "Do not have permissions, request them now!");
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能。", RC_PERMISSION, permissions());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.d(TAG);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.d(TAG, "Some permissions have been granted: " + perms.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtil.w(TAG, "Some permissions have been denied: " + perms.toString());
        finish();
    }
    ////////////////////////////////////////// permission end //////////////////////////////////////////
}
