package com.winjay.practice.media.media3.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.ForwardingPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSourceBitmapLoader;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CacheBitmapLoader;
import androidx.media3.session.CommandButton;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.R;
import com.winjay.practice.media.media3.Media3Activity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * androidx.media3 服务端
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3Service extends MediaLibraryService {
    private static final String TAG = Media3Service.class.getSimpleName();
    private ExoPlayer player;
    private MediaLibrarySession mediaLibrarySession = null;
    private List<CommandButton> customCommands = new ArrayList<>();

    private static final String CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON = "android.media3.session.demo.SHUFFLE_ON";
    private static final String CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF = "android.media3.session.demo.SHUFFLE_OFF";
    private static final String CHANNEL_ID = "demo_session_notification_channel_id";
    private final int immutableFlag = PendingIntent.FLAG_IMMUTABLE;
    private static final int NOTIFICATION_ID = 123;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        customCommands.add(getShuffleCommandButton(new SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)));
        customCommands.add(getShuffleCommandButton(new SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)));

        initializeSessionAndPlayer();
        setListener(new MediaSessionServiceListener());
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        // Return a MediaLibrarySession to accept the connection request, or return null to reject the request.
        return mediaLibrarySession;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!player.getPlayWhenReady() || player.getMediaItemCount() == 0) {
            stopSelf();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onDestroy() {
        mediaLibrarySession.setSessionActivity(getBackStackedActivity());
        mediaLibrarySession.release();
        player.release();
        clearListener();
        super.onDestroy();
    }

    private class CustomMediaLibrarySessionCallback implements MediaLibrarySession.Callback {

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public MediaSession.ConnectionResult onConnect(MediaSession session, MediaSession.ControllerInfo controller) {
            LogUtil.d(TAG);
            SessionCommands.Builder availableSessionCommands =
                    MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon();
            for (CommandButton customCommand : customCommands) {
                if (customCommand.sessionCommand != null) {
                    availableSessionCommands.add(customCommand.sessionCommand);
                }
            }

            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build();
        }

        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(MediaLibrarySession session,
                                                                           MediaSession.ControllerInfo browser,
                                                                           @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onGetLibraryRoot(session, browser, params);
//            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params));
        }

        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(MediaLibrarySession session,
                                                                                       MediaSession.ControllerInfo browser,
                                                                                       String parentId,
                                                                                       int page,
                                                                                       int pageSize,
                                                                                       @Nullable LibraryParams params) {
            return MediaLibrarySession.Callback.super.onGetChildren(session, browser, parentId, page, pageSize, params);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializeSessionAndPlayer() {
        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build();
        ForwardingPlayer forwardingPlayer = new ForwardingPlayer(player) {
            @Override
            public void play() {
                // Add custom logic
                super.play();
            }

            @Override
            public void setPlayWhenReady(boolean playWhenReady) {
                // Add custom logic
                super.setPlayWhenReady(playWhenReady);
            }
        };
//        MediaItemTree.initialize(assets);
        mediaLibrarySession = new MediaLibrarySession.Builder(this, player, new CustomMediaLibrarySessionCallback())
                .setSessionActivity(getSingleTopActivity())
                .setCustomLayout(ImmutableList.of(customCommands.get(0)))
                .setBitmapLoader(new CacheBitmapLoader(new DataSourceBitmapLoader(/* context= */ this)))
                .build();
    }

    private PendingIntent getSingleTopActivity() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, Media3Activity.class),
                immutableFlag | PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private CommandButton getShuffleCommandButton(SessionCommand sessionCommand) {
        boolean isOn = sessionCommand.customAction.equals(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON);
        return new CommandButton.Builder()
                .setDisplayName(getString(isOn ? R.string.exo_controls_shuffle_on_description : R.string.exo_controls_shuffle_off_description))
                .setSessionCommand(sessionCommand)
                .setIconResId(isOn ? R.drawable.exo_icon_shuffle_off : R.drawable.exo_icon_shuffle_on)
                .build();
    }

    private PendingIntent getBackStackedActivity() {
        return TaskStackBuilder.create(this)
                .addNextIntent(new Intent(Media3Service.this, Media3Activity.class))
//                .addNextIntent(new Intent(Media3Service.this, PlayerActivity.class))
                .getPendingIntent(0, immutableFlag | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @UnstableApi
    private class MediaSessionServiceListener implements MediaSessionService.Listener {
        @Override
        public void onForegroundServiceStartNotAllowedException() {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Media3Service.this);
            ensureNotificationChannel(notificationManagerCompat);
            PendingIntent pendingIntent = TaskStackBuilder.create(Media3Service.this)
                    .addNextIntent(new Intent(Media3Service.this, Media3Activity.class))
                    .getPendingIntent(0, immutableFlag | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Media3Service.this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.media3_notification_small_icon)
                    .setContentTitle("Playback cannot be resumed")
                    .setStyle(
                            new NotificationCompat.BigTextStyle().bigText("Press on the play button on the media notification if it" +
                                    " is still present, otherwise please open the app to start the playback and re-connect the session" +
                                    " to the controller")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void ensureNotificationChannel(NotificationManagerCompat notificationManagerCompat) {
        if (Util.SDK_INT < 26 || notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Playback cannot be resumed", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManagerCompat.createNotificationChannel(channel);
    }
}
