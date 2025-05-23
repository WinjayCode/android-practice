package com.winjay.practice.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.ToastUtil;

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

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
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

        if (isFullScreen()) {
            fullScreen();
        }

        if (useViewBinding()) {
            if (viewBinding() != null) {
                setContentView(viewBinding());
            }
        } else {
            if (getLayoutId() != -1) {
                setContentView(getLayoutId());
            }
        }

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), getActivityResultCallback());

        setTitle(getClass().getSimpleName().replace("Activity", ""));

        requestPermissions();


        // 延迟关闭启动画面SplashScreen
        /*View contentView = findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isDataReady) {//判断是否可以关闭启动动画，可以则返回true
                    contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return isDataReady;
            }
        });
        Thread.sleep(5000); //模拟耗时
        isDataReady = true;*/
    }

    public boolean isFullScreen() {
        return false;
    }

    private void fullScreen() {
        // action bar
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        // window fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 默认情况，全屏页面不可用刘海区域，非全屏页面可以进行使用
        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT = 0;
        // 允许页面延伸到刘海区域
        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1;
        // 不允许使用刘海区域
        // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER = 2;
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFullScreen()) {
            if (hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    // use viewBinding
    public boolean useViewBinding() {
        return false;
    }

    public View viewBinding() {
        return null;
    }

    // use findViewById
    protected int getLayoutId() {
        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    protected void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            ToastUtil.show(this, text);
        }
    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);

        /*KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardLocked()) {
            if (keyguardManager.isKeyguardSecure()) {
//                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
//                keyguardLock.disableKeyguard();

                // 如果需要密码解锁，调起解锁页面
//                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
//                startActivityForResult(intent, REQUEST_CODE_UNLOCK);
//                startActivity(intent);

                keyguardManager.requestDismissKeyguard(this, null);
            } else {
                // 如果不需要密码，直接解锁
                keyguardManager.requestDismissKeyguard(this, null);
            }
        } else {
            // 直接启动下一个 Activity
            startActivity(new Intent(this, cls));
        }*/
    }

    public void startActivityForResult(Intent intent) {
        activityResultLauncher.launch(intent);
    }

    protected ActivityResultCallback<ActivityResult> getActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
            }
        };
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
