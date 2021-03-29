package com.winjay.practice.media.audio_focus;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.AudioFocusManagerLib;
import com.winjay.practice.utils.LogUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class AudioFocusTestActivity extends BaseActivity {
    private static final String TAG = AudioFocusTestActivity.class.getSimpleName();

    @BindView(R.id.audio_matrix)
    RecyclerView mRecyclerView;

    @BindView(R.id.media_btn)
    Button mediaBtn;

    @BindView(R.id.system_btn)
    Button systemBtn;

    private AudioFocusManagerLib mMediaAudioFocusManager;
    private AudioFocusManagerLib mSystemAudioFocusManager;

    private MediaPlayer musicPlayer;
    private MediaPlayer systemPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.audio_focus_test_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicPlayer = new MediaPlayer();
        systemPlayer = new MediaPlayer();

        mMediaAudioFocusManager = new AudioFocusManagerLib(this);
        mMediaAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManagerLib.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_GAIN");
                        if (musicPlayer != null) {
                            musicPlayer.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (musicPlayer != null) {
                            musicPlayer.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        if (musicPlayer != null) {
                            musicPlayer.pause();
                        }
                        break;
                }
            }
        });

        mSystemAudioFocusManager = new AudioFocusManagerLib(this);
        mSystemAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManagerLib.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "system:AUDIOFOCUS_GAIN");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "system:AUDIOFOCUS_LOSS");
                        if (!mSystemAudioFocusManager.isMusicActive()) {
                            if (systemPlayer != null) {
                                systemPlayer.stop();
                            }
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "system:AUDIOFOCUS_LOSS_TRANSIENT");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "system:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                }
            }
        });
    }

    @OnClick(R.id.media_btn)
    void media() {
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mMediaAudioFocusManager.requestFocus(AudioFocusManagerLib.AudioType.MEDIA)) {
            playMusic();
        }
    }

    private void playMusic() {
        musicPlayer.reset();
        try {
            AssetFileDescriptor afd = getAssets().openFd("audio/chengdu.mp3");
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//                mMediaPlayer.setDataSource("system/media/audio/ringtones/Luna.ogg");
//                mMediaPlayer.setDataSource(this, Uri.parse("system/media/Music/guniang.mp3"));

            musicPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + musicPlayer.getDuration());
                musicPlayer.start();
            }
        });
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG);
                mMediaAudioFocusManager.releaseAudioFocus();
            }
        });
        musicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                return false;
            }
        });
    }

    @OnClick(R.id.system_btn)
    void system() {
//        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mSystemAudioFocusManager.requestFocus(AudioFocusManagerLib.AudioType.SYSTEM)) {
//            playSystem();
//        }
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mSystemAudioFocusManager.requestFocus(AudioFocusManagerLib.AudioType.SPEECH)) {
            playSystem();
        }
    }

    private void playSystem() {
        systemPlayer.reset();
        try {
//                mMediaPlayer.setDataSource("system/media/audio/ringtones/Basic_Bell.ogg");
            AssetFileDescriptor afd = getAssets().openFd("audio/13730.mp3");
            systemPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            systemPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        systemPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + systemPlayer.getDuration());
                systemPlayer.start();
            }
        });
        systemPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mSystemAudioFocusManager.releaseAudioFocus();
            }
        });
        systemPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        if (musicPlayer != null) {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
            }
            musicPlayer.release();
            musicPlayer = null;
        }
        if (systemPlayer != null) {
            if (systemPlayer.isPlaying()) {
                systemPlayer.stop();
            }
            systemPlayer.release();
            systemPlayer = null;
        }

        mMediaAudioFocusManager.releaseAudioFocus();
        mSystemAudioFocusManager.releaseAudioFocus();
    }
}
