package com.winjay.practice.media.audio_focus;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

import com.winjay.practice.media.interfaces.AudioType;

/**
 * Audio focus manager
 *
 * @author Winjay
 * @date 12/26/20
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private final AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;
    private OnAudioFocusChangeListener mAudioFocusChangeListener;

    public AudioFocusManager(Context context) {
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
    public int requestAudioFocus(int type) {
        if (mFocusRequest == null) {
            if (mAudioAttributes == null) {
                switch (type) {
                    case AudioType.TEST:
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build();
                        break;
                    case AudioType.MEDIA:
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build();
                        break;
                    case AudioType.SYSTEM:
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build();
                        break;
                    case AudioType.SPEECH:
                        mAudioAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build();
                        break;
                }
            }
            switch (type) {
                case AudioType.TEST:
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this)
                            .build();
                    break;
                case AudioType.MEDIA:
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this)
                            .build();
                    break;
                case AudioType.SYSTEM:
                    // For a concurrent interaction to take place, the following conditions must be met. The:
                    // * Incoming focus request must ask for AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                    // * Current focus holder doesn't setPauseWhenDucked(true)
                    // * Current focus holder opts not to receive duck events
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this)
                            .build();
                    break;
                case AudioType.SPEECH:
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                            .setAudioAttributes(mAudioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this)
                            .build();
                    break;
            }
        }
        return mAudioManager.requestAudioFocus(mFocusRequest);
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
    public int abandonAudioFocus() {
        if (mFocusRequest == null) {
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        return mAudioManager.abandonAudioFocusRequest(mFocusRequest);
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

    public boolean isMusicActive() {
        return mAudioManager.isMusicActive();
    }

    public AudioAttributes getAudioAttributes() {
        return mAudioAttributes;
    }
}
