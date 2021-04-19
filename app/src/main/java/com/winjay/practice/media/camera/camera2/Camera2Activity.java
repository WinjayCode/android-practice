package com.winjay.practice.media.camera.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.BitmapUtil;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Camera2
 *
 * @author Winjay
 * @date 2021-04-01
 */
public class Camera2Activity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = Camera2Activity.class.getSimpleName();

    private CameraManager mCameraManager;
    private String mCameraId;

    private String mFrontCameraId;
    private CameraCharacteristics mFrontCameraCharacteristics;

    private String mBackCameraId;
    private CameraCharacteristics mBackCameraCharacteristics;

    private ImageReader mImageReader;

    private int mSensorOrientation;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;

    private CaptureRequest previewCaptureRequest;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private boolean mTorchMode = false;

    private String savePath;

    private Handler mCameraHandler;
    private HandlerThread handlerThread = new HandlerThread("CameraThread");

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @BindView(R.id.preview_texture_view)
    TextureView mTextureView;

    @BindView(R.id.img_pic)
    ImageView imgPic;

    @BindView(R.id.pic_path_tv)
    TextView tvPicDir;

    @Override
    protected int getLayoutId() {
        return R.layout.camera2_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
        savePath = getExternalCacheDir() + File.separator + "camera" + File.separator;
        mTextureView.setSurfaceTextureListener(new MySurfaceTextureListener());
    }

    private class MySurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.d(TAG);
            initCamera();
            mCameraId = mBackCameraId;
            //当有大小时，打开摄像头
            openCamera(mBackCameraId, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtil.d(TAG);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.d(TAG);
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtil.d(TAG);
        }
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        LogUtil.d(TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                //获取相机服务 CameraManager
                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                //遍历设备支持的相机 ID ，比如前置，后置等
                String[] cameraIdList = mCameraManager.getCameraIdList();
                LogUtil.d(TAG, "cameraIdList.length=" + cameraIdList.length);
                for (String cameraId : cameraIdList) {
                    // 拿到装在所有相机信息的  CameraCharacteristics 类
                    CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                    //拿到相机的方向，前置，后置，外置
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

                    if (facing != null) {
                        //后置摄像头
                        if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                            mBackCameraId = cameraId;
                            LogUtil.d(TAG, "mBackCameraId=" + mBackCameraId);
                            mBackCameraCharacteristics = characteristics;
                        } else if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            //前置摄像头
                            mFrontCameraId = cameraId;
                            LogUtil.d(TAG, "mFrontCameraId=" + mFrontCameraId);
                            mFrontCameraCharacteristics = characteristics;
                        }
                    }

                    // CameraCharacteristics.FLASH_INFO_AVAILABLE 是否有闪光灯
                    // CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES 是否有AE自动曝光模式
                    //是否支持 Camera2 的高级特性
                    Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    // 不支持 Camera2 的特性
                    if (level == null || level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        toast("当前设备不支持Camera2的高级特效！");
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera(String cameraId, int width, int height) {
        LogUtil.d(TAG, "cameraId=" + cameraId + ", width=" + width + ", height=" + height + ", ratio=" + (float) width / (float) height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //判断不同摄像头，拿到 CameraCharacteristics
            CameraCharacteristics characteristics = cameraId.equals(mBackCameraId) ? mBackCameraCharacteristics : mFrontCameraCharacteristics;
            // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            //获取摄像头传感器的方向
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            LogUtil.d(TAG, "camera sensor orientation:" + mSensorOrientation);
            // 获取预览尺寸
            Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);
            for (Size previewSize : previewSizes) {
                LogUtil.d(TAG, "预览尺寸：" + previewSize.getWidth() + "*" + previewSize.getHeight()
                        + ", 比例：" + (float) previewSize.getWidth() / (float) previewSize.getHeight());
            }
            // 获取最佳预览尺寸（相机输出尺寸宽高默认是横向的，屏幕是竖向时需要反转！！！）
            Size bestSize = getBestSize(height, width, previewSizes);
            LogUtil.d(TAG, "最佳预览尺寸：" + bestSize.getWidth() + "*" + bestSize.getHeight());

            mTextureView.getSurfaceTexture().setDefaultBufferSize(bestSize.getWidth(), bestSize.getHeight());

            // 保存照片尺寸
            Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
            for (Size size : sizes) {
                LogUtil.d(TAG, "保存照片尺寸：" + size.getWidth() + "*" + size.getHeight()
                        + ", 比例：" + (float) size.getWidth() / (float) size.getHeight());
            }
            Size largest = Collections.max(Arrays.asList(sizes), new CompareSizesByArea());
            LogUtil.d(TAG, "最佳保存照片尺寸：" + largest.getWidth() + "*" + largest.getHeight());

            //设置imagereader，配置大小，且最大Image为 1，因为是 JPEG
            mImageReader = ImageReader.newInstance(bestSize.getHeight(), bestSize.getWidth(), ImageFormat.JPEG, 1);

            //拍照监听
            mImageReader.setOnImageAvailableListener(new ImageAvailable(), mCameraHandler);

            try {
                //打开摄像头，监听数据
                mCameraManager.openCamera(cameraId, new CameraDeviceCallback(), mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // 为Size定义一个比较器Comparator
    private class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private class CameraDeviceCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            LogUtil.d(TAG);
            mCameraDevice = camera;
            //此时摄像头已经打开，可以预览了
            createPreviewPipeline(camera);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            LogUtil.d(TAG);
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            LogUtil.d(TAG);
            camera.close();
        }
    }

    /**
     * 拍照监听,当有图片数据时，回调该接口
     */
    private class ImageAvailable implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            LogUtil.d(TAG);
            Bitmap bitmap = getPhoto(reader);
            // 保存图片
            File file = FileUtil.saveBitmap(bitmap, savePath);

            imgPic.post(new Runnable() {
                @Override
                public void run() {
                    imgPic.setImageBitmap(bitmap);
                    tvPicDir.setText("图片路径：" + file.getPath());
                }
            });
        }
    }

    private Bitmap getPhoto(ImageReader imageReader) {
        //获取捕获的照片数据
        Image image = imageReader.acquireLatestImage();
        //拿到所有的 Plane 数组
        Image.Plane[] planes = image.getPlanes();
        //由于是 JPEG ，只需要获取下标为 0 的数据即可
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        //把 bytebuffer 的数据给 byte数组
        buffer.get(data);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        //旋转图片（根据预览画面宽高比来决定是否需要旋转）
        if (mCameraId.equals(mFrontCameraId)) {
//            bitmap = BitmapUtil.rotateBitmap(bitmap, 270);
            bitmap = BitmapUtil.convertBitmap(bitmap);
        } else {
//            bitmap = BitmapUtil.rotateBitmap(bitmap, 90);
        }
        //记得关闭 image
        if (image != null) {
            image.close();
        }
        return bitmap;
    }

    private void createPreviewPipeline(CameraDevice cameraDevice) {
        LogUtil.d(TAG);
        try {
            // CameraDevice.TEMPLATE_PREVIEW 适用于配置预览的模板
            // CameraDevice.TEMPLATE_RECORD 适用于视频录制的模板
            // CameraDevice.TEMPLATE_STILL_CAPTURE 适用于拍照的模板
            // CameraDevice.TEMPLATE_VIDEO_SNAPSHOT 适用于在录制视频过程中支持拍照的模板
            // CameraDevice.TEMPLATE_MANUAL 适用于希望自己手动配置大部分参数的模板

            //创建作为预览的 CaptureRequst.builder
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface surface = new Surface(mTextureView.getSurfaceTexture());
            //添加 surface 容器
            mPreviewRequestBuilder.addTarget(surface);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求,这个必须在创建 Seesion 之前就准备好，传递给底层用于皮遏制 pipeline
            cameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    LogUtil.d(TAG, "createCaptureSession");
                    mCameraCaptureSession = session;
                    try {
                        //设置自动聚焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //设置自动曝光
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                        //创建 CaptureRequest
                        previewCaptureRequest = mPreviewRequestBuilder.build();
                        //设置预览时连续捕获图片数据
                        session.setRepeatingRequest(previewCaptureRequest, null, mCameraHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    LogUtil.d(TAG);
                    toast("配置失败");
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取预览最后尺寸
     */
    private Size getBestSize(int shortSize, int longSize, Size[] sizes) {
        Size bestSize = null;
        float uiRatio = (float) longSize / shortSize;
        float minRatio = uiRatio;
        for (Size previewSize : sizes) {
            float cameraRatio = (float) previewSize.getWidth() / previewSize.getHeight();

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

    @OnClick({R.id.btn_take_photo})
    void takePhoto() {
        LogUtil.d(TAG);
        try {
            //创建一个拍照的 session
            final CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //设置装在图像数据的 Surface
            captureRequest.addTarget(mImageReader.getSurface());
            //聚焦
            captureRequest.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //自动曝光
            captureRequest.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取设备方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            LogUtil.d(TAG, "device orientation:" + rotation);
            // 根据设备方向计算设置照片的方向
            captureRequest.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(captureRequest.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    try {
                        // 拍完之后，让它继续可以预览
                        mCameraCaptureSession.setRepeatingRequest(previewCaptureRequest, null, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }, mCameraHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * 关闭摄像头
     */
    private void closeCamera() {
        if (mCameraCaptureSession != null) {
            try {
                //停止预览
                mCameraCaptureSession.stopRepeating();
                mCameraCaptureSession = null;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        //关闭设备
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    @OnClick(R.id.switch_camera_btn)
    void switchCamera() {
        LogUtil.d(TAG);
        mCameraId = mCameraId.equals(mBackCameraId) ? mFrontCameraId : mBackCameraId;
        closeCamera();
        LogUtil.d(TAG, "mCameraId=" + mCameraId);
        openCamera(mCameraId, mTextureView.getWidth(), mTextureView.getHeight());
    }

    @OnClick(R.id.switch_flash_btn)
    void switchFlashlight() {
        LogUtil.d(TAG);
        // 没有作用？
        try {
            mTorchMode = !mTorchMode;
            if (mTorchMode) {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            }
            mCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        closeCamera();
        FileUtil.delete(savePath);
    }
}
