package com.winjay.practice.media.camera.camerax;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * CameraX
 *
 * @author Winjay
 * @date 2021-04-12
 */
public class CameraXActivity extends BaseActivity {
    private static final String TAG = CameraXActivity.class.getSimpleName();

    private ImageCapture mImageCapture;
    private CameraSelector mCameraSelector;
    private String savePath;

    @BindView(R.id.preview)
    PreviewView previewView;

    @BindView(R.id.img_pic)
    ImageView imgPic;

    @BindView(R.id.pic_path_tv)
    TextView tvPicDir;

    @Override
    protected int getLayoutId() {
        return R.layout.camerax_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savePath = getExternalCacheDir() + File.separator + "camera" + File.separator;

        //选择后置摄像头
        mCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        startCamera();
    }

    /**
     * 开启摄像头
     */
    private void startCamera() {
        //返回当前可以绑定生命周期的 ProcessCameraProvider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                try {
                    //将相机的生命周期和activity的生命周期绑定，camerax 会自己释放，不用担心了
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    //预览的 capture，它里面支持角度换算
                    Preview preview = new Preview.Builder().build();

                    //创建图片的 capture
                    mImageCapture = new ImageCapture.Builder()
                            // 自动闪光
                            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                            // 缩短照片拍摄的延迟时间
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build();

                    //预览之前先解绑
                    cameraProvider.unbindAll();

                    //将数据绑定到相机的生命周期中
                    Camera camera = cameraProvider.bindToLifecycle(CameraXActivity.this, mCameraSelector, preview, mImageCapture);

                    //将previewView 的 surface 给相机预览
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OnClick({R.id.btn_take_photo})
    void takePhoto() {
        LogUtil.d(TAG);
        if (mImageCapture != null) {
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //创建文件
            File file = new File(savePath, "Pic_" + System.currentTimeMillis() + ".jpg");
            if (file.exists()) {
                file.delete();
            }

            // 此方法onImageSaved返回的getSavedUri不为空
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(
                    getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues).build();

            //创建包文件的数据，比如创建文件，此方法onImageSaved返回的getSavedUri为空
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

            //开始拍照
            mImageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    LogUtil.d(TAG);
                    Uri savedUri = outputFileResults.getSavedUri();
                    if (savedUri != null) {
                        LogUtil.d(TAG, "savedUri=" + savedUri.toString());
                        // 真实路径在sdcard/Pictures/目录下
                        imgPic.setImageURI(savedUri);
                        tvPicDir.setText("图片路径：" + savedUri.getPath());
                    } else {
                        // BitmapFactory.decodeFile 图片有可能被旋转
//                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                        imgPic.setImageBitmap(bitmap);

                        imgPic.setImageURI(Uri.fromFile(file));

                        tvPicDir.setText("图片路径：" + file.getPath());
                    }
//                    Toast.makeText(CameraxActivity.this, "保存成功: ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    LogUtil.e(TAG, "exception=" + exception.toString());
//                    Toast.makeText(CameraxActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.switch_camera_btn)
    void switchCamera() {
        LogUtil.d(TAG);
        /**
         * 白屏的问题是 PreviewView 移除所有View，且没数据到 Surface，
         * 所以只留背景色，可以对次做处理
         */
        mCameraSelector = mCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA ?
                CameraSelector.DEFAULT_BACK_CAMERA : CameraSelector.DEFAULT_FRONT_CAMERA;
        startCamera();
    }

    @OnClick(R.id.switch_flash_btn)
    void switchFlashlight() {
        LogUtil.d(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.delete(savePath);
    }
}
