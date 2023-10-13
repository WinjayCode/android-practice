package com.winjay.practice.media.media3.service;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommands;

import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.media.media3.Media3Activity;
import com.winjay.practice.utils.LogUtil;

/**
 *
 * @author Winjay
 * @date 2023-10-13
 */
public class Media3SessionService extends MediaSessionService {
    private static final String TAG = Media3SessionService.class.getSimpleName();
    private MediaSession mediaSession = null;
    private ExoPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeSessionAndPlayer();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        // Return a MediaSession to accept the connection request, or return null to reject the request.
        return mediaSession;
    }

    private class MediaSessionCallback implements MediaSession.Callback {

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public MediaSession.ConnectionResult onConnect(MediaSession session, MediaSession.ControllerInfo controller) {
            LogUtil.d(TAG);
            SessionCommands.Builder availableSessionCommands =
                    MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon();
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build();
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(MediaSession mediaSession, MediaSession.ControllerInfo controller) {
            return MediaSession.Callback.super.onPlaybackResumption(mediaSession, controller);
        }
    }

    private void initializeSessionAndPlayer() {
        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build();
//        new MediaItem.Builder().setMediaMetadata()
//        player.setMediaItem();
        mediaSession = new MediaSession.Builder(this, player)
                .setSessionActivity(getSingleTopActivity())
                .setCallback(new MediaSessionCallback())
                .build();
    }

    private PendingIntent getSingleTopActivity() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, Media3Activity.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}
