/*
package com.winjay.practice.media.music;


import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaService extends MediaBrowserServiceCompat {
    private static final String TAG = "TEST_MediaService";

    */
/**
     * 媒体会话，受控端
     * 设置session 到界面响应
     *//*

    private MediaSessionCompat mediaSession;
    */
/**
     * 返回到界面的播放状态/播放的ID3信息
     *//*

    private PlaybackStateCompat mPlaybackState;

    */
/**
     * 当前播放下标
     *//*

    private int currentPostion = 0;
    */
/**
     *
     *//*

    private String parentId;
    */
/**
     * 当前播放列表
     *//*

    private List<MusicData> mPlayBeanList = MusicListData.getPlayList();

    boolean isHaveAudioFocus = false;
    private AudioManager mAudioManager;

    */
/**
     * 进度条更新时间1s
     *//*

    private static final int UPDATE_TIME = 1000;

    private final Handler mHandler = new Handler();

    */
/**
     * 音乐进度条
     *//*

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (null == mMediaPlayer || null == mPlaybackState || null == mediaSession) {
                Log.d(TAG, "run: mMediaPlayer = " + mMediaPlayer + "  mPlaybackState = " + mPlaybackState + "   mediaSession = " + mediaSession);
                return;
            }
            if (!mMediaPlayer.isPlaying()) {
                Log.d(TAG, "run: not playing");
                return;
            }
            Log.d(TAG, "run: currentPostion = " + mMediaPlayer.getCurrentPosition() + "  duration = " + mMediaPlayer.getDuration());
            Bundle extras = new Bundle();
            extras.putLong("currentPostion", mMediaPlayer.getCurrentPosition());
            extras.putLong("duration", mMediaPlayer.getDuration());
            sendPlaybackState(PlaybackStateCompat.STATE_PLAYING, extras);
            mHandler.postDelayed(mRunnable, UPDATE_TIME);
        }
    };

    */
/**
     * 播放器
     *//*

    private MediaPlayer mMediaPlayer;
    */
/**
     * 准备就绪？
     *//*

    private boolean isPrepare = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, currentPostion, 1.0f)
                .setActions(getAvailableActions(PlaybackStateCompat.STATE_NONE))
                .build();

        //初始化，第一个参数为context，第二个参数为String类型tag，这里就设置为类名了
        mediaSession = new MediaSessionCompat(this, "MediaService");
        //设置token
        setSessionToken(mediaSession.getSessionToken());
        //设置callback，这里的callback就是客户端对服务指令到达处
        mediaSession.setCallback(mCallback);
        mediaSession.setActive(true);

        //初始化播放器
        mMediaPlayer = new MediaPlayer();
        //准备监听
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: ");
                isPrepare = true;
                // 准备就绪
                sendPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
                handlePlay();
            }
        });
        //播放完成监听
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ");
                sendPlaybackState(PlaybackStateCompat.STATE_NONE);
                // 播放完成 重置 播放器
//                mMediaPlayer.reset();
                // 下一曲
                mCallback.onSkipToNext();
            }
        });
        //播放错误监听
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "onError:  what = " + what + "   extra = " + extra);
                isPrepare = false;
                sendPlaybackState(PlaybackStateCompat.STATE_ERROR);
                // 播放错误 重置 播放器
                mMediaPlayer.reset();
                return false;
            }
        });

        // 设置音频流类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置声音
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (null != mAudioManager) {
            int mVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(mVolumn, mVolumn);
        }
    }

    //mediaSession设置的callback，也是客户端控制指令所到达处
    private final MediaSessionCompat.Callback mCallback = new MediaSessionCompat.Callback() {
        //重写的方法都是选择性重写的，不完全列列举，具体可以查询文章末尾表格
        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "onPlay: isPrepare = " + isPrepare);
            //客户端mMediaController.getTransportControls().play()就会调用到这里，以下类推
            //处理播放逻辑
            //处理完成后通知客户端更新，这里就会回调给客户端的MediaController.Callback
            if (null != mMediaPlayer && !isPrepare) {
                handleOpenUri(MusicListData.rawToUri(MediaService.this, Objects.requireNonNull(getPlayBean()).mediaId));
            } else {
                handlePlay();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause: ");
            handlePause(true);
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            //设置到指定进度时触发
        }

        @Override
        public void onSkipToPrevious() {
            int pos = (currentPostion + mPlayBeanList.size() - 1) % mPlayBeanList.size();
            Log.e(TAG, "onSkipToPrevious  pos = " + pos);
            handleOpenUri(MusicListData.rawToUri(MediaService.this, Objects.requireNonNull(setPlayPosition(pos)).mediaId));
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();

            //下一首
            //通知媒体信息改变
//            mediaSession.setMetadata(mediaMetadata);
            int pos = (currentPostion + 1) % mPlayBeanList.size();
            Log.d(TAG, "onSkipToNext: pos = " + pos);
            handleOpenUri(MusicListData.rawToUri(MediaService.this, Objects.requireNonNull(setPlayPosition(pos)).mediaId));
        }

        */
/**
         * 响应MediaControllerCompat.getTransportControls().playFromUri
         *
         * @param uri uri
         * @param extras extras
         *//*

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(TAG, "onPlayFromUri");
            int position = extras.getInt("playPosition");
            setPlayPosition(position);
            handleOpenUri(uri);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            //自定义指令发送到的地方
            //对应客户端 mMediaController.getTransportControls().sendCustomAction(...)
        }

    };

    */
