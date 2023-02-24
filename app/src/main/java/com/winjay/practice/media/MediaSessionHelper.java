package com.winjay.practice.media;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.winjay.practice.media.receiver.MediaButtonIntentReceiver;
import com.winjay.practice.utils.LogUtil;

/**
 * 多媒体按键监听
 * <p>
 * 2023.2.24 废弃！使用最新的多媒体应用架构
 *
 * @author Winjay
 * @date 21/02/3
 */
@Deprecated
public class MediaSessionHelper {
    private static final String TAG = MediaSessionHelper.class.getSimpleName();
    private Context mContext;
    private AudioManager mAudioManager;
    private MediaSession mMediaSession;
    private ComponentName mComponentName;
    private PlaybackState.Builder mPlaybackStateBuilder;

    public MediaSessionHelper(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(context.getPackageName(), MediaButtonIntentReceiver.class.getName());
    }

    /**
     * 注册多媒体按键监听
     *
     * @param focusResult
     */
    public void registerMediaButton(int focusResult) {
        LogUtil.d(TAG, "focusResult=" + focusResult);
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == focusResult) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //注册媒体按键 API 21+（Android 5.0）
                if (mMediaSession == null) {
                    setMediaButtonEvent();
                }
            } else {
                //注册媒体按键 API 21 以下， 通常的做法
                mAudioManager.registerMediaButtonEventReceiver(mComponentName);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setMediaButtonEvent() {
        LogUtil.d(TAG);
        mMediaSession = new MediaSession(mContext, "Winjay_MediaSession");
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                LogUtil.d(TAG);
                //这里处理播放器逻辑 播放
                updatePlaybackState(true);//播放暂停更新控制中心播放状态
            }

            @Override
            public void onPause() {
                super.onPause();
                LogUtil.d(TAG);
                //这里处理播放器逻辑 暂停
                updatePlaybackState(false);//播放暂停更新控制中心播放状态
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                LogUtil.d(TAG);
                //CMD NEXT 这里处理播放器逻辑 下一曲
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                LogUtil.d(TAG);
                //这里处理播放器逻辑 上一曲
            }

            @Override
            public void onStop() {
                super.onStop();
                LogUtil.d(TAG);
                // 播放停止
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
                LogUtil.d(TAG);
                // 快进
            }

            @Override
            public void onRewind() {
                super.onRewind();
                LogUtil.d(TAG);
                // 快退
            }

            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                LogUtil.d(TAG, "mediaButtonIntent=" + mediaButtonIntent.getAction());
//                if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())) {
//                    KeyEvent ke = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//                    if (ke != null && ke.getAction() == KeyEvent.ACTION_DOWN) {
//                        switch (ke.getKeyCode()) {
//                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                                LogUtil.d(TAG, "pause");
//                                break;
//                        }
//                    }
//                }
                return super.onMediaButtonEvent(mediaButtonIntent);
//                return true;
            }
        });
        //FLAG_HANDLES_MEDIA_BUTTONS 控制媒体按钮
        //FLAG_HANDLES_TRANSPORT_CONTROLS 控制传输命令
        //FLAG_EXCLUSIVE_GLOBAL_PRIORITY 优先级最高的，会在activity处理之前先处理，
        //且不需要配合MediaPlayer使用。（注：Android8.1开始MediaSession必须配合MediaPlayer使用，之后会有说明）
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mPlaybackStateBuilder = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY
                        | PlaybackState.ACTION_PLAY_PAUSE
                        | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackState.ACTION_PAUSE
                        | PlaybackState.ACTION_SKIP_TO_NEXT
                        | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_NONE, 0, 1.0f);
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        //MediaSession必须激活才能去使用，当你不希望使用MediaSession的是，可以设置false
        mMediaSession.setActive(true);
    }

    /*
     * update mediaCenter state
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updatePlaybackState(boolean isPlaying) {
        if (isPlaying) {
            mPlaybackStateBuilder.setState(PlaybackState.STATE_PLAYING,
                    PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                    SystemClock.elapsedRealtime());
        } else {
            mPlaybackStateBuilder.setState(PlaybackState.STATE_PAUSED,
                    PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                    SystemClock.elapsedRealtime());
        }
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
    }

    //点击播放 注册监听后调用此方法才能将媒体焦点抢过来
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void updateMediaCenterInfo(String title, String artist) {
        if (mMediaSession == null) {
            return;
        }
        MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        // 歌曲名
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        // 歌手
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, artist);
        mMediaSession.setMetadata(metadataBuilder.build());
        updatePlaybackState(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void unRegisterMediaButton() {
        LogUtil.d(TAG);
        if (mComponentName != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
            mComponentName = null;
        }
        if (mMediaSession != null) {
            mMediaSession.setCallback(null);
            mMediaSession.setActive(false);
            mMediaSession.release();
        }
    }
}
