package com.winjay.practice.media.audio_focus;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.winjay.practice.media.interfaces.MediaType;

/**
 * 音频焦点管理
 *
 * @author Winjay
 * @date 12/26/20
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = AudioFocusManager.class.getSimpleName();
    private AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;
    private OnAudioFocusChangeListener mAudioFocusChangeListener;

    public AudioFocusManager(Context context, int mediaType) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int contentType = AudioAttributes.CONTENT_TYPE_UNKNOWN;
        if (mediaType == MediaType.MUSIC) {
            contentType = AudioAttributes.CONTENT_TYPE_MUSIC;
        } else if (mediaType == MediaType.MOVIE) {
            contentType = AudioAttributes.CONTENT_TYPE_MOVIE;
        }
        mAudioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(contentType)
                .build();
    }

    public AudioAttributes getAudioAttributes() {
        return mAudioAttributes;
    }

    /**
     * Request audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED}, {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED}
     * or {@link AudioManager#AUDIOFOCUS_REQUEST_DELAYED}.
     * <br>Note that the return value is never {@link AudioManager#AUDIOFOCUS_REQUEST_DELAYED} when focus
     * is requested without building the {@link AudioFocusRequest} with
     * {@link AudioFocusRequest.Builder#setAcceptsDelayedFocusGain(boolean)} set to
     * {@code true}.
     */
    public int requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mFocusRequest == null) {
                mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(mAudioAttributes)
                        .setAcceptsDelayedFocusGain(true) // 允许延迟获得焦点
                        .setWillPauseWhenDucked(true) // 不希望系统自动降低音量
                        .setOnAudioFocusChangeListener(this)
                        .build();
            }
            return mAudioManager.requestAudioFocus(mFocusRequest);
        } else {
            return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mAudioFocusChangeListener != null) {
            mAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }
    }

    /**
     * Release audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED} or {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED}
     */
    public int releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        } else {
            return mAudioManager.abandonAudioFocus(this);
        }
    }

    /**
     * Same as AudioManager.OnAudioFocusChangeListener.
     */
    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int focusChange);
    }

    public void setOnAudioFocusChangeListener(OnAudioFocusChangeListener listener) {
        mAudioFocusChangeListener = listener;
    }
}
