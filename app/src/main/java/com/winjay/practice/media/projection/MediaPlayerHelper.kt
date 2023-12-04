package com.winjay.practice.media.projection

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.SurfaceHolder
import com.winjay.practice.media.audio_focus.AudioFocusManager
import com.winjay.practice.media.interfaces.AudioType
import com.winjay.practice.utils.LogUtil

/**
 * MediaPlayer
 *
 * @author Winjay
 * @date 2021-04-22
 */
private const val TAG = "MediaPlayerHelper"

object MediaPlayerHelper {
    var mediaPlayer: MediaPlayer? = null
    var audioFocusManager: AudioFocusManager? = null

    @JvmStatic
    fun prepare(context: Context, videoPath: String, holder: SurfaceHolder, preparedListener: MediaPlayer.OnPreparedListener) {
        LogUtil.d(TAG, "videoPath=$videoPath")
        mediaPlayer = MediaPlayer()
        audioFocusManager = AudioFocusManager(context)
        mediaPlayer?.apply {
            try {
                reset()
                setDataSource(videoPath)
                setAudioAttributes(audioFocusManager!!.audioAttributes)
                setDisplay(holder)
                prepareAsync()
                setOnPreparedListener(preparedListener)
            } catch (e: Exception) {
                LogUtil.e(TAG, "error=$e")
            }

        }
    }

    @JvmStatic
    fun play() {
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManager?.requestAudioFocus(AudioType.MEDIA)) {
            LogUtil.d(TAG)
            mediaPlayer?.start()
        }
    }

    @JvmStatic
    fun pause() {
        LogUtil.d(TAG)
        mediaPlayer?.pause()
    }

    @JvmStatic
    fun release() {
        LogUtil.d(TAG)
        mediaPlayer?.release()
        audioFocusManager?.abandonAudioFocus()
    }
}