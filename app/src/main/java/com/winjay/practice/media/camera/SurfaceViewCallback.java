package com.winjay.practice.media.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.winjay.practice.utils.BitmapUtil;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class SurfaceViewCallback implements SurfaceHolder.Callback {
    private static final String TAG = SurfaceViewCallback.class.getSimpleName();
    private Activity activity;

    Camera mCamera;

    private int mCurrentCamIndex;

    private int mFrontCameraId;
    private int mBackCameraId;

    private int surfaceViewWidth;
    private int surfaceViewHeight;
    private SurfaceHolder surfaceHolder;

    boolean previewing;

    private boolean hasSurface;

    /**
     * 为true时则开始捕捉照片
     */
    boolean canTake;

    private String savePath;

    private boolean isFlashOpen;

    /**
     * 拍照回调接口
     */
    CameraTakeManager.CameraTakeListener listener;

    public SurfaceViewCallback(Activity activity, CameraTakeManager.CameraTakeListener listener) {
        previewing = false;
        hasSurface = false;

        this.activity = activity;
        this.listener = listener;

        getCameraInfo();
        savePath = activity.getExternalCacheDir() + File.separator + "camera" + File.separator;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG);
        surfaceHolder = holder;
        if (!hasSurface) {
            hasSurface = true;
            openCamera(mBackCameraId);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.d(TAG, "width=" + width + ", height=" + height);
        surfaceViewWidth = width;
        surfaceViewHeight = height;

        if (previewing) {
            mCamera.stopPreview();
            previewing = false;
        }

        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG);
        if (!previewing) {
            return;
        }
        holder.removeCallback(this);
        closeCamera();
    }

    private void getCameraInfo() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        LogUtil.d(TAG, "cameraCount=" + cameraCount);
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = camIdx;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = camIdx;
            }
        }
    }

    /**
     * 打开摄像头
     */
    private void openCamera(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            mCurrentCamIndex = cameraId;
        } catch (RuntimeException e) {
            LogUtil.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
        }
        if (mCamera == null) {
            LogUtil.e(TAG, "没有可用的摄像头!");
            if (listener != null) {
                listener.onFail("没有可用的摄像头");
            }
            return;
        }
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
//                LogUtil.i(TAG, "onPreviewFrame " + canTake);
                if (canTake) {
                    getSurfacePic(bytes, camera);
                    canTake = false;
                }
            }
        });
    }

    private void closeCamera() {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.lock();
        mCamera.release();
        mCamera = null;
    }

    private void startPreview() {
        // 配置camera参数
        initPreviewParams(surfaceViewWidth, surfaceViewHeight);
        // 设置预览 SurfaceHolder
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 开始显示
        mCamera.startPreview();
        previewing = true;
        // 调整预览图像方向
        setCameraDisplayOrientation(activity, mCurrentCamIndex);
    }

    private void initPreviewParams(int shortSize, int longSize) {
        Camera.Parameters parameters = mCamera.getParameters();
        //获取手机支持的尺寸
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = getBestSize(shortSize, longSize, sizes);
        //设置预览大小
        parameters.setPreviewSize(bestSize.width, bestSize.height);
        //设置图片大小，拍照
        parameters.setPictureSize(bestSize.width, bestSize.height);
        //设置格式,所有的相机都支持 NV21格式
        parameters.setPreviewFormat(ImageFormat.NV21);
        //设置聚焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        mCamera.setParameters(parameters);
    }

    /**
     * 获取预览最后尺寸
     */
    private Camera.Size getBestSize(int shortSize, int longSize, List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        float uiRatio = (float) longSize / shortSize;
        float minRatio = uiRatio;
        for (Camera.Size previewSize : sizes) {
            float cameraRatio = (float) previewSize.width / previewSize.height;

            //如果找不到比例相同的，找一个最近的,防止预览变形
            float offset = Math.abs(cameraRatio - minRatio);
            if (offset < minRatio) {
                minRatio = offset;
                bestSize = previewSize;
            }
            //比例相同
            if (uiRatio == cameraRatio) {
                bestSize = previewSize;
                break;
            }

        }
        return bestSize;
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        LogUtil.d(TAG, "window rotation=" + rotation);
        LogUtil.d(TAG, "camera rotation=" + info.orientation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 获取照片
     */
    public void getSurfacePic(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);

        Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

        // 因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
        if (mCurrentCamIndex == mFrontCameraId) {
            LogUtil.d(TAG, "front rotate!");
            bitmap = BitmapUtil.rotateBitmap(bitmap, 270);
            // 前置摄像头，镜像翻转
            bitmap = BitmapUtil.convertBitmap(bitmap);
        } else {
            LogUtil.d(TAG, "back rotate!");
            bitmap = BitmapUtil.rotateBitmap(bitmap, 90);
        }

        saveBitmap(bitmap);
    }

    /**
     * 保存图片
     */
    private void saveBitmap(final Bitmap mBitmap) {
        if (FileUtil.getAvailableSize() > 512) {
            final File filePic = FileUtil.saveBitmap(mBitmap, savePath);
            if (filePic == null) {
                /** 图片保存失败*/
                listener.onFail("图片保存失败");
                return;
            }
            listener.onSuccess(filePic, mBitmap);
//            FileUtil.compressPic(activity, filePic, new OnCompressListener() {
//                @Override
//                public void onStart() {
//                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
//                }
//
//                @Override
//                public void onSuccess(File file) {
//                    // TODO 压缩成功后调用，返回压缩后的图片文件
//                    FileUtil.deleteFile(filePic);
//                    listener.onSuccess(filePic, mBitmap);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    // TODO 当压缩过程出现问题时调用
//                    LogUtil.e("compressPic error");
//                }
//            });
        } else {
            listener.onFail("存储空间小于512M，图片无法正常保存");
        }
    }

    /**
     * 获取相机当前的照片
     */
    public void takePhoto() {
        LogUtil.d(TAG);
        // 经测试该方法到图片处理保存结束时间不到200ms
        this.canTake = true;

        // takePicture方法测试拍照时间大概为400ms左右
//        mCamera.takePicture(null, null, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                // takePicture会停止预览，需要连续拍照的话，需要自己重新开启图像预览
//                startPreview();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                bitmap = BitmapUtil.convertBitmap(bitmap);
//                saveMyBitmap(bitmap);
//            }
//        });
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        closeCamera();
        mCurrentCamIndex = mCurrentCamIndex == mFrontCameraId ? mBackCameraId : mFrontCameraId;
        openCamera(mCurrentCamIndex);
        startPreview();
    }

    /**
     * 开启闪光灯
     */
    public void switchFlashlight() {
        // FLASH_MODE_OFF:关闭闪光灯
        // FLASH_MODE_AUTO:自动模式（在预览或者自动对焦，或者拍照时根据实际拍摄场景自动触发）
        // FLASH_MODE_ON:打开模式（拍照时触发，驱动控制也有可能在预研或者自动对焦时触发）
        // FLASH_MODE_RED_EYE:红眼减少模式下触发
        // FLASH_MODE_TORCH:持续打开模式（预览、自动对焦和拍照时持续打开，也可用于视频录制）
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (!isFlashOpen) {
                LogUtil.d(TAG, "FLASH_MODE_AUTO");
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                isFlashOpen = true;
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                isFlashOpen = false;
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放
     */
    public void destroy() {
        hasSurface = false;
        FileUtil.delete(savePath);
    }
}
