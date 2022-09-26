package com.winjay.practice.media.exoplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.DebugTextViewHelper;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoSize;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ExoplayerActivityBinding;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;

/**
 * exoplayer
 *
 * @author Winjay
 * @date 2020/8/21
 */
public class ExoPlayerActivity extends BaseActivity implements View.OnClickListener, StyledPlayerView.ControllerVisibilityListener {
    private static final String TAG = "ExoPlayerActivity";
    private ExoplayerActivityBinding binding;
//    private static final String KEY_TRACK_SELECTION_PARAMETERS = "track_selection_parameters";
//    private static final String KEY_SERVER_SIDE_ADS_LOADER_STATE = "server_side_ads_loader_state";
//    private static final String KEY_ITEM_INDEX = "item_index";
//    private static final String KEY_POSITION = "position";
//    private static final String KEY_AUTO_PLAY = "auto_play";

    protected @Nullable
    ExoPlayer player;

    private boolean isShowingTrackSelectionDialog;
    private DataSource.Factory dataSourceFactory;
//    private List<MediaItem> mediaItems;
//    private TrackSelectionParameters trackSelectionParameters;
    private DebugTextViewHelper debugViewHelper;
    private Tracks lastSeenTracks;
    private boolean startAutoPlay;
//    private int startItemIndex;
//    private long startPosition;

    // For ad playback only.

//    @Nullable
//    private AdsLoader clientSideAdsLoader;


    //    private String url = "http://apis.dui.ai/resource/ae4a3a1c0c8817?productId=278579724";
    private String url = "http://apis.dui.ai/resource/ae4a3a1c0d8310?productId=278579724";

//    private AudioFocusManager mAudioFocusManager;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ExoplayerActivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        updateTrackSelectorParameters();
//        updateStartPosition();
//        outState.putBundle(KEY_TRACK_SELECTION_PARAMETERS, trackSelectionParameters.toBundle());
//        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
//        outState.putInt(KEY_ITEM_INDEX, startItemIndex);
//        outState.putLong(KEY_POSITION, startPosition);
//        saveServerSideAdsLoaderState(outState);
//    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
//        releaseClientSideAdsLoader();
//        clearStartPosition();
        setIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.selectTracksButton.setOnClickListener(this);

        binding.playerView.setControllerVisibilityListener(this);
        binding.playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        binding.playerView.requestFocus();

//        if (savedInstanceState != null) {
//            trackSelectionParameters =
//                    TrackSelectionParameters.fromBundle(
//                            savedInstanceState.getBundle(KEY_TRACK_SELECTION_PARAMETERS));
//            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
//            startItemIndex = savedInstanceState.getInt(KEY_ITEM_INDEX);
//            startPosition = savedInstanceState.getLong(KEY_POSITION);
//            restoreServerSideAdsLoaderState(savedInstanceState);
//        } else {
//            trackSelectionParameters = new TrackSelectionParameters.Builder(/* context= */ this).build();
//            clearStartPosition();
//        }
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<PlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(PlaybackException e) {
            String errorString = getString(R.string.error_generic);
            Throwable cause = e.getCause();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.codecInfo == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString =
                                getString(
                                        R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                    } else {
                        errorString =
                                getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                    }
                } else {
                    errorString =
                            getString(
                                    R.string.error_instantiating_decoder,
                                    decoderInitializationException.codecInfo.name);
                }
            }
            return Pair.create(0, errorString);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
        binding.playerView.onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
            binding.playerView.onResume();
        }
    }

    /**
     * @return Whether initialization was successful.
     */
    protected boolean initializePlayer() {
        if (player == null) {
            Intent intent = getIntent();

//            mediaItems = createMediaItems(intent);
//            if (mediaItems.isEmpty()) {
//                return false;
//            }

            lastSeenTracks = Tracks.EMPTY;
            ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(/* context= */ this);
//            ExoPlayer.Builder playerBuilder =
//                    new ExoPlayer.Builder(/* context= */ this)
//                            .setMediaSourceFactory(createMediaSourceFactory());
//            setRenderersFactory(
//                    playerBuilder, intent.getBooleanExtra(IntentUtil.PREFER_EXTENSION_DECODERS_EXTRA, false));
            player = playerBuilder.build();
            // 设置约束音轨选择的参数
//            player.setTrackSelectionParameters(trackSelectionParameters);
            player.addListener(new PlayerEventListener());
            player.addAnalyticsListener(new EventLogger());
            player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
            player.setPlayWhenReady(startAutoPlay);
            binding.playerView.setPlayer(player);
//            configurePlayerWithServerSideAdsLoader();
            debugViewHelper = new DebugTextViewHelper(player, binding.debugTextView);
            debugViewHelper.start();
        }
//        boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
//        if (haveStartPosition) {
//            player.seekTo(startItemIndex, startPosition);
//        }
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
//        player.setMediaItems(mediaItems, /* resetPosition= */ !haveStartPosition);
        player.prepare();
        updateButtonVisibility();
        return true;
    }

