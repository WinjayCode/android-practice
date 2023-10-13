package com.winjay.practice.media.media3;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMusicPlayBinding;
import com.winjay.practice.media.media3.service.Media3Service;
import com.winjay.practice.media.media3.service.Media3SessionService;
import com.winjay.practice.utils.LogUtil;

import java.util.concurrent.ExecutionException;

/**
 * androidx.media3 学习
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3Activity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = Media3Activity.class.getSimpleName();
    private ActivityMusicPlayBinding binding;

    private SessionToken sessionToken;
    private ListenableFuture<MediaController> controllerFuture;
    private ListenableFuture<MediaBrowser> browserFuture;
    private MediaBrowser mediaBrowser;
    private Player player;


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

        binding.prevIv.setOnClickListener(this);
        binding.nextIv.setOnClickListener(this);
        binding.playPauseIv.setOnClickListener(this);

//        sessionToken = new SessionToken(this, new ComponentName(this, Media3Service.class));
        sessionToken = new SessionToken(this, new ComponentName(this, Media3SessionService.class));
        initMediaController();
//        initMediaBrowser();
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
                player = controllerFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
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
                if (immutableListLibraryResult != null && immutableListLibraryResult.value  != null) {
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
            player.seekToPreviousMediaItem();
        }
        if (v == binding.nextIv) {
            player.seekToNextMediaItem();
        }
        if (v == binding.playPauseIv) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        }
    }

    @Override
    protected void onDestroy() {
        MediaController.releaseFuture(controllerFuture);

        super.onDestroy();
    }
}
