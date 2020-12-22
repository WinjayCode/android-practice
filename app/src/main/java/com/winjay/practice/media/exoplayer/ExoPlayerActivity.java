package com.winjay.practice.media.exoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;

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
    private MediaSource mediaSource;

//    private String url = "http://apis.dui.ai/resource/ae4a3a1c0c8817?productId=278579724";
    private String url = "http://apis.dui.ai/resource/ae4a3a1c0d8310?productId=278579724";

    @Override
    protected int getLayoutId() {
        return R.layout.exoplayer_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCache();
        release();
    }

    @OnClick(R.id.start)
    void start(View view) {
        if (mediaSource != null) {
            exoPlayer.prepare(mediaSource);
        }
    }

    @OnClick(R.id.pause)
    void pause(View view) {
        exoPlayer.setPlayWhenReady(false);
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    @OnClick(R.id.resume)
    void resume(View view) {
        exoPlayer.setPlayWhenReady(true);
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    @OnClick(R.id.stop)
    void stop(View view) {
        exoPlayer.stop();
        LogUtil.d(TAG, exoPlayer.getPlaybackState());
    }

    private void initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));


        // 网络资源
//        mediaSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, new DefaultExtractorsFactory(), null, null);

        // 使用缓存
        mediaSource = new ExtractorMediaSource(Uri.parse(url), new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024, dataSourceFactory), new DefaultExtractorsFactory(), null, null);

        // R.raw.xxx
//        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.a));
//        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
//        try {
//            rawResourceDataSource.open(dataSpec);
//        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
//            e.printStackTrace();
//        }
//        mediaSource = new ExtractorMediaSource(rawResourceDataSource.getUri(), dataSourceFactory, new DefaultExtractorsFactory(), null, null);

        // assets目录文件
//        mediaSource = new ExtractorMediaSource(Uri.parse("asset:///audio/0.mp3"), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(listener);
    }

    private Player.EventListener listener = new Player.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            LogUtil.d(TAG, "onTimelineChanged()");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            LogUtil.d(TAG, "onTracksChanged()");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            LogUtil.d(TAG, "onLoadingChanged()>>>isLoading=" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtil.d(TAG, "onPlayerStateChanged()>>>playWhenReady=" + playWhenReady + ", playbackState=" + playbackState);
            switch (playbackState) {
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
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            LogUtil.d(TAG, "onRepeatModeChanged()>>>repeatMode=" + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            LogUtil.d(TAG, "onShuffleModeEnabledChanged()>>>shuffleModeEnabled=" + shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // 报错
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    // 加载资源时出错
                    LogUtil.d(TAG, "onPlayerError()>>>加载资源时出错!");
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    // 渲染时出错
                    LogUtil.d(TAG, "onPlayerError()>>>渲染时出错!");
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    // 意外的错误
                    LogUtil.d(TAG, "onPlayerError()>>>意外的错误!");
                    break;
                default:
                    LogUtil.d(TAG, "onPlayerError()");
                    break;
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            LogUtil.d(TAG, "onPositionDiscontinuity()>>>reason=" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            LogUtil.d(TAG, "onPlaybackParametersChanged()");
        }

        @Override
        public void onSeekProcessed() {
            LogUtil.d(TAG, "onSeekProcessed()");
        }
    };

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
}