//    private MediaSource.Factory createMediaSourceFactory() {
//        DefaultDrmSessionManagerProvider drmSessionManagerProvider =
//                new DefaultDrmSessionManagerProvider();
//        drmSessionManagerProvider.setDrmHttpDataSourceFactory(
//                DemoUtil.getHttpDataSourceFactory(/* context= */ this));
//        ImaServerSideAdInsertionMediaSource.AdsLoader.Builder serverSideAdLoaderBuilder =
//                new ImaServerSideAdInsertionMediaSource.AdsLoader.Builder(/* context= */ this, playerView);
//        if (serverSideAdsLoaderState != null) {
//            serverSideAdLoaderBuilder.setAdsLoaderState(serverSideAdsLoaderState);
//        }
//        serverSideAdsLoader = serverSideAdLoaderBuilder.build();
//        ImaServerSideAdInsertionMediaSource.Factory imaServerSideAdInsertionMediaSourceFactory =
//                new ImaServerSideAdInsertionMediaSource.Factory(
//                        serverSideAdsLoader,
//                        new DefaultMediaSourceFactory(/* context= */ this)
//                                .setDataSourceFactory(dataSourceFactory));
//        return new DefaultMediaSourceFactory(/* context= */ this)
//                .setDataSourceFactory(dataSourceFactory)
//                .setDrmSessionManagerProvider(drmSessionManagerProvider)
//                .setLocalAdInsertionComponents(
//                        this::getClientSideAdsLoader, /* adViewProvider= */ playerView)
//                .setServerSideAdInsertionMediaSourceFactory(imaServerSideAdInsertionMediaSourceFactory);
//    }

//    private void setRenderersFactory(ExoPlayer.Builder playerBuilder, boolean preferExtensionDecoders) {
//        RenderersFactory renderersFactory =
//                DemoUtil.buildRenderersFactory(/* context= */ this, preferExtensionDecoders);
//        playerBuilder.setRenderersFactory(renderersFactory);
//    }

//    private void configurePlayerWithServerSideAdsLoader() {
//        serverSideAdsLoader.setPlayer(player);
//    }

//    private List<MediaItem> createMediaItems(Intent intent) {
//        String action = intent.getAction();
//        boolean actionIsListView = IntentUtil.ACTION_VIEW_LIST.equals(action);
//        if (!actionIsListView && !IntentUtil.ACTION_VIEW.equals(action)) {
//            toast(getString(R.string.unexpected_intent_action, action));
//            finish();
//            return Collections.emptyList();
//        }
//
//        List<MediaItem> mediaItems =
//                createMediaItems(intent, DemoUtil.getDownloadTracker(/* context= */ this));
//        for (int i = 0; i < mediaItems.size(); i++) {
//            MediaItem mediaItem = mediaItems.get(i);
//
//            if (!Util.checkCleartextTrafficPermitted(mediaItem)) {
//                toast(getResources().getString(R.string.error_cleartext_not_permitted));
//                finish();
//                return Collections.emptyList();
//            }
//            if (Util.maybeRequestReadExternalStoragePermission(/* activity= */ this, mediaItem)) {
//                // The player will be reinitialized if the permission is granted.
//                return Collections.emptyList();
//            }
//
//            MediaItem.DrmConfiguration drmConfiguration = mediaItem.localConfiguration.drmConfiguration;
//            if (drmConfiguration != null) {
//                if (Build.VERSION.SDK_INT < 18) {
//                    toast(getResources().getString(R.string.error_drm_unsupported_before_api_18));
//                    finish();
//                    return Collections.emptyList();
//                } else if (!FrameworkMediaDrm.isCryptoSchemeSupported(drmConfiguration.scheme)) {
//                    toast(getResources().getString(R.string.error_drm_unsupported_scheme));
//                    finish();
//                    return Collections.emptyList();
//                }
//            }
//        }
//        return mediaItems;
//    }

//    private AdsLoader getClientSideAdsLoader(MediaItem.AdsConfiguration adsConfiguration) {
//        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
//        if (clientSideAdsLoader == null) {
//            clientSideAdsLoader = new ImaAdsLoader.Builder(/* context= */ this).build();
//        }
//        clientSideAdsLoader.setPlayer(player);
//        return clientSideAdsLoader;
//    }

