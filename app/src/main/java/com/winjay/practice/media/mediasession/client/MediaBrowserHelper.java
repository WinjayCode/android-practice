/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.winjay.practice.media.mediasession.client;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for a MediaBrowser that handles connecting, disconnecting,
 * and basic browsing with simplified callbacks.
 */
public class MediaBrowserHelper {
    private static final String TAG = MediaBrowserHelper.class.getSimpleName();

    private final Context mContext;
    private final String mMediaBrowserServicePkg;
    private final String mMediaBrowserServiceClass;

    private MediaBrowserCompat mMediaBrowser;

    @Nullable
    private MediaControllerCompat mMediaController;

    private final List<MediaControllerCompat.Callback> mMediaControllerCallbackList = new ArrayList<>();

    /**
     * @param context Context of the application
     * @param pkg     The name of the package the MediaBrowserService component exists in
     * @param cls     The name of the class inside of pkg that implements the MediaBrowserService component
     */
    public MediaBrowserHelper(Context context, @NonNull String pkg, @NonNull String cls) {
        mContext = context;
        mMediaBrowserServicePkg = pkg;
        mMediaBrowserServiceClass = cls;
    }

    public void onStart() {
        if (mMediaBrowser == null) {
            // Create MediaBrowserServiceCompat
            mMediaBrowser =
                    new MediaBrowserCompat(
                            mContext,
                            new ComponentName(mMediaBrowserServicePkg, mMediaBrowserServiceClass),
                            mConnectionCallback,
                            null); // optional Bundle
            mMediaBrowser.connect();
        }
        LogUtil.d(TAG, "Creating MediaBrowser, and connecting");
    }

    // Receives callbacks from the MediaBrowser when it has successfully connected to the
    // MediaBrowserService (MusicService).
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        // Happens as a result of onStart().
        @Override
        public void onConnected() {
            // Get a MediaController for the MediaSession.
            mMediaController = new MediaControllerCompat(mContext, mMediaBrowser.getSessionToken());
            mMediaController.registerCallback(mMediaControllerCallback);

            // Sync existing MediaSession state to the UI.
            mMediaControllerCallback.onMetadataChanged(mMediaController.getMetadata());
            mMediaControllerCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());

            MediaBrowserHelper.this.onConnected();

            // 从 MediaBrowserService 的 onLoadChildren() 方法中获取媒体列表
            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
            // 还可以使用 先取消订阅再订阅的方式 进行主动异步获取的操作
            // mediaBrowser.unsubscribe(mediaBrowser.getRoot());
            // mediaBrowser.subscribe(mediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
        }
    };

    // Receives callbacks from the MediaController and updates the UI state,
    // i.e.: Which is the current item, whether it's playing or paused, etc.
    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {
            for (MediaControllerCompat.Callback callback : mMediaControllerCallbackList) {
                callback.onMetadataChanged(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(@Nullable final PlaybackStateCompat state) {
            for (MediaControllerCompat.Callback callback : mMediaControllerCallbackList) {
                callback.onPlaybackStateChanged(state);
            }
        }

        // This might happen if the MusicService is killed while the Activity is in the
        // foreground and onStart() has been called (but not onStop()).
        @Override
        public void onSessionDestroyed() {
            resetState();
            onPlaybackStateChanged(null);

            MediaBrowserHelper.this.onDisconnected();
        }
    };

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    public final MediaBrowserCompat.SubscriptionCallback mMediaBrowserSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            // children 即为从 MediaBrowserService 的 onLoadChildren() 方法中获取的媒体列表
            MediaBrowserHelper.this.onChildrenLoaded(parentId, children);
        }
    };

    @NonNull
    protected final MediaControllerCompat getMediaController() {
        if (mMediaController == null) {
            throw new IllegalStateException("MediaController is null!");
        }
        return mMediaController;
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return getMediaController().getTransportControls();
    }

    public PlaybackStateCompat getPlaybackState() {
        return getMediaController().getPlaybackState();
    }

    public void registerMediaControllerCallback(MediaControllerCompat.Callback callback) {
        if (callback != null) {
            mMediaControllerCallbackList.add(callback);

            // Update with the latest metadata/playback state.
            if (mMediaController != null) {
                final MediaMetadataCompat metadata = mMediaController.getMetadata();
                if (metadata != null) {
                    callback.onMetadataChanged(metadata);
                }

                final PlaybackStateCompat playbackState = mMediaController.getPlaybackState();
                if (playbackState != null) {
                    callback.onPlaybackStateChanged(playbackState);
                }
            }
        }
    }

    public void onStop() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mMediaController = null;
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
        }
        resetState();
        LogUtil.d(TAG, "Releasing MediaController, Disconnecting from MediaBrowser");
    }

    /**
     * The internal state of the app needs to revert to what it looks like when it started before
     * any connections to the {@link MediaBrowserServiceCompat} happens via the {@link MediaSessionCompat}.
     */
    private void resetState() {
        for (MediaControllerCompat.Callback callback : mMediaControllerCallbackList) {
            callback.onPlaybackStateChanged(null);
        }
        LogUtil.d(TAG);
    }

    /**
     * Called after connecting with a {@link MediaBrowserServiceCompat}.
     * <p>
     * Override to perform processing after a connection is established.
     */
    protected void onConnected() {
    }

    /**
     * Called after loading a browsable {@link MediaBrowserCompat.MediaItem}
     *
     * @param parentId The media ID of the parent item.
     * @param children List (possibly empty) of child items.
     */
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
    }

    /**
     * Called when the {@link MediaBrowserServiceCompat} connection is lost.
     */
    protected void onDisconnected() {
    }
}