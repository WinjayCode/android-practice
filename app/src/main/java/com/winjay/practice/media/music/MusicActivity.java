package com.winjay.practice.media.music;

import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.MediaSessionHelper;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.interfaces.IMediaStatus;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.media.interfaces.SourceType;
import com.winjay.practice.media.notification.MusicNotificationManager;
import com.winjay.practice.media.receiver.MediaNotificationReceiver;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MusicActivity extends BaseActivity implements IMediaStatus {
    private static final String TAG = MusicActivity.class.getSimpleName();
    private final String assetsDir = "audio";
    private MediaPlayer musicPlayer;
    private AudioFocusManager mAudioFocusManager;
    private MediaSessionHelper mMediaSessionHelper;
    private MediaNotificationReceiver mMediaNotificationReceiver;
    private String[] mAssetsMusicList;
    private int mCurrentIndex = 0;

    @BindView(R.id.music_title_tv)
    TextView mMusicTitleTV;

    @BindView(R.id.play_pause_iv)
    ImageView play_pause_iv;

    private int mSourceType;

    @Override
    protected int getLayoutId() {
        return R.layout.music_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        musicPlayer = new MediaPlayer();
        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
        mMediaSessionHelper = new MediaSessionHelper(this);
        mAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
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

        if (getIntent().hasExtra("audio")) {
            mSourceType = SourceType.USB_TYPE;
            AudioBean audio = (AudioBean) getIntent().getSerializableExtra("audio");
            if (audio != null && !TextUtils.isEmpty(audio.getPath())) {
                if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                    mMediaSessionHelper.registerMediaButton(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
                    setLocalSource(audio.getPath());
                }
                mMusicTitleTV.setText(audio.getDisplayName());
                MusicNotificationManager.getInstance(this).setMediaData(audio);
            }
        } else {
            mSourceType = SourceType.ASSETS_TYPE;
            getAssetsSource();
            if (mAssetsMusicList.length > 0) {
                if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                    mMediaSessionHelper.registerMediaButton(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
                    setAssetsSource();
                }
            }
        }
    }

    private void setLocalSource(String path) {
        try {
            musicPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playMusic();
    }

    private void getAssetsSource() {
        try {
            mAssetsMusicList = getAssets().list(assetsDir);
            LogUtil.d(TAG, "mAssetsMusicList=" + mAssetsMusicList.length);
            for (String file : mAssetsMusicList) {
                LogUtil.d(TAG, "assetsFile=" + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAssetsSource() {
        LogUtil.d(TAG, "assets=" + mAssetsMusicList[mCurrentIndex]);
        musicPlayer.reset();
        try {
            AssetFileDescriptor afd = getAssets().openFd(assetsDir + File.separator + mAssetsMusicList[mCurrentIndex]);
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        playMusic();
        mMusicTitleTV.setText(mAssetsMusicList[mCurrentIndex]);

        AudioBean audioBean = new AudioBean();
        audioBean.setDisplayName(mAssetsMusicList[mCurrentIndex]);
        MusicNotificationManager.getInstance(this).setMediaData(audioBean);
    }

    private void playMusic() {
        musicPlayer.prepareAsync();
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + musicPlayer.getDuration());
                musicPlayer.start();
                MusicNotificationManager.getInstance(MusicActivity.this).showMusicNotification(true);
            }
        });
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG);
                next();
//                mAudioFocusManager.releaseAudioFocus();
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

    @OnClick(R.id.play_pause_iv)
    void playPauseSwitch() {
        if (musicPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @OnClick(R.id.prev_iv)
    void prevClick() {
        prev();
    }

    @OnClick(R.id.next_iv)
    void nextClick() {
        next();
    }

    @Override
    public void prev() {
        LogUtil.d(TAG);
        --mCurrentIndex;
        if (mSourceType == SourceType.ASSETS_TYPE) {
            if (mCurrentIndex < 0) {
                mCurrentIndex = mAssetsMusicList.length;
            }
            setAssetsSource();
        } else if (mSourceType == SourceType.USB_TYPE) {

        }
    }

    @Override
    public void next() {
        LogUtil.d(TAG);
        ++mCurrentIndex;
        if (mSourceType == SourceType.ASSETS_TYPE) {
            if (mCurrentIndex > mAssetsMusicList.length - 1) {
                mCurrentIndex = 0;
            }
            setAssetsSource();
        } else if (mSourceType == SourceType.USB_TYPE) {

        }
    }

    @Override
    public void play() {
        LogUtil.d(TAG);
        musicPlayer.start();
        play_pause_iv.setImageResource(android.R.drawable.ic_media_pause);
        MusicNotificationManager.getInstance(this).showMusicNotification(true);
    }

    @Override
    public void pause() {
        LogUtil.d(TAG);
        musicPlayer.pause();
        play_pause_iv.setImageResource(android.R.drawable.ic_media_play);
        MusicNotificationManager.getInstance(this).showMusicNotification(false);
    }

    private void registerReceiver() {
        if (mMediaNotificationReceiver == null) {
            mMediaNotificationReceiver = new MediaNotificationReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE);
        registerReceiver(mMediaNotificationReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mMediaNotificationReceiver != null) {
            unregisterReceiver(mMediaNotificationReceiver);
            mMediaNotificationReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
            }
            musicPlayer.release();
            musicPlayer = null;
        }
        mAudioFocusManager.releaseAudioFocus();
        mMediaSessionHelper.unRegisterMediaButton();
        unregisterReceiver();
        MusicNotificationManager.getInstance(getApplicationContext()).cancel();
    }
}