/**
     * 控制客户端 链接
     *
     * @param clientPackageName clientPackageName
     * @param clientUid         clientUid
     * @param rootHints         rootHints
     * @return BrowserRoot
     *//*

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@android.support.annotation.NonNull @NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //MediaBrowserService必须重写的方法，第一个参数为客户端的packageName，第二个参数为Uid
        //第三个参数是从客户端传递过来的Bundle。
        //通过以上参数来进行判断，若同意连接，则返回BrowserRoot对象，否则返回null;
        //构造BrowserRoot的第一个参数为rootId(自定义)，第二个参数为Bundle;
        Log.d(TAG, "onGetRoot: clientPackageName = " + clientPackageName + "   clientUid = " + clientUid);
        return new BrowserRoot(clientPackageName, null);
    }

    */
/**
     * 播放列表
     *
     * @param parentId parentId
     * @param result   result
     *//*

    @Override
    public void onLoadChildren(@android.support.annotation.NonNull @NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //MediaBrowserService必须重写的方法，用于处理订阅信息，文章后面会提及
        // 设置播放列表
        Log.e(TAG, "onLoadChildren-------- parentId = " + parentId);
        this.parentId = parentId;
        // 将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach();

        // 我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = MusicListData.transformPlayList(mPlayBeanList);

        // 向Browser发送 播放列表数据
        result.sendResult(mediaItems);
        // 刷新 播放数据数据
//        notifyChildrenChanged(parentId);
        // 模拟定时加载数据
        if (mediaItems.size() == 2) {
            getSyncData();
        }
    }


    */
/**
     * 模拟刷新数据
     *//*

    private void getSyncData() {
        Log.d(TAG, "getSyncData: ");

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: load data for update ");
                mPlayBeanList = MusicListData.getPlayListUpdate();
                currentPostion = 0;
                notifyChildrenChanged(parentId);
            }
        }, 30000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPrepare = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
    }

    */
/**
     * 申请焦点
     *
     * @return 焦点
     *//*

    private int requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
        return result;
    }


    */
/**
     * 释放焦点
     *//*

    private void abandAudioFocus() {
        int result = mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
    }


    */
/**
     * open
     *
     * @param uri uri
     *//*

    private void handleOpenUri(Uri uri) {
        Log.d(TAG, "handleOpenUri: uri = " + uri);
        if (uri == null) {
            Log.d(TAG, "handlePlayUri: URL == NULL ");
            return;
        }
        if (requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d(TAG, "handlePlayUri: requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED");
            return;
        }
        isPrepare = false;
        mMediaPlayer.reset();
        mMediaPlayer.setLooping(false);
        try {
            mMediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            Log.e(TAG, "handlePlayUri: ", e);
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    */
/**
     * 播放
     *//*

    private void handlePlay() {
        Log.d(TAG, "handlePlay: play   isPrepare = " + isPrepare);

        if (null == mMediaPlayer || !isPrepare) {
            Log.d(TAG, "handlePlay: null == mMediaPlayer || isPrepare " + isPrepare);
            return;
        }
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, UPDATE_TIME);

        sendPlaybackState(PlaybackStateCompat.STATE_CONNECTING);
        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
        mediaSession.setMetadata(MusicListData.transformPlayBean(Objects.requireNonNull(getPlayBean())));
    }

    */
/**
     * 暂停
     *
     * @param isAbandFocus 焦点
     *//*

    private void handlePause(boolean isAbandFocus) {
        Log.d(TAG, "handlePause: isAbandFocus = " + isAbandFocus);
        if (mMediaPlayer == null || !isPrepare) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mHandler.removeCallbacks(mRunnable);
        sendPlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }


    */
/**
     * 当前播放 歌曲
     *
     * @return playbean
     *//*

    private MusicData getPlayBean() {
        if (currentPostion >= 0 && currentPostion < mPlayBeanList.size()) {
            return mPlayBeanList.get(currentPostion);
        }
        return null;
    }

    */
/**
     * 设置列表 播放下标
     *
     * @param pos pos
     * @return playbean
     *//*

    private MusicData setPlayPosition(int pos) {
        if (pos >= 0 && pos < mPlayBeanList.size()) {
            currentPostion = pos;
            return mPlayBeanList.get(currentPostion);
        }
        return null;
    }


    */
/**
     * Set the current capabilities available on this session. This should
     * use a bitmask of the available capabilities.
     *
     * @param state 歌曲状态
     * @return 可用的操作Actions
     *//*

    public long getAvailableActions(int state) {
        long actions = PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_REWIND
                | PlaybackStateCompat.ACTION_FAST_FORWARD
                | PlaybackStateCompat.ACTION_SEEK_TO;
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    */
/**
     * 音源切换监听
     *//*

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "onAudioFocusChange  focusChange = " + focusChange + ", before isHaveAudioFocus = " +
                    isHaveAudioFocus);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    // 音源丢失
                    isHaveAudioFocus = false;
                    mCallback.onPause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // 音源短暂丢失
                    isHaveAudioFocus = false;
                    Log.d(TAG, " AUDIOFOCUS_LOSS_TRANSIENT  ");
                    handlePause(false);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //  降低音量
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 获得音源
                    isHaveAudioFocus = true;
                    mCallback.onPlay();
                    break;
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    // 音源申请失败
                    break;
                default:
                    break;
            }
        }
    };

    */
/**
     * 发送歌曲状态
     *
     * @param state 状态
     *//*

    private void sendPlaybackState(int state) {
        sendPlaybackState(state, null);
    }

    */
/**
     * 发送歌曲状态
     *
     * @param state  状态
     * @param extras 参数
     *//*

    private void sendPlaybackState(int state, Bundle extras) {
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(state, currentPostion, 1.0f)
                .setActions(getAvailableActions(state))
                .setExtras(extras)
                .build();
        mediaSession.setPlaybackState(mPlaybackState);
    }

}
*/
