package com.winjay.practice.media.projection

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ProjectionActivityBinding
import com.winjay.practice.utils.BitmapUtil
import com.winjay.practice.utils.FileUtil
import com.winjay.practice.utils.LogUtil
import java.io.File

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
    private var isRecord = false
    private var videoPath: String? = null

    private var mMediaProjectionReceiver: MediaProjectionReceiver? = null

    private lateinit var binding: ProjectionActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ProjectionActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun permissions(): Array<String> {
        return arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        videoPath = externalCacheDir.toString() + File.separator + "mediaProjection" + File.separator
        if (!hasPermissions()) {
            requestPermissions()
        }

        registerMediaProjectionReceiver()
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

            } catch (e: Exception) {
                LogUtil.w(TAG, "media projection $e")
            }
        }
    }

    private fun stopScreenRecord() {
        sendBroadcast(Intent("stop"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val intent = Intent(this, ScreenRecordService::class.java)
                intent.putExtra("requestCode", requestCode)
                intent.putExtra("resultCode", resultCode)
                intent.putExtra("data", data)
                intent.putExtra("videoPath", videoPath)
                startForegroundService(intent)
            }
        }
    }

    private fun registerMediaProjectionReceiver() {
        if (mMediaProjectionReceiver == null) {
            mMediaProjectionReceiver = MediaProjectionReceiver()
            val intentFilter = IntentFilter("bitmap")
            intentFilter.addAction("play")
            registerReceiver(mMediaProjectionReceiver, intentFilter, RECEIVER_EXPORTED)
        }
    }

    private fun unregisterMediaProjectionReceiver() {
        mMediaProjectionReceiver?.let {
            unregisterReceiver(mMediaProjectionReceiver)
            mMediaProjectionReceiver = null
        }
    }

    inner class MediaProjectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (intent.action) {
                    "bitmap" -> {
                        val bytes = intent.getByteArrayExtra("bitmap")
                        val bitmap = BitmapUtil.bytes2Bitmap(bytes)
                        if (bitmap != null) {
                            val canvas = binding.sv.holder.lockCanvas()
                            with(canvas) {
                                drawBitmap(bitmap, 0f, 0f, null)
                                binding.sv.holder.unlockCanvasAndPost(this)
                            }
                        }
                    }
                    "play" -> {
                        toast("开始播放")
                        val file = File(videoPath, "mediaprojection.mp4")
                        MediaPlayerHelper.prepare(this@MediaProjectionActivity, file.absolutePath, binding.sv.holder, MediaPlayer.OnPreparedListener {
                            LogUtil.d(TAG, "onPrepared: ${it.isPlaying}")
                            MediaPlayerHelper.play()
                        })
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerHelper.release()

        unregisterMediaProjectionReceiver()

        stopScreenRecord()

        FileUtil.delete(videoPath)
    }
}