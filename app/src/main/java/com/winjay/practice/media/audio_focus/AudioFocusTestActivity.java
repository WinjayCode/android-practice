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

/**
 * 连续申请焦点，焦点最后也会依次释放
 *
 * @author Winjay
 * @date 2021-04-08
 */
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

    private AudioFocusManagerLib audioFocusManagerLib1;
    private AudioFocusManagerLib audioFocusManagerLib2;
    private AudioFocusManagerLib audioFocusManagerLib3;

    private MediaPlayer musicPlayer;
    private MediaPlayer systemPlayer;

    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;
    private MediaPlayer mediaPlayer3;

    @Override
    protected int getLayoutId() {
        return R.layout.audio_focus_test_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicPlayer = new MediaPlayer();
        systemPlayer = new MediaPlayer();

        mediaPlayer1 = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer3 = new MediaPlayer();

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


        audioFocusManagerLib1 = new AudioFocusManagerLib(this);
        audioFocusManagerLib1.setOnAudioFocusChangeListener(new AudioFocusManagerLib.OnAudioFocusChangeListener() {
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

        audioFocusManagerLib2 = new AudioFocusManagerLib(this);
        audioFocusManagerLib2.setOnAudioFocusChangeListener(new AudioFocusManagerLib.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "2:AUDIOFOCUS_GAIN");
                        if (mediaPlayer2 != null) {
                            mediaPlayer2.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "2:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "2:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mediaPlayer2 != null) {
                            mediaPlayer2.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "2:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                }
            }
        });

        audioFocusManagerLib3 = new AudioFocusManagerLib(this);
        audioFocusManagerLib3.setOnAudioFocusChangeListener(new AudioFocusManagerLib.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "3:AUDIOFOCUS_GAIN");
                        if (mediaPlayer3 != null) {
                            mediaPlayer3.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "3:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "3:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mediaPlayer3 != null) {
                            mediaPlayer3.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "3:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
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

    @OnClick(R.id.btn_1)
    void btn1() {
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManagerLib1.requestFocus(AudioFocusManagerLib.AudioType.TEST)) {
            playMediaNum(1);
        }
    }

    @OnClick(R.id.btn_2)
    void btn2() {
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManagerLib2.requestFocus(AudioFocusManagerLib.AudioType.TEST)) {
            playMediaNum(2);
        }
    }

    @OnClick(R.id.btn_3)
    void btn3() {
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManagerLib3.requestFocus(AudioFocusManagerLib.AudioType.TEST)) {
            playMediaNum(3);
        }
    }

    private MediaPlayer mediaPlayerNum = null;

    private void playMediaNum(int num) {
        if (num == 1) {
            mediaPlayerNum = mediaPlayer1;
        }
        if (num == 2) {
            mediaPlayerNum = mediaPlayer2;
        }
        if (num == 3) {
            mediaPlayerNum = mediaPlayer3;
        }
        mediaPlayerNum.reset();
        try {
            String fileName = "";
            if (num == 1) {
                fileName = "audio/chengdu.mp3";
            }
            if (num == 2) {
                fileName = "audio/speaker_test.mp3";
            }
            if (num == 3) {
                fileName = "audio/13730.mp3";
            }
//            mediaPlayerNum.setDataSource(path);
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            mediaPlayerNum.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            mediaPlayerNum.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayerNum.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + mediaPlayerNum.getDuration());
                mediaPlayerNum.start();
            }
        });
        mediaPlayerNum.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (num == 1) {
                    audioFocusManagerLib1.releaseAudioFocus();
                }
                if (num == 2) {
                    audioFocusManagerLib2.releaseAudioFocus();
                }
                if (num == 3) {
                    audioFocusManagerLib3.releaseAudioFocus();
                }
            }
        });
        mediaPlayerNum.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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

        if (mediaPlayer1 != null) {
            if (mediaPlayer1.isPlaying()) {
                mediaPlayer1.stop();
            }
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }
        if (mediaPlayer2 != null) {
            if (mediaPlayer2.isPlaying()) {
                mediaPlayer2.stop();
            }
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        if (mediaPlayer3 != null) {
            if (mediaPlayer3.isPlaying()) {
                mediaPlayer3.stop();
            }
            mediaPlayer3.release();
            mediaPlayer3 = null;
        }

        mMediaAudioFocusManager.releaseAudioFocus();
        mSystemAudioFocusManager.releaseAudioFocus();

        audioFocusManagerLib1.releaseAudioFocus();
        audioFocusManagerLib2.releaseAudioFocus();
        audioFocusManagerLib3.releaseAudioFocus();
    }
}