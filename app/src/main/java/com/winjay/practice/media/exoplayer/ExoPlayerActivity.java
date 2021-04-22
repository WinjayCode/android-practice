package com.winjay.practice.media.exoplayer;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.OnClick;

/**
 * exoplayer
 *
 * @author Winjay
 * @date 2020/8/21
 */
public class ExoPlayerActivity extends BaseActivity {
    private static final String TAG = ExoPlayerActivity.class.getSimpleName();
    private SimpleExoPlayer exoPlayer;

    //    private String url = "http://apis.dui.ai/resource/ae4a3a1c0c8817?productId=278579724";
    private String url = "http://apis.dui.ai/resource/ae4a3a1c0d8310?productId=278579724";

    private AudioFocusManager mAudioFocusManager;

    @Override
    protected int getLayoutId() {
        return R.layout.exoplayer_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
        mAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "AUDIOFOCUS_GAIN");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                }
            }
        });
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
            initializePlayer();
        }
        ;
    }

    @OnClick(R.id.start)
    void start(View view) {
        exoPlayer.play();

//        if (mediaSource != null) {
//            exoPlayer.prepare(mediaSource);
//        }
    }

    @OnClick(R.id.pause)
    void pause(View view) {
        exoPlayer.pause();

//        exoPlayer.setPlayWhenReady(false);
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    @OnClick(R.id.resume)
    void resume(View view) {
        exoPlayer.play();

//        exoPlayer.setPlayWhenReady(true);
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    @OnClick(R.id.stop)
    void stop(View view) {
        exoPlayer.stop();
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    private void initializePlayer() {
        // 新版本实现方式
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        // 网络资源
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        // R.raw.xxx
        // assets目录文件

        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();


        // 老版本实现方式
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
//
//
//        // 网络资源
////        mediaSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
//
//        // 使用缓存
//        mediaSource = new ExtractorMediaSource(Uri.parse(url), new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024, dataSourceFactory), new DefaultExtractorsFactory(), null, null);
//
//        // R.raw.xxx
////        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.a));
////        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
////        try {
////            rawResourceDataSource.open(dataSpec);
////        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
////            e.printStackTrace();
////        }
////        mediaSource = new ExtractorMediaSource(rawResourceDataSource.getUri(), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
//
//        // assets目录文件
////        mediaSource = new ExtractorMediaSource(Uri.parse("asset:///audio/0.mp3"), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
//        exoPlayer.prepare(mediaSource);
//        exoPlayer.setPlayWhenReady(true);
//        exoPlayer.addListener(listener);
    }

    private Player.EventListener listener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            LogUtil.d(TAG);
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            LogUtil.d(TAG);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            LogUtil.d(TAG);
        }

        @Override
        public void onStaticMetadataChanged(List<Metadata> metadataList) {
            LogUtil.d(TAG);
        }

        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            LogUtil.d(TAG, "isLoading=" + isLoading);
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            LogUtil.d(TAG, "state=" + state);
            switch (state) {
                // 空闲
                case Player.STATE_IDLE:
                    break;
                // 缓冲中
                case Player.STATE_BUFFERING:
                    break;
                // 准备好
                case Player.STATE_READY:
                    LogUtil.d(TAG, "duration=" + exoPlayer.getDuration());
                    break;
                // 结束
                case Player.STATE_ENDED:
                    mAudioFocusManager.releaseAudioFocus();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            LogUtil.d(TAG, "playWhenReady=" + playWhenReady + ", reason=" + reason);
        }

        @Override
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
            LogUtil.d(TAG, "playbackSuppressionReason=" + playbackSuppressionReason);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            LogUtil.d(TAG, "isPlaying=" + isPlaying);
            if (isPlaying) {
                // Active playback.
            } else {
                // Not playing because playback is paused, ended, suppressed, or the player
                // is buffering, stopped or failed. Check player.getPlayWhenReady,
                // player.getPlaybackState, player.getPlaybackSuppressionReason and
                // player.getPlaybackError for details.
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            LogUtil.d(TAG, "repeatMode=" + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            LogUtil.d(TAG, "shuffleModeEnabled=" + shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            LogUtil.d(TAG, "error=" + error.getMessage());
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                IOException cause = error.getSourceException();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // HTTP错误。
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // 这是发生错误的请求。
                    DataSpec requestDataSpec = httpError.dataSpec;
                    // 通过强制类型转换错误查询原因，可以找到更多关于错误的信息。
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        //转换为InvalidResponseCodeException并检索响应代码、消息和报头。
                    } else {
                        //尝试调用httpError.getCause()来检索底层的原因，尽管注意它可能是空的。
                    }
                }
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            LogUtil.d(TAG, "reason=" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            LogUtil.d(TAG, "playbackParameters=" + playbackParameters.toString());
        }

        @Override
        public void onExperimentalOffloadSchedulingEnabledChanged(boolean offloadSchedulingEnabled) {
            LogUtil.d(TAG, "offloadSchedulingEnabled=" + offloadSchedulingEnabled);
        }

        @Override
        public void onExperimentalSleepingForOffloadChanged(boolean sleepingForOffload) {
            LogUtil.d(TAG, "sleepingForOffload=" + sleepingForOffload);
        }

        @Override
        public void onEvents(Player player, Player.Events events) {
            LogUtil.d(TAG);
        }
    };

//    private Player.EventListener listener = new Player.EventListener() {
//
//        @Override
//        public void onTimelineChanged(Timeline timeline, Object manifest) {
//            LogUtil.d(TAG, "onTimelineChanged()");
//        }
//
//        @Override
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//            LogUtil.d(TAG, "onTracksChanged()");
//        }
//
//        @Override
//        public void onLoadingChanged(boolean isLoading) {
//            LogUtil.d(TAG, "onLoadingChanged()>>>isLoading=" + isLoading);
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            LogUtil.d(TAG, "onPlayerStateChanged()>>>playWhenReady=" + playWhenReady + ", playbackState=" + playbackState);
//            switch (playbackState) {
//                // 空闲
//                case Player.STATE_IDLE:
//                    break;
//                // 缓冲中
//                case Player.STATE_BUFFERING:
//                    break;
//                // 准备好
//                case Player.STATE_READY:
//                    LogUtil.d(TAG, "duration=" + exoPlayer.getDuration());
//                    break;
//                // 结束
//                case Player.STATE_ENDED:
//                    mAudioFocusManager.releaseAudioFocus();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        @Override
//        public void onRepeatModeChanged(int repeatMode) {
//            LogUtil.d(TAG, "onRepeatModeChanged()>>>repeatMode=" + repeatMode);
//        }
//
//        @Override
//        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//            LogUtil.d(TAG, "onShuffleModeEnabledChanged()>>>shuffleModeEnabled=" + shuffleModeEnabled);
//        }
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {
//            // 报错
//            switch (error.type) {
//                case ExoPlaybackException.TYPE_SOURCE:
//                    // 加载资源时出错
//                    LogUtil.d(TAG, "onPlayerError()>>>加载资源时出错!");
//                    break;
//                case ExoPlaybackException.TYPE_RENDERER:
//                    // 渲染时出错
//                    LogUtil.d(TAG, "onPlayerError()>>>渲染时出错!");
//                    break;
//                case ExoPlaybackException.TYPE_UNEXPECTED:
//                    // 意外的错误
//                    LogUtil.d(TAG, "onPlayerError()>>>意外的错误!");
//                    break;
//                default:
//                    LogUtil.d(TAG, "onPlayerError()");
//                    break;
//            }
//        }
//
//        @Override
//        public void onPositionDiscontinuity(int reason) {
//            LogUtil.d(TAG, "onPositionDiscontinuity()>>>reason=" + reason);
//        }
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//            LogUtil.d(TAG, "onPlaybackParametersChanged()");
//        }
//
//        @Override
//        public void onSeekProcessed() {
//            LogUtil.d(TAG, "onSeekProcessed()");
//        }
//    };

    public void clearCache() {
        LogUtil.d(TAG, "clearCache()");
        FileUtil.delete(getCacheDir() + File.separator + "exoplayer_cache");
    }

    private void release() {
        LogUtil.d(TAG, "release()");
        if (exoPlayer != null) {
            exoPlayer.removeListener(listener);
            listener = null;
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCache();
        release();
        mAudioFocusManager.releaseAudioFocus();
    }
}
