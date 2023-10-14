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

import com.winjay.practice.media.media3.Media3SessionActivity;
import com.winjay.practice.media.media3.data.Media3DataHelper;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 *
 * @author Winjay
 * @date 2023-10-13
 */
public class Media3SessionService extends MediaSessionService {
    private static final String TAG = Media3SessionService.class.getSimpleName();
    private MediaSession mediaSession = null;
    private ExoPlayer player;
    private Media3DataHelper media3DataHelper;

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
    }

    private void initializeSessionAndPlayer() {
        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build();
        media3DataHelper = new Media3DataHelper();
        media3DataHelper.getMediaItems(new Media3DataHelper.GetDataCallback() {
            @Override
            public void onLoadFinished(List<MediaItem> mediaItems) {
                HandlerManager.getInstance().postOnMainThread(() -> {
                    LogUtil.d(TAG, "MediaItem.size=" + mediaItems.size());
                    player.setMediaItems(mediaItems);
                });
            }
        });
        mediaSession = new MediaSession.Builder(this, player)
                .setSessionActivity(getSingleTopActivity())
                .setCallback(new MediaSessionCallback())
                .build();
    }

    private PendingIntent getSingleTopActivity() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, Media3SessionActivity.class),
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
