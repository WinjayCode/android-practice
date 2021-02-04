package com.winjay.practice.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

public class AudioFocusManagerLib implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;
    private OnAudioFocusChangeListener mAudioFocusChangeListener;

    public AudioFocusManagerLib(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
    public int requestFocus(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mFocusRequest == null) {
                if (mAudioAttributes == null) {
                    if (type == AudioType.MEDIA) {
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                .build();
                    } else if (type == AudioType.SYSTEM) {
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build();
                    }
                }
                if (type == AudioType.MEDIA) {
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true) // 允许延迟获得焦点
                            .setWillPauseWhenDucked(true) // 不希望系统自动降低音量
                            .setOnAudioFocusChangeListener(this)
                            .build();
                } else if (type == AudioType.SYSTEM) {
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true) // 允许延迟获得焦点
                            .setWillPauseWhenDucked(true) // 不希望系统自动降低音量
                            .setOnAudioFocusChangeListener(this)
                            .build();
                }
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
        if (mFocusRequest == null) {
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
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

    public boolean isMusicActive () {
        return mAudioManager.isMusicActive();
    }

    public interface AudioType {
        int MEDIA = 1;
        int SYSTEM = 2;
    }
}
