package com.winjay.practice.media.projection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.winjay.practice.Constants
import com.winjay.practice.utils.BitmapUtil
import com.winjay.practice.utils.DisplayUtil
import com.winjay.practice.utils.LogUtil
import com.winjay.practice.utils.ToastUtils
import java.io.File
import java.nio.ByteBuffer

/**
 * 屏幕录制服务
 *
 * @author Winjay
 * @date 2022-10-28
 */
class ScreenRecordService : Service() {
    private val TAG: String = javaClass.simpleName

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null

    private var imageReader: ImageReader? = null

    private var mediaRecord: MediaRecorder? = null

    private var mediaProjectionControlReceiver: MediaProjectionControlReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
        startForeground(Constants.NOTIFICATION_ID, builder.build())

        registerMediaProjectionControlReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val requestCode = intent.getIntExtra("requestCode", 0)
            val resultCode = intent.getIntExtra("resultCode", 0)
            val videoPath = intent.getStringExtra("videoPath")
            val data: Intent? = intent.getParcelableExtra("data")

            data?.let {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, it)
            }

            if (mediaProjection == null) {
                LogUtil.w(TAG, "mediaProjection == null")
            }

            when (requestCode) {
                // 截屏
                MediaProjectionActivity.SCREEN_CAPTURE_MODE -> {
                    LogUtil.d(TAG, "开始截屏")
                    configImageReader()
                }
                // 录屏
                MediaProjectionActivity.MEDIA_PROJECTION_MODE -> {
                    if (configMediaRecorder(videoPath)) {
                        try {
                            ToastUtils.show(this@ScreenRecordService, "正在录制")
                            if (mediaRecord != null) {
                                mediaRecord?.start()
                            }
                        } catch (e: Exception) {
                            LogUtil.w(TAG, "mediaRecord start error=$e")
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterMediaProjectionControlReceiver()
    }

    /**
     * 配置截屏参数
     */
    private fun configImageReader() {
        val screenSize = DisplayUtil.getScreenSize(this)
        imageReader = ImageReader.newInstance(
            screenSize[0], screenSize[1],
            PixelFormat.RGBA_8888, 1
        ).apply {
            setOnImageAvailableListener({
                savePicTask(it)
            }, null)
            // 把内容投射到ImageReader的surface
            mVirtualDisplay = mediaProjection?.createVirtualDisplay(
                TAG, screenSize[0], screenSize[1], resources.displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null
            )
        }
    }

    /**
     * 保存截图
     */
    private fun savePicTask(imageReader: ImageReader) {
        scopeIo {
            var image: Image? = null
            try {
                image = imageReader.acquireNextImage()

                // acquireLatestImage 会获取到带有系统提示框的截图
//                image = imageReader.acquireLatestImage()

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
                LogUtil.d(TAG, "bitmap.size=${bitmap.width}, ${bitmap.height}")


                val intent = Intent("bitmap")
                intent.putExtra("bitmap", BitmapUtil.bitmap2Bytes(bitmap))
                sendBroadcast(intent)

                stop()
            } catch (e: Exception) {
                LogUtil.w(TAG, "savePicTask: $e")
            } finally {
                try {
                    image?.close()
                } catch (e: Exception) {
                    LogUtil.w(TAG, "image close: $e")
                }
            }
        }
    }

    /**
     * 配置录屏参数
     * @return Boolean
     */
    private fun configMediaRecorder(videoPath: String?): Boolean {
        if (videoPath == null || TextUtils.isEmpty(videoPath)) {
            return false
        }

        val dir = File(videoPath)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val file = File(videoPath, "mediaprojection.mp4")
        if (file.exists()) {
            file.delete()
        }

        val screenSize = DisplayUtil.getScreenSize(this)
        mediaRecord = MediaRecorder()
        mediaRecord?.apply {

            // 音频来源
            setAudioSource(MediaRecorder.AudioSource.MIC)

            // 视频来源（必须于 setOutputFormat 之前调用！！！）
            setVideoSource(MediaRecorder.VideoSource.SURFACE)

            // 输出格式（必须于 setAudioSource 之后调用！！！）
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            // 音频编码格式（必须于 setOutputFormat 之后调用！！！）
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            // Usually it is either 1 (mono) or 2 (stereo). (默认手机录出来的是mono，但是后期解码时AudioTrack会报错，需要stereo才行。。。)
            setAudioChannels(2)

            // 视频编码格式
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            // 视频宽高（如果宽高信息不合适，会导致 MediaRecorder 录屏失败！！！）
            LogUtil.d(TAG, "widthPixels=${screenSize[0]}, heightPixels=${screenSize[1]}")
            setVideoSize(screenSize[0], screenSize[1])
            // 视频帧率
            setVideoFrameRate(60)
            // 视频编码比特率(值越大，视频越清晰，类似音频比特率，值越大，音质越好)
            setVideoEncodingBitRate(6 * 1024 * 1024)

            // 输出文件位置
            setOutputFile(file.absolutePath)

            setOnErrorListener(object : MediaRecorder.OnErrorListener {
                override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
                    LogUtil.d(TAG, "what=${what}, extra=${extra}")
                }
            })
            setOnInfoListener { mr, what, extra -> LogUtil.d(TAG, "what=${what}, extra=${extra}") }
            try {
                prepare()
                mVirtualDisplay = mediaProjection?.createVirtualDisplay(
                    TAG,
                    screenSize[0],
                    screenSize[1],
                    resources.displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    surface,
                    null,
                    null
                )
            } catch (e: Exception) {
                LogUtil.w(TAG, "mediaRecord prepare error=$e")
                return false
            }
        }
        return true
    }

    private fun registerMediaProjectionControlReceiver() {
        if (mediaProjectionControlReceiver == null) {
            mediaProjectionControlReceiver = MediaProjectionControlReceiver()
            val intentFilter = IntentFilter("stop")
            registerReceiver(mediaProjectionControlReceiver, intentFilter)
        }
    }

    private fun unregisterMediaProjectionControlReceiver() {
        mediaProjectionControlReceiver?.let {
            unregisterReceiver(it)
            mediaProjectionControlReceiver = null
        }
    }

    inner class MediaProjectionControlReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (intent.action) {
                    "stop" -> {
                        stop()

                        sendBroadcast(Intent("play"))
                    }
                    else -> {}
                }
            }
        }
    }

    fun stop() {
        imageReader?.close()
        mediaRecord?.stop()
        release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun release() {
        mediaRecord?.let {
            mediaRecord?.reset()
            mediaRecord?.release()
            mediaRecord = null
        }

        mediaProjection?.let {
            mediaProjection?.stop()
            mediaProjection = null
        }

        mVirtualDisplay?.let {
            mVirtualDisplay?.release()
            mVirtualDisplay = null
        }
    }
}