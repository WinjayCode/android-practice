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
    private val SCREEN_CAPTURE_MODE = 1
    private val MEDIA_PROJECTION_MODE = 2

    // lateinit延迟初始化
    private lateinit var mediaManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var isRecord = false
    private var mediaRecord: MediaRecorder? = null
    private var videoPath: String? = null;

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
        videoPath = externalCacheDir.toString() + File.separator + "mediaProjection" + File.separator
        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    @OnClick(R.id.screen_capture_btn)
    fun screenCapture() {
        mediaManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaManager.createScreenCaptureIntent().apply {
            startActivityForResult(this, SCREEN_CAPTURE_MODE)
        }
    }

    @OnClick(R.id.media_projection_btn)
    fun mediaProjection() {
        if (!isRecord) {
            isRecord = true
            mediaManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaManager.createScreenCaptureIntent().apply {
                startActivityForResult(this, MEDIA_PROJECTION_MODE)
            }
            binding.mediaProjectionBtn.text = "正在录制，可切换页面，点击结束并播放"
        } else {
            try {
                binding.mediaProjectionBtn.text = "点击开始屏幕录制"
                mediaRecord?.stop()
                mediaProjection?.stop()
                toast("开始播放")
                val file = File(videoPath, "mediaprojection.mp4")
                MediaPlayerHelper.prepare(this, file.absolutePath, binding.sv.holder, MediaPlayer.OnPreparedListener {
                    LogUtil.d(TAG, "onPrepared: ${it.isPlaying}")
                    MediaPlayerHelper.play()
                })
            } catch (e: Exception) {
                LogUtil.d(TAG, "media projection $e")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // 获取到操作对象（data不为null时执行let函数体，相当于判空）
            data?.let {
                mediaProjection = mediaManager.getMediaProjection(resultCode, it)
            }
            when (requestCode) {
                // 截屏
                SCREEN_CAPTURE_MODE -> {
                    LogUtil.d(TAG, "开始截屏")
                    configImageReader()
                }
                // 录屏
                MEDIA_PROJECTION_MODE -> {
                    if (configMediaRecorder()) {
                        try {
                            mediaRecord?.start()
                        } catch (e: Exception) {
                            LogUtil.e(TAG, "error=$e")
                            e.printStackTrace()
                        }
                        toast("正在录制")
                    }
                }
            }
        }
    }

    private fun configImageReader() {
        val dm = resources.displayMetrics
        imageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels,
                PixelFormat.RGBA_8888, 1).apply {
            setOnImageAvailableListener({
                savePicTask(it)
            }, null)
            // 把内容投射到ImageReader的surface
            mediaProjection?.createVirtualDisplay(TAG, dm.widthPixels, dm.heightPixels, dm.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)
        }
    }

    private fun configMediaRecorder(): Boolean {
        val dir = File(videoPath)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val file = File(videoPath, "mediaprojection.mp4")
        if (file.exists()) {
            file.delete()
        }

        val dm = resources.displayMetrics
        mediaRecord = MediaRecorder()
        mediaRecord?.apply {
            // 音频来源
            setAudioSource(MediaRecorder.AudioSource.MIC)
            // 视频来源
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            // 输出格式
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            // 音频编码格式
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            // 视频编码格式
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            // 视频宽高
            setVideoSize(dm.widthPixels, dm.heightPixels)
            // 视频帧率
            setVideoFrameRate(60)
            // 视频编码比特率(值越大，视频越清晰，类似音频比特率，值越大，音质越好)
            setVideoEncodingBitRate(6 * 1024 * 1024)
            // 输出文件位置
            setOutputFile(file.absolutePath)
            try {
                prepare()
                mediaProjection?.createVirtualDisplay(
                        TAG,
                        dm.widthPixels,
                        dm.heightPixels,
                        dm.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        surface,
                        null,
                        null
                )
            } catch (e: Exception) {
                LogUtil.e(TAG, "error=$e")
                e.printStackTrace()
                return false
            }
        }
        return true
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
                val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                        Bitmap.Config.ARGB_8888)
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
        mediaProjection?.stop()
        MediaPlayerHelper.release()
        imageReader?.close()
        try {
            mediaRecord?.stop()
            mediaRecord?.release()
            mediaRecord = null
        } catch (e: Exception) {
            LogUtil.e(TAG, "error=$e")
            e.printStackTrace()
        }
        FileUtil.delete(videoPath)
    }
}