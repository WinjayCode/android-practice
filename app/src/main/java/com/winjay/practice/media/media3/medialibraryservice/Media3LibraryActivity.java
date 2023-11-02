package com.winjay.practice.media.media3.medialibraryservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.SessionToken;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.MainActivityBinding;
import com.winjay.practice.media.media3.medialibraryservice.service.Media3LibraryService;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Media3服务端使用MediaLibraryService
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3LibraryActivity extends BaseActivity {
    private static final String TAG = Media3LibraryActivity.class.getSimpleName();
    private MainActivityBinding binding;
    private List<MediaItem> subItemMediaList = new ArrayList<>();
    private FolderMediaItemAdapter mediaListAdapter;

    private ListenableFuture<MediaBrowser> mediaBrowserFuture;
    private MediaBrowser mediaBrowser;

    private ArrayDeque<MediaItem> treePathStack = new ArrayDeque<>();

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = MainActivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.mainRv.setLayoutManager(new LinearLayoutManager(this));
        mediaListAdapter = new FolderMediaItemAdapter(subItemMediaList);
        binding.mainRv.setAdapter(mediaListAdapter);
        mediaListAdapter.setOnItemClickListener((view, position) -> {
            MediaItem selectedMediaItem = mediaListAdapter.getItem(position);
            if (selectedMediaItem != null && selectedMediaItem.mediaMetadata.isPlayable) {
                mediaBrowser.setMediaItem(selectedMediaItem);
                startActivity(Media3LibraryPlayerActivity.class);
            } else {
                pushPathStack(selectedMediaItem);
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                popPathStack();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeBrowser();
    }

    @Override
    protected void onStop() {
        releaseBrowser();
        super.onStop();
    }

    private void initializeBrowser() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, Media3LibraryService.class));
        mediaBrowserFuture = new MediaBrowser.Builder(this, sessionToken).buildAsync();
        mediaBrowserFuture.addListener(() -> {
            try {
                if (mediaBrowserFuture.isDone() && !mediaBrowserFuture.isCancelled()) {
                    mediaBrowser = mediaBrowserFuture.get();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            pushRoot();
        }, ContextCompat.getMainExecutor(this));
    }

    private void releaseBrowser() {
        MediaBrowser.releaseFuture(mediaBrowserFuture);
    }

    private void displayChildrenList(MediaItem mediaItem) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(treePathStack.size() != 1);
        }
        LogUtil.d(TAG, "mediaId=" + mediaItem.mediaId);
        ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> childrenFuture = mediaBrowser.getChildren(
                mediaItem.mediaId, 0, Integer.MAX_VALUE, null);
        subItemMediaList.clear();
        childrenFuture.addListener(() -> {
            try {
                LibraryResult<ImmutableList<MediaItem>> immutableListLibraryResult = childrenFuture.get();
                if (immutableListLibraryResult != null && immutableListLibraryResult.value != null) {
                    LogUtil.d(TAG, "childrenList.size=" + immutableListLibraryResult.value.size());
                    subItemMediaList.addAll(immutableListLibraryResult.value);
                    mediaListAdapter.notifyDataSetChanged();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void pushPathStack(MediaItem mediaItem) {
        treePathStack.addLast(mediaItem);
        displayChildrenList(treePathStack.getLast());
    }

    private void popPathStack() {
        treePathStack.removeLast();
        if (treePathStack.size() == 0) {
            finish();
            return;
        }

        displayChildrenList(treePathStack.getLast());
    }

    private void pushRoot() {
        // browser can be initialized many times
        // only push root at the first initialization
        if (!treePathStack.isEmpty()) {
            return;
        }
        if (mediaBrowser == null) {
            return;
        }
        ListenableFuture<LibraryResult<MediaItem>> rootFuture = mediaBrowser.getLibraryRoot(null);
        rootFuture.addListener(() -> {
            try {
                LibraryResult<MediaItem> mediaItemLibraryResult = rootFuture.get();
                if (mediaItemLibraryResult != null && mediaItemLibraryResult.value != null) {
                    pushPathStack(mediaItemLibraryResult.value);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
