package com.winjay.practice.media.projection

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ProjectionActivityBinding
import com.winjay.practice.utils.FileUtil
import com.winjay.practice.utils.LogUtil
import com.winjay.practice.utils.ToastUtils
import java.io.File
import java.nio.ByteBuffer

/**
 * 截屏和屏幕录制(MediaProjection)
 *
 * @author Winjay
 * @date 2021-04-20
 */
class MediaProjectionActivity : BaseActivity() {
    private val TAG = "MediaProjectionActivity"

    companion object {
        val SCREEN_CAPTURE_MODE = 1
        val MEDIA_PROJECTION_MODE = 2
    }

    // lateinit延迟初始化
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var isRecord = false
    private var mediaRecord: MediaRecorder? = null
    private var videoPath: String? = null

    private lateinit var binding: ProjectionActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ProjectionActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun permissions(): Array<String> {
        return arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        videoPath = externalCacheDir.toString() + File.separator + "mediaProjection" + File.separator
        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    @OnClick(R.id.screen_capture_btn)
    fun screenCapture() {
        mediaProjectionManager.createScreenCaptureIntent().apply {
            startActivityForResult(this, SCREEN_CAPTURE_MODE)
        }
    }

    @OnClick(R.id.media_projection_btn)
    fun mediaProjection() {
        if (!isRecord) {
            isRecord = true
            mediaProjectionManager.createScreenCaptureIntent().apply {
                startActivityForResult(this, MEDIA_PROJECTION_MODE)
            }
            binding.mediaProjectionBtn.text = "正在录制，可切换页面，点击结束并播放"
        } else {
            isRecord = false
            try {
                binding.mediaProjectionBtn.text = "点击开始屏幕录制"

                stopScreenRecord()

                toast("开始播放")
//                val file = File(videoPath, "mediaprojection.mp4")
//                MediaPlayerHelper.prepare(this, file.absolutePath, binding.sv.holder, MediaPlayer.OnPreparedListener {
//                    LogUtil.d(TAG, "onPrepared: ${it.isPlaying}")
//                    MediaPlayerHelper.play()
//                })
            } catch (e: Exception) {
                LogUtil.d(TAG, "media projection $e")
            }
        }
    }

    fun stopScreenRecord() {
        val intent = Intent("stop")
        sendBroadcast(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            data?.let {
                if (requestCode == SCREEN_CAPTURE_MODE) {
                    configImageReader()
                }
                val intent = Intent(this, ScreenRecordService::class.java)
                intent.putExtra("requestCode", requestCode)
                intent.putExtra("resultCode", resultCode)
                intent.putExtra("data", data)
                intent.putExtra("videoPath", videoPath)
                startForegroundService(intent)
            }
        }
    }

    /**
     * 配置截屏参数
     */
    private fun configImageReader() {
        val dm = resources.displayMetrics
        imageReader = ImageReader.newInstance(
            dm.widthPixels, dm.heightPixels,
            PixelFormat.RGBA_8888, 1
        ).apply {
            setOnImageAvailableListener({
                savePicTask(it)
            }, null)
//            // 把内容投射到ImageReader的surface
//            mediaProjection?.createVirtualDisplay(
//                TAG, dm.widthPixels, dm.heightPixels, dm.densityDpi,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null
//            )
        }
    }

    /**
     * 保存截图
     */
    private fun savePicTask(imageReader: ImageReader) {
        scopeIo {
            var image: Image? = null
            try {
                image = imageReader.acquireLatestImage()
                val width = image.width
                val height = image.height
                LogUtil.d(TAG, "width=$width, height=$height")

                val planes = image.planes
                val plane = planes[0]

                val buffer: ByteBuffer = plane.buffer

                //相邻像素样本之间的距离，因为RGBA，所以间距是4个字节
                val pixelStride = plane.pixelStride
                //每行的宽度
                val rowStride = plane.rowStride
                //因为内存对齐问题，每个buffer 宽度不同，所以通过pixelStride * width 得到大概的宽度，
                //然后通过 rowStride 去减，得到大概的内存偏移量，不过一般都是对齐的。
                val rowPadding = rowStride - pixelStride * width
                // 创建具体的bitmap大小，由于rowPadding是RGBA 4个通道的，所以也要除以pixelStride，得到实际的宽
                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)

                withMain {
                    val canvas = binding.sv.holder.lockCanvas()
                    with(canvas) {
                        drawBitmap(bitmap, 0f, 0f, null)
                        binding.sv.holder.unlockCanvasAndPost(this)
                    }
                    toast("保存成功")
                    mediaProjection?.stop()
                }
            } catch (e: Exception) {
                LogUtil.d(TAG, "savePicTask: $e")
            } finally {
                try {
                    image?.close()
                } catch (e: Exception) {
                    LogUtil.d(TAG, "image close: $e")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerHelper.release()
        stopScreenRecord()

//        mediaProjection?.stop()
//        MediaPlayerHelper.release()
//        imageReader?.close()
//        try {
//            mediaRecord?.stop()
//            mediaRecord?.release()
//            mediaRecord = null
//        } catch (e: Exception) {
//            LogUtil.e(TAG, "error=$e")
//            e.printStackTrace()
//        }
//        FileUtil.delete(videoPath)
    }
}