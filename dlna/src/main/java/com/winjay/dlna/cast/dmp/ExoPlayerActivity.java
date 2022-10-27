package com.winjay.dlna.cast.dmp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.winjay.dlna.R;
import com.winjay.dlna.cast.util.Action;
import com.winjay.dlna.databinding.ExoplayerActivityBinding;
import com.winjay.dlna.util.LogUtil;


public class ExoPlayerActivity extends AppCompatActivity {
    private static final String TAG = "ExoPlayerActivity";
    private ExoplayerActivityBinding binding;

    private static final int UPDATE_PROGRESS = 1;

    protected @Nullable
    ExoPlayer player;

    String playURI;

    private Tracks lastSeenTracks;

    private AudioManager mAudioManager;

    public static MediaListener mMediaListener;

    public static void setMediaListener(MediaListener mediaListener) {
        mMediaListener = mediaListener;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        playURI = intent.getStringExtra("playURI");
        if (!TextUtils.isEmpty(playURI)) {
            initializePlayer(playURI);
        }
        setTitle(intent.getStringExtra("name"));
        setIntent(intent);
    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding = ExoplayerActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        binding.playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        binding.playerView.requestFocus();

        Intent intent = getIntent();
        playURI = intent.getStringExtra("playURI");
        LogUtil.d(TAG, "playURI=" + playURI);
        if (!TextUtils.isEmpty(playURI)) {
            initializePlayer(playURI);
        }
        setTitle(intent.getStringExtra("name"));

        registerBroadcast();
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

    protected void initializePlayer(String url) {
        if (player == null) {
            lastSeenTracks = Tracks.EMPTY;
            ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(this);

            player = playerBuilder.build();
            player.addListener(new PlayerEventListener());
            player.addAnalyticsListener(new EventLogger());
            player.setAudioAttributes(AudioAttributes.DEFAULT, true);
            binding.playerView.setPlayer(player);
        }
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
        player.prepare();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    if (player != null) {
                        LogUtil.d(TAG, "currentPosition=" + player.getCurrentPosition());
                        if (mMediaListener != null) {
                            mMediaListener.positionChanged((int) player.getCurrentPosition());
                        }
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
                        player.play();

                        if (null != mMediaListener) {
                            mMediaListener.start();
                        }

                        LogUtil.d(TAG, "duration=" + player.getDuration());
                        if (null != mMediaListener) {
                            mMediaListener.durationChanged((int) player.getDuration());
                        }

                        handler.sendEmptyMessage(UPDATE_PROGRESS);
                    }
                    break;
                // 结束
                case Player.STATE_ENDED:
                    handler.removeMessages(UPDATE_PROGRESS);
                    if (null != mMediaListener) {
                        mMediaListener.endOfMedia();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                player.seekToDefaultPosition();
                player.prepare();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(Tracks tracks) {
            if (tracks == lastSeenTracks) {
                return;
            }
            if (tracks.containsType(C.TRACK_TYPE_VIDEO)
                    && !tracks.isTypeSupported(C.TRACK_TYPE_VIDEO, true)) {
                showToast(getResources().getString(R.string.error_unsupported_video));
            }
            if (tracks.containsType(C.TRACK_TYPE_AUDIO)
                    && !tracks.isTypeSupported(C.TRACK_TYPE_AUDIO, true)) {
                showToast(getResources().getString(R.string.error_unsupported_audio));
            }
            lastSeenTracks = tracks;
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
                if (null != mMediaListener) {
                    mMediaListener.start();
                }
                handler.sendEmptyMessage(UPDATE_PROGRESS);
            } else {
                if (mMediaListener != null) {
                    mMediaListener.pause();
                }
                handler.removeMessages(UPDATE_PROGRESS);
            }
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
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            LogUtil.d(TAG, "reason=" + reason);
            switch (reason) {
                case Player.DISCONTINUITY_REASON_SEEK:
                    LogUtil.d(TAG, "DISCONTINUITY_REASON_SEEK");
                    LogUtil.d(TAG, "oldPosition.contentPositionMs=" + oldPosition.contentPositionMs);
                    LogUtil.d(TAG, "oldPosition.positionMs=" + oldPosition.positionMs);

                    LogUtil.d(TAG, "newPosition.contentPositionMs=" + newPosition.contentPositionMs);
                    LogUtil.d(TAG, "newPosition.positionMs=" + newPosition.positionMs);

                    if (mMediaListener != null) {
                        mMediaListener.positionChanged((int) newPosition.positionMs);
                    }
                    break;
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.playerView.onPause();
        releasePlayer();
    }

    protected void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            binding.playerView.setPlayer(null);
        }

        binding.playerView.getAdViewGroup().removeAllViews();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaListener = null;
        unregisterBroadcast();
    }

    private PlayBroadcastReceiver playRecevieBroadcast = new PlayBroadcastReceiver();

    public void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Action.DMR);
        intentFilter.addAction(Action.VIDEO_PLAY);
        registerReceiver(this.playRecevieBroadcast, intentFilter);
    }

    public void unregisterBroadcast() {
        unregisterReceiver(this.playRecevieBroadcast);
    }

    class PlayBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String helpAction = intent.getStringExtra("helpAction");
            LogUtil.d(TAG, "helpAction=" + helpAction);
            switch (helpAction) {
                case Action.PLAY:
                    if (player != null) {
                        player.play();
                    }
                    if (null != mMediaListener) {
                        mMediaListener.start();
                    }
                    break;
                case Action.PAUSE:
                    if (player != null) {
                        player.pause();
                    }
                    if (null != mMediaListener) {
                        mMediaListener.pause();
                    }
                    break;
                case Action.SEEK:
                    int position = intent.getIntExtra("position", 0);
                    if (player != null) {
                        player.seekTo(position);
                    }
                    break;
                case Action.SET_VOLUME:
                    int volume = (int) (intent.getDoubleExtra("volume", 0) * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                    break;
                case Action.STOP:
                    if (player != null) {
                        player.stop();
                    }
                    if (null != mMediaListener) {
                        mMediaListener.stop();
                    }
                    break;
            }

        }
    }

    public interface MediaListener {
        void pause();

        void start();

        void stop();

        void endOfMedia();

        void positionChanged(int position);

        void durationChanged(int duration);
    }
}