//    private void releaseServerSideAdsLoader() {
//        serverSideAdsLoaderState = serverSideAdsLoader.release();
//        serverSideAdsLoader = null;
//    }

//    private void releaseClientSideAdsLoader() {
//        if (clientSideAdsLoader != null) {
//            clientSideAdsLoader.release();
//            clientSideAdsLoader = null;
//            binding.playerView.getAdViewGroup().removeAllViews();
//        }
//    }

//    private void saveServerSideAdsLoaderState(Bundle outState) {
//        if (serverSideAdsLoaderState != null) {
//            outState.putBundle(KEY_SERVER_SIDE_ADS_LOADER_STATE, serverSideAdsLoaderState.toBundle());
//        }
//    }

//    private void restoreServerSideAdsLoaderState(Bundle savedInstanceState) {
//        Bundle adsLoaderStateBundle = savedInstanceState.getBundle(KEY_SERVER_SIDE_ADS_LOADER_STATE);
//        if (adsLoaderStateBundle != null) {
//            serverSideAdsLoaderState =
//                    ImaServerSideAdInsertionMediaSource.AdsLoader.State.CREATOR.fromBundle(
//                            adsLoaderStateBundle);
//        }
//    }

//    private void updateTrackSelectorParameters() {
//        if (player != null) {
//            trackSelectionParameters = player.getTrackSelectionParameters();
//        }
//    }

//    private void updateStartPosition() {
//        if (player != null) {
//            startAutoPlay = player.getPlayWhenReady();
//            startItemIndex = player.getCurrentMediaItemIndex();
//            startPosition = Math.max(0, player.getContentPosition());
//        }
//    }

//    protected void clearStartPosition() {
//        startAutoPlay = true;
//        startItemIndex = C.INDEX_UNSET;
//        startPosition = C.TIME_UNSET;
//    }

    // User controls

    private void updateButtonVisibility() {
        binding.selectTracksButton.setEnabled(player != null && TrackSelectionDialog.willHaveContent(player));
    }

    private void showControls() {
        binding.controlsRoot.setVisibility(View.VISIBLE);
    }

//    private static List<MediaItem> createMediaItems(Intent intent, DownloadTracker downloadTracker) {
//        List<MediaItem> mediaItems = new ArrayList<>();
//        for (MediaItem item : IntentUtil.createMediaItemsFromIntent(intent)) {
//            mediaItems.add(
//                    maybeSetDownloadProperties(
//                            item, downloadTracker.getDownloadRequest(item.localConfiguration.uri)));
//        }
//        return mediaItems;
//    }

