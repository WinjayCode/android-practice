package com.winjay.practice.media.camera.camera1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

public class CameraTakeManager {

    Activity activity;
    SurfaceView surfaceView;
    CameraTakeListener listener;

    SurfaceHolder surfaceHolder;

    SurfaceViewCallback surfaceViewCallback;


    public CameraTakeManager(Activity activity, SurfaceView surfaceView, CameraTakeListener listener) {
        this.activity = activity;
        this.surfaceView = surfaceView;
        this.listener = listener;

        surfaceViewCallback = new SurfaceViewCallback(activity, listener);
        initCamera();
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        //在surfaceView中获取holder
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceViewCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 获取相机当前的照片
     */
    public void takePhoto() {
        surfaceViewCallback.takePhoto();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        surfaceViewCallback.switchCamera();
    }

    /**
     * 开关闪光灯
     */
    public void switchFlashlight() {
        surfaceViewCallback.switchFlashlight();
    }

    public void destroy() {
        surfaceViewCallback.destroy();
    }

    /**
     * 图片拍摄回调
     */
    public interface CameraTakeListener {
        void onSuccess(File bitmapFile, Bitmap mBitmap);

        void onFail(String error);
    }
}
