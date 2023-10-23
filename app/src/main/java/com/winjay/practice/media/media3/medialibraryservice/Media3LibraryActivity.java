package com.winjay.practice.media.media3.medialibraryservice;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.bumptech.glide.Glide;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMusicPlayBinding;
import com.winjay.practice.media.media3.medialibraryservice.service.Media3LibraryService;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.MediaUtil;

import java.util.concurrent.ExecutionException;

/**
 * Media3服务端使用MediaLibraryService
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3LibraryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = Media3LibraryActivity.class.getSimpleName();
    private ActivityMusicPlayBinding binding;

    private SessionToken sessionToken;
    private ListenableFuture<MediaController> controllerFuture;
    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaBrowser mediaBrowser;
    private MediaController mediaController;
    private PlayerListener playerListener;
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

        // 可获取媒体资源库的 service (通过MediaBrowser获取)
        sessionToken = new SessionToken(this, new ComponentName(this, Media3LibraryService.class));

        initMediaController();
        initMediaBrowser();
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
                Glide.with(Media3LibraryActivity.this)
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
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();

        controllerFuture.addListener(() -> {
            // Call controllerFuture.get() to retrieve the MediaController.
            // MediaController implements the Player interface, so it can be
            // attached to the PlayerView UI component.
//            playerView.setPlayer(mControllerFuture.get());
            try {
                LogUtil.d(TAG, "get player");
                mediaController = controllerFuture.get();

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
            Glide.with(Media3LibraryActivity.this)
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

    private void initMediaBrowser() {
        browserFuture = new MediaBrowser.Builder(this, sessionToken).buildAsync();
        browserFuture.addListener(() -> {
            try {
                mediaBrowser = browserFuture.get();
                if (mediaBrowser != null) {
                    pushRoot();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void pushRoot() {
        ListenableFuture<LibraryResult<MediaItem>> rootFuture = mediaBrowser.getLibraryRoot(null);
        rootFuture.addListener(() -> {
            try {
                LibraryResult<MediaItem> mediaItemLibraryResult = rootFuture.get();
                if (mediaItemLibraryResult != null && mediaItemLibraryResult.value != null) {
                    displayChildrenList(mediaItemLibraryResult.value);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void displayChildrenList(MediaItem mediaItem) {
        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture = mediaBrowser.getChildren(mediaItem.mediaId, 0, Integer.MAX_VALUE, null);
        childrenFuture.addListener(() -> {
            try {
                LibraryResult<ImmutableList<MediaItem>> immutableListLibraryResult = childrenFuture.get();
                if (immutableListLibraryResult != null && immutableListLibraryResult.value != null) {
                    LogUtil.d(TAG, "childrenList.size=" + immutableListLibraryResult.value.size());
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onClick(View v) {
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

    @Override
    protected void onDestroy() {
        updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
        mediaController.removeListener(playerListener);
        MediaController.releaseFuture(controllerFuture);
        binding.media3SeekBar.disconnectPlayer();

        super.onDestroy();
    }
}
