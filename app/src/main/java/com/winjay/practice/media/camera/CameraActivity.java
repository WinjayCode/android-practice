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

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Camera
 *
 * @author Winjay
 * @date 2020/9/30
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private Context mContext;

    /**
     * 权限相关
     */
    private static final int GETPERMS = 100;
    private String[] perms;
    private Handler permissionsHandler = new Handler();

    @BindView(R.id.surfaceview)
    SurfaceView previewView;
    @BindView(R.id.img_pic)
    ImageView imgPic;
    @BindView(R.id.tv_pic_dir)
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
        perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        checkPermission();
    }

    public void checkPermission() {
        //判断是否有相关权限，并申请权限
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            permissionsHandler.post(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this, perms, GETPERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init() {
        LogUtil.d(TAG, "init()");
        manager = new CameraTakeManager(this, previewView, new CameraTakeManager.CameraTakeListener() {
            @Override
            public void onSuccess(File bitmapFile, Bitmap mBitmap) {
                LogUtil.d(TAG, "onSuccess()_bitmapFile=" + bitmapFile.getPath());
                imgPic.setImageBitmap(mBitmap);
                tvPicDir.setText("图片路径：" + bitmapFile.getPath());
            }

            @Override
            public void onFail(String error) {
                LogUtil.e(TAG, "onFail()_error=" + error);
            }
        });
    }

    @OnClick({R.id.btn_take_photo})
    public void onClick(View view) {
        LogUtil.d(TAG, "takePhoto()");
        manager.takePhoto();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroy();
    }
}
