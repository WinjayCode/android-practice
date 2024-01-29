package com.winjay.mirrorcast.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.mirrorcast.util.HandlerManager;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.wifidirect.LoadingDialog;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Common Activity
 *
 * @author Winjay
 * @date 2019-08-24
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "BaseActivity";

    private final int RC_PERMISSION = 100;

    protected abstract View viewBinding();

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFullScreen()) {
            fullScreen();
        }

        if (viewBinding() != null) {
            setContentView(viewBinding());
        }

        if (!hasPermissions()) {
            requestPermissions();
        }
    }

    public boolean isFullScreen() {
        return true;
    }

    private void fullScreen() {
//        // action bar
//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        // window fullscreen
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        // 默认情况，全屏页面不可用刘海区域，非全屏页面可以进行使用
//        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT = 0;
//        // 允许页面延伸到刘海区域
//        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1;
//        // 不允许使用刘海区域
//        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER = 2;
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        getWindow().setAttributes(lp);

        if (Build.VERSION.SDK_INT >= 19) {
            int flags =
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.INVISIBLE;

            getWindow().getDecorView().setSystemUiVisibility(flags);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (isFullScreen()) {
//            if (hasFocus) {
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            }
//        }
//    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideBottomNav() {
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.GONE);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * 沉浸效果
     */
    public void translucentStatusAndNavigation() {
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    protected void dialogToast(String text) {
        if (!TextUtils.isEmpty(text)) {
//            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            showLoadingDialog(text);
            HandlerManager.getInstance().postDelayedOnMainThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                }
            }, 1000);
        }
    }

    protected void toast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show(message, true, false);
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    protected void cancelLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.cancel();
        }
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void goHome() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    protected String[] permissions() {
        return new String[]{};
    }

    protected void permissionGranted() {
    }

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
                EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分功能使用！", RC_PERMISSION, permissions());
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