//    private static MediaItem maybeSetDownloadProperties(
//            MediaItem item, @Nullable DownloadRequest downloadRequest) {
//        if (downloadRequest == null) {
//            return item;
//        }
//        MediaItem.Builder builder = item.buildUpon();
//        builder
//                .setMediaId(downloadRequest.id)
//                .setUri(downloadRequest.uri)
//                .setCustomCacheKey(downloadRequest.customCacheKey)
//                .setMimeType(downloadRequest.mimeType)
//                .setStreamKeys(downloadRequest.streamKeys);
//        @Nullable
//        MediaItem.DrmConfiguration drmConfiguration = item.localConfiguration.drmConfiguration;
//        if (drmConfiguration != null) {
//            builder.setDrmConfiguration(
//                    drmConfiguration.buildUpon().setKeySetId(downloadRequest.keySetId).build());
//        }
//        return builder.build();
//    }

    private static final int UPDATE_PROGRESS = 1;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    if (player != null) {
                        LogUtil.d(TAG, "currentPosition=" + player.getCurrentPosition());
                        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    }
                    break;
            }
        }
    };

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            LogUtil.d(TAG, "playbackState=" + playbackState);
            switch (playbackState) {
                // 空闲
                case Player.STATE_IDLE:
                    break;
                // 缓冲中
                case Player.STATE_BUFFERING:
                    break;
                // 准备好
                case Player.STATE_READY:
                    if (player != null) {
                        LogUtil.d(TAG, "duration=" + player.getDuration());
                        player.play();
                    }
                    handler.sendEmptyMessage(UPDATE_PROGRESS);
                    break;
                // 结束
                case Player.STATE_ENDED:
                    handler.removeMessages(UPDATE_PROGRESS);
                    showControls();
                    break;
                default:
                    break;
            }

            updateButtonVisibility();
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                player.seekToDefaultPosition();
                player.prepare();
            } else {
                updateButtonVisibility();
                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(Tracks tracks) {
            updateButtonVisibility();
            if (tracks == lastSeenTracks) {
                return;
            }
            if (tracks.containsType(C.TRACK_TYPE_VIDEO)
                    && !tracks.isTypeSupported(C.TRACK_TYPE_VIDEO, /* allowExceedsCapabilities= */ true)) {
                toast(getResources().getString(R.string.error_unsupported_video));
            }
            if (tracks.containsType(C.TRACK_TYPE_AUDIO)
                    && !tracks.isTypeSupported(C.TRACK_TYPE_AUDIO, /* allowExceedsCapabilities= */ true)) {
                toast(getResources().getString(R.string.error_unsupported_audio));
            }
            lastSeenTracks = tracks;
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
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            LogUtil.d(TAG, "playWhenReady=" + playWhenReady + ", reason=" + reason);
            switch (reason) {
                case Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST:
                    LogUtil.d(TAG, "reason=PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST");
                    break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS:
                    LogUtil.d(TAG, "reason=PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS");
                    break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY:
                    LogUtil.d(TAG, "reason=PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY");
                    break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE:
                    LogUtil.d(TAG, "reason=PLAY_WHEN_READY_CHANGE_REASON_REMOTE");
                    break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM:
                    LogUtil.d(TAG, "reason=PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM");
                    break;
                default:
                    break;
            }
            if (playWhenReady) {
                handler.sendEmptyMessage(UPDATE_PROGRESS);
            } else {
                handler.removeMessages(UPDATE_PROGRESS);
            }
        }

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            LogUtil.d(TAG, "reason=" + reason);
            switch (reason) {
                case Player.DISCONTINUITY_REASON_SEEK:
                    LogUtil.d(TAG, "DISCONTINUITY_REASON_SEEK");
                    LogUtil.d(TAG, "oldPosition.contentPositionMs=" + oldPosition.contentPositionMs);
                    LogUtil.d(TAG, "oldPosition.positionMs=" + oldPosition.positionMs);

                    LogUtil.d(TAG, "newPosition.contentPositionMs=" + newPosition.contentPositionMs);
                    LogUtil.d(TAG, "newPosition.positionMs=" + newPosition.positionMs);
                    break;
            }
        }
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        binding.controlsRoot.setVisibility(visibility);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.playerView.onPause();
        releasePlayer();
    }

    protected void releasePlayer() {
        if (player != null) {
//            updateTrackSelectorParameters();
//            updateStartPosition();
//            releaseServerSideAdsLoader();
            debugViewHelper.stop();
            debugViewHelper = null;
            player.release();
            player = null;
            binding.playerView.setPlayer(/* player= */ null);
//            mediaItems = Collections.emptyList();
        }
//        if (clientSideAdsLoader != null) {
//            clientSideAdsLoader.setPlayer(null);
//        } else {
            binding.playerView.getAdViewGroup().removeAllViews();
//        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.selectTracksButton
                && !isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(player)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForPlayer(
                            player,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        releaseClientSideAdsLoader();
//        mAudioFocusManager.releaseAudioFocus();
    }

//    private void requestAudioFocus() {
//        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
//        mAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
//            @Override
//            public void onAudioFocusChange(int focusChange) {
//                switch (focusChange) {
//                    case AudioManager.AUDIOFOCUS_GAIN:
//                        LogUtil.d(TAG, "AUDIOFOCUS_GAIN");
//                        break;
//                    case AudioManager.AUDIOFOCUS_LOSS:
//                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS");
//                        break;
//                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
//                        break;
//                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                        LogUtil.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
//                        break;
//                }
//            }
//        });
//        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
//            initializePlayer();
//        }
//    }
//
//    private void initializePlayer() {
//        // 新版本实现方式
//        exoPlayer = new SimpleExoPlayer.Builder(this).build();
//        // 网络资源
//        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
//        // R.raw.xxx
//        // assets目录文件
//
//        exoPlayer.setMediaItem(mediaItem);
//        exoPlayer.prepare();
//
//
//        // 老版本实现方式
////        exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
////        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
////
////
////        // 网络资源
//////        mediaSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
////
////        // 使用缓存
////        mediaSource = new ExtractorMediaSource(Uri.parse(url), new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024, dataSourceFactory), new DefaultExtractorsFactory(), null, null);
////
////        // R.raw.xxx
//////        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.a));
//////        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
//////        try {
//////            rawResourceDataSource.open(dataSpec);
//////        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
//////            e.printStackTrace();
//////        }
//////        mediaSource = new ExtractorMediaSource(rawResourceDataSource.getUri(), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
////
////        // assets目录文件
//////        mediaSource = new ExtractorMediaSource(Uri.parse("asset:///audio/0.mp3"), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
////        exoPlayer.prepare(mediaSource);
////        exoPlayer.setPlayWhenReady(true);
//        exoPlayer.addListener(listener);
//    }
//
//    private Player.Listener listener = new Player.Listener() {
//        @Override
//        public void onEvents(Player player, Player.Events events) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onTimelineChanged(Timeline timeline, int reason) {
//            LogUtil.d(TAG, "reason=" + reason);
//        }
//
//        @Override
//        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
//            LogUtil.d(TAG, "reason=" + reason);
//        }
//
//        @Override
//        public void onTracksChanged(Tracks tracks) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onIsLoadingChanged(boolean isLoading) {
//            LogUtil.d(TAG, "isLoading=" + isLoading);
//        }
//
//        @Override
//        public void onAvailableCommandsChanged(Player.Commands availableCommands) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onTrackSelectionParametersChanged(TrackSelectionParameters parameters) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onPlaybackStateChanged(int playbackState) {
//            LogUtil.d(TAG, "state=" + playbackState);
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
//                    showControls();
//                    break;
//                default:
//                    break;
//            }
//            updateButtonVisibility();
//        }
//
//        @Override
//        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
//            LogUtil.d(TAG, "playWhenReady=" + playWhenReady + ", reason=" + reason);
//        }
//
//        @Override
//        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
//            LogUtil.d(TAG, "playbackSuppressionReason=" + playbackSuppressionReason);
//        }
//
//        @Override
//        public void onIsPlayingChanged(boolean isPlaying) {
//            LogUtil.d(TAG, "isPlaying=" + isPlaying);
//            if (isPlaying) {
//                // Active playback.
//            } else {
//                // Not playing because playback is paused, ended, suppressed, or the player
//                // is buffering, stopped or failed. Check player.getPlayWhenReady,
//                // player.getPlaybackState, player.getPlaybackSuppressionReason and
//                // player.getPlaybackError for details.
//            }
//        }
//
//        @Override
//        public void onRepeatModeChanged(int repeatMode) {
//            LogUtil.d(TAG, "repeatMode=" + repeatMode);
//        }
//
//        @Override
//        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//            LogUtil.d(TAG, "shuffleModeEnabled=" + shuffleModeEnabled);
//        }
//
//        @Override
//        public void onPlayerError(PlaybackException error) {
//            LogUtil.d(TAG, "error=" + error.getMessage());
//        }
//
//        @Override
//        public void onPlayerErrorChanged(@Nullable PlaybackException error) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
//            LogUtil.d(TAG, "reason=" + reason);
//            switch (reason) {
//                case Player.DISCONTINUITY_REASON_SEEK:
//                    LogUtil.d(TAG, "DISCONTINUITY_REASON_SEEK");
//                    break;
//            }
//        }
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onSeekBackIncrementChanged(long seekBackIncrementMs) {
//            LogUtil.d(TAG, "seekBackIncrementMs=" + seekBackIncrementMs);
//        }
//
//        @Override
//        public void onSeekForwardIncrementChanged(long seekForwardIncrementMs) {
//            LogUtil.d(TAG, "seekBackIncrementMs=" + seekForwardIncrementMs);
//        }
//
//        @Override
//        public void onMaxSeekToPreviousPositionChanged(long maxSeekToPreviousPositionMs) {
//            LogUtil.d(TAG, "maxSeekToPreviousPositionMs=" + maxSeekToPreviousPositionMs);
//        }
//
//        @Override
//        public void onAudioSessionIdChanged(int audioSessionId) {
//            LogUtil.d(TAG, "audioSessionId=" + audioSessionId);
//        }
//
//        @Override
//        public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onVolumeChanged(float volume) {
//            LogUtil.d(TAG, "volume=" + volume);
//        }
//
//        @Override
//        public void onSkipSilenceEnabledChanged(boolean skipSilenceEnabled) {
//            LogUtil.d(TAG, "skipSilenceEnabled=" + skipSilenceEnabled);
//        }
//
//        @Override
//        public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onDeviceVolumeChanged(int volume, boolean muted) {
//            LogUtil.d(TAG, "volume=" + volume + ", muted=" + muted);
//        }
//
//        @Override
//        public void onVideoSizeChanged(VideoSize videoSize) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onSurfaceSizeChanged(int width, int height) {
//            LogUtil.d(TAG, "width=" + width + ", height=" + height);
//        }
//
//        @Override
//        public void onRenderedFirstFrame() {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onCues(CueGroup cueGroup) {
//            LogUtil.d(TAG);
//        }
//
//        @Override
//        public void onMetadata(Metadata metadata) {
//            LogUtil.d(TAG);
//        }
//    };
}
