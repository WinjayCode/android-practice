package com.winjay.practice.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import com.winjay.practice.utils.LogUtil;

/**
 * 音量调节工具类
 *
 * @author winjay
 * @date 2019/5/31
 */
public class VolumeUtil {
    public static final String TAG = VolumeUtil.class.getSimpleName();

    /**
     * 静音
     *
     * @param context
     * @param stream  通道
     */
    public static void muteVolume(Context context, int stream) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtil.d(TAG, "dialogMute()_isStreamMute=" + audioManager.isStreamMute(stream));
            if (!audioManager.isStreamMute(stream)) {
                LogUtil.d(TAG, "执行静音  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
                audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_MUTE, 0);
//                audioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_MUTE, stream, 0);
            }
        } else {
            audioManager.setStreamMute(stream, true);
        }
    }

    /**
     * 取消静音
     *
     * @param context
     * @param stream  通道
     */
    public static void unMuteVolume(Context context, int stream) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtil.d(TAG, "dialogUnMute()_isStreamMute=" + audioManager.isStreamMute(stream));
            if (audioManager.isStreamMute(stream)) {
                LogUtil.d(TAG, "执行取消静音  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
                audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_UNMUTE, 0);
//                audioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_UNMUTE, stream, 0);
            }
        } else {
            audioManager.setStreamMute(stream, false);
        }
    }

    /**
     * 音量增加
     */
    public static void volumeUp(Context context, int stream) {
        LogUtil.d(TAG, "volumeUp()");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
        }
    }

    /**
     * 音量减小
     */
    public static void volumeDown(Context context, int stream) {
        LogUtil.d(TAG, "volumeDown()");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
        }
    }
}
