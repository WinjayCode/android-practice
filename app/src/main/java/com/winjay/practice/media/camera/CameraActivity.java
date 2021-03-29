package com.winjay.practice.media.camera;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Camera
 *
 * @author Winjay
 * @date 2020/9/30
 */
public class CameraActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private Context mContext;
    private final int RC_PERMISSION = 100;

    @BindView(R.id.preview_sv)
    SurfaceView previewView;

    @BindView(R.id.img_pic)
    ImageView imgPic;

    @BindView(R.id.pic_path_tv)
    TextView tvPicDir;

    CameraTakeManager manager;

    @Override
    protected int getLayoutId() {
        return R.layout.camera_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requiresPermissions();
    }

    private void init() {
        LogUtil.d(TAG);
        manager = new CameraTakeManager(this, previewView, new CameraTakeManager.CameraTakeListener() {
            @Override
            public void onSuccess(File bitmapFile, Bitmap mBitmap) {
                LogUtil.d(TAG, "bitmapFile=" + bitmapFile.getPath());
                imgPic.setImageBitmap(mBitmap);
                tvPicDir.setText("图片路径：" + bitmapFile.getPath());
            }

            @Override
            public void onFail(String error) {
                LogUtil.e(TAG, "error=" + error);
            }
        });
    }

    @OnClick({R.id.btn_take_photo})
    void takePhoto() {
        LogUtil.d(TAG);
        manager.takePhoto();
    }

    @OnClick(R.id.switch_camera_btn)
    void switchCamera() {
        LogUtil.d(TAG);
        manager.switchCamera();
    }

    @OnClick(R.id.switch_flash_btn)
    void switchFlashlight() {
        LogUtil.d(TAG);
        manager.switchFlashlight();
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtil.d(TAG, "Already have permission, do the thing!");
            // Already have permission, do the thing
            init();
        } else {
            LogUtil.w(TAG, "Do not have permissions, request them now!");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能。", RC_PERMISSION, perms);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroy();
    }
}
