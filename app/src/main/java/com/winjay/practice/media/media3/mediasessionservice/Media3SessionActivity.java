package com.winjay.practice.media.media3.mediasessionservice;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionResult;
import androidx.media3.session.SessionToken;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMusicPlayBinding;
import com.winjay.practice.media.media3.Media3Constant;
import com.winjay.practice.media.media3.mediasessionservice.service.Media3SessionService;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.MediaUtil;

import java.util.concurrent.ExecutionException;

/**
 * Media3服务端使用MediaSessionService
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3SessionActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = Media3SessionActivity.class.getSimpleName();
    private ActivityMusicPlayBinding binding;

    private ListenableFuture<MediaController> controllerFuture;
    private MediaController mediaController;
    private PlayerListener playerListener;
    private SessionCommand customSessionCommand;
    private SessionCommand toggleShuffleModeOnCustomCommand;
    private SessionCommand toggleShuffleModeOffCustomCommand;
    private final Handler updatePositionHandler = new Handler();

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityMusicPlayBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.mediaSeekBar.setVisibility(View.INVISIBLE);

        binding.prevIv.setOnClickListener(this);
        binding.nextIv.setOnClickListener(this);
        binding.playPauseIv.setOnClickListener(this);

        initMediaController();
    }

    private final Runnable mUpdatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                binding.positionTv.setText(MediaUtil.formatDuration(mediaController.getCurrentPosition()));
                updatePositionHandler.postDelayed(mUpdatePositionRunnable, 1000);
            } catch (Exception e) {
                updatePositionHandler.removeCallbacks(this);
            }
        }
    };

    private class PlayerListener implements Player.Listener {
        @Override
        public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
            binding.musicTitleTv.setText(mediaMetadata.title);
            binding.musicArtistTv.setText(mediaMetadata.artist);

            if (mediaMetadata.artworkUri == Uri.EMPTY) {
                binding.albumIv.setImageResource(R.mipmap.icon);
            } else {
                Glide.with(Media3SessionActivity.this)
                        .load(mediaMetadata.artworkUri)
                        .into(binding.albumIv);
            }

            if (mediaController.getDuration() != C.TIME_UNSET) {
                binding.durationTv.setText(MediaUtil.formatDuration(mediaController.getDuration()));
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying) {
                binding.playPauseIv.setImageResource(android.R.drawable.ic_media_pause);

                updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
                updatePositionHandler.post(mUpdatePositionRunnable);
            } else {
                binding.playPauseIv.setImageResource(android.R.drawable.ic_media_play);

                updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
            }
        }
    }

    private void initMediaController() {
        // 普通可后台播放的 service
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, Media3SessionService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();

        controllerFuture.addListener(() -> {
            try {
                LogUtil.d(TAG, "get player");
                mediaController = controllerFuture.get();

                getCustomCommand();

                playerListener = new PlayerListener();
                mediaController.addListener(playerListener);

                if (mediaController.getPlaybackState() != Player.STATE_IDLE) {
                    initMediaDisplay();
                }
                binding.media3SeekBar.setPlayer(mediaController);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    private void initMediaDisplay() {
        if (mediaController.getCurrentMediaItem() == null) {
            return;
        }
        MediaItem mediaItem = mediaController.getCurrentMediaItem();
        binding.musicTitleTv.setText(mediaItem.mediaMetadata.title);
        binding.musicArtistTv.setText(mediaItem.mediaMetadata.artist);

        if (mediaItem.mediaMetadata.artworkUri == Uri.EMPTY) {
            binding.albumIv.setImageResource(R.mipmap.icon);
        } else {
            Glide.with(Media3SessionActivity.this)
                    .load(mediaItem.mediaMetadata.artworkUri)
                    .into(binding.albumIv);
        }

        if (mediaController.isPlaying()) {
            binding.playPauseIv.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            binding.playPauseIv.setImageResource(android.R.drawable.ic_media_play);
        }

        updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
        updatePositionHandler.post(mUpdatePositionRunnable);

        if (mediaController.getDuration() != C.TIME_UNSET) {
            binding.durationTv.setText(MediaUtil.formatDuration(mediaController.getDuration()));
        }
    }

    @Override
    public void onClick(View v) {
//        testCustomCommand();

        if (v == binding.prevIv) {
            mediaController.seekToPreviousMediaItem();
        }
        if (v == binding.nextIv) {
            mediaController.seekToNextMediaItem();
        }
        if (v == binding.playPauseIv) {
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        }
    }

    private void getCustomCommand() {
        SessionCommands availableSessionCommands = mediaController.getAvailableSessionCommands();
        for (SessionCommand sessionCommand : availableSessionCommands.commands) {
            LogUtil.d(TAG, "commandCode=" + sessionCommand.commandCode + ",customAction=" + sessionCommand.customAction);
            if (sessionCommand.commandCode == SessionCommand.COMMAND_CODE_CUSTOM) {
                switch (sessionCommand.customAction) {
                    case Media3Constant.CUSTOM_COMMAND:
                        customSessionCommand = sessionCommand;
                        break;
                    case Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON:
                        toggleShuffleModeOnCustomCommand = sessionCommand;
                        break;
                    case Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF:
                        toggleShuffleModeOffCustomCommand = sessionCommand;
                        break;
                }
            }
        }
    }

    /**
     * test custom command
     */
    private void testCustomCommand() {
        ListenableFuture<SessionResult> sessionResultListenableFuture = mediaController.sendCustomCommand(customSessionCommand, Bundle.EMPTY);
        sessionResultListenableFuture.addListener(() -> {
            try {
                SessionResult sessionResult = sessionResultListenableFuture.get();
                if (SessionResult.RESULT_SUCCESS == sessionResult.resultCode) {
                    LogUtil.d(TAG, "custom command send succeeded!");
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    @Override
    protected void onDestroy() {
        updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
        mediaController.removeListener(playerListener);
        MediaController.releaseFuture(controllerFuture);
        binding.media3SeekBar.disconnectPlayer();

        super.onDestroy();
    }
}
