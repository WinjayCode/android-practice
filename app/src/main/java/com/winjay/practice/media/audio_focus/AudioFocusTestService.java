package com.winjay.practice.media.audio_focus;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.winjay.practice.media.interfaces.AudioType;
import com.winjay.practice.utils.LogUtil;

import java.io.IOException;

/**
 * @author Winjay
 * @date 2022-03-08
 */
public class AudioFocusTestService extends Service {
    private static final String TAG = "AudioFocusTestService";

    private MediaPlayer mediaPlayer1;
    private AudioFocusManager audioFocusManager1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);

        mediaPlayer1 = new MediaPlayer();

        audioFocusManager1 = new AudioFocusManager(this);
        audioFocusManager1.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "1:AUDIOFOCUS_GAIN");
                        if (mediaPlayer1 != null) {
                            mediaPlayer1.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "1:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "1:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mediaPlayer1 != null) {
                            mediaPlayer1.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "1:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                }
            }
        });

        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManager1.requestAudioFocus(AudioType.SYSTEM)) {
            playAudio();
        }
    }

    private void playAudio() {
        mediaPlayer1.reset();
        try {
//                mMediaPlayer.setDataSource("system/media/audio/ringtones/Basic_Bell.ogg");
            AssetFileDescriptor afd = getAssets().openFd("audio/chengdu.mp3");
            mediaPlayer1.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            mediaPlayer1.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + mediaPlayer1.getDuration());
                mediaPlayer1.start();
            }
        });
        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG);
                audioFocusManager1.abandonAudioFocus();
            }
        });
        mediaPlayer1.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);

        if (mediaPlayer1 != null) {
            if (mediaPlayer1.isPlaying()) {
                mediaPlayer1.stop();
            }
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }

        audioFocusManager1.abandonAudioFocus();
    }
}
