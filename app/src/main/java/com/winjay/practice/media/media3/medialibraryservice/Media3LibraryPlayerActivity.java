package com.winjay.practice.media.media3.medialibraryservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMedia3PlayerBinding;
import com.winjay.practice.media.media3.medialibraryservice.service.Media3LibraryService;
import com.winjay.practice.utils.LogUtil;

import java.util.concurrent.ExecutionException;

/**
 * Player
 *
 * @author Winjay
 * @date 2023-10-23
 */
public class Media3LibraryPlayerActivity extends BaseActivity {
    private static final String TAG = Media3LibraryPlayerActivity.class.getSimpleName();
    private ActivityMedia3PlayerBinding binding;
    private ListenableFuture<MediaController> mediaControllerFuture;
    private MediaController mediaController;
    private PlayerListener playerListener;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityMedia3PlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeController();
    }

    @Override
    protected void onDestroy() {
        mediaController.pause();
        binding.playerView.setPlayer(null);
        releaseController();

        super.onDestroy();
    }

    private void initializeController() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, Media3LibraryService.class));
        mediaControllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        mediaControllerFuture.addListener(() -> {
            try {
                mediaController = mediaControllerFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            setController();
        }, MoreExecutors.directExecutor());
    }

    private void releaseController() {
        mediaController.removeListener(playerListener);
        MediaController.releaseFuture(mediaControllerFuture);
    }

    private class PlayerListener implements Player.Listener {
        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            LogUtil.d(TAG);
            updateMediaMetadataUI(mediaItem != null ? mediaItem.mediaMetadata : MediaMetadata.EMPTY);
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onTracksChanged(Tracks tracks) {
            LogUtil.d(TAG);
            binding.playerView.setShowSubtitleButton(tracks.isTypeSupported(C.TRACK_TYPE_TEXT));
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setController() {
        if (mediaController == null) {
            return;
        }

        binding.playerView.setPlayer(mediaController);

        updateMediaMetadataUI(mediaController.getMediaMetadata());
        binding.playerView.setShowSubtitleButton(mediaController.getCurrentTracks().isTypeSupported(C.TRACK_TYPE_TEXT));

        playerListener = new PlayerListener();
        mediaController.addListener(playerListener);

        mediaController.prepare();
        mediaController.play();
    }

    private void updateMediaMetadataUI(MediaMetadata mediaMetadata) {
        binding.mediaTitle.setText(mediaMetadata.title);
        binding.mediaArtist.setText(mediaMetadata.artist);
    }
}
