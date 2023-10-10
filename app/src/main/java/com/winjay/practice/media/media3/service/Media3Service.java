package com.winjay.practice.media.media3.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.R;
import com.winjay.practice.media.media3.Media3Activity;

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

    /*private class CustomMediaLibrarySessionCallback implements MediaLibrarySession.Callback {

        override fun

        onConnect(session:MediaSession, controller:ControllerInfo):

        ConnectionResult {
            val availableSessionCommands =
                    ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
            for (commandButton in customCommands) {
                // Add custom command to available session commands.
                commandButton.sessionCommand ?.let {
                    availableSessionCommands.add(it)
                }
            }
            return ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build()
        }

        override fun

        onCustomCommand(
                session:MediaSession,
                controller:ControllerInfo,
                customCommand:SessionCommand,
                args:Bundle
        ):ListenableFuture<SessionResult>

        {
            if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
                // Enable shuffling.
                player.shuffleModeEnabled = true
                // Change the custom layout to contain the `Disable shuffling` command.
                session.setCustomLayout(ImmutableList.of(customCommands[1]))
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                // Disable shuffling.
                player.shuffleModeEnabled = false
                // Change the custom layout to contain the `Enable shuffling` command.
                session.setCustomLayout(ImmutableList.of(customCommands[0]))
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun

        onGetLibraryRoot(
                session:MediaLibrarySession,
                browser:ControllerInfo,
                params:LibraryParams?
        ):ListenableFuture<LibraryResult<MediaItem>>

        {
            if (params != null && params.isRecent) {
                // The service currently does not support playback resumption. Tell System UI by returning
                // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
                // https://github.com/androidx/media/issues/355
                return Futures.immediateFuture(LibraryResult.ofError(RESULT_ERROR_NOT_SUPPORTED))
            }
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params))
        }

        override fun

        onGetItem(
                session:MediaLibrarySession,
                browser:ControllerInfo,
                mediaId:String
        ):ListenableFuture<LibraryResult<MediaItem>>

        {
            val item =
                    MediaItemTree.getItem(mediaId)
                            ?:return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        )
            return Futures.immediateFuture(LibraryResult.ofItem(item, *//* params= *//* null))
        }

        override fun

        onSubscribe(
                session:MediaLibrarySession,
                browser:ControllerInfo,
                parentId:String,
                params:LibraryParams?
        ):ListenableFuture<LibraryResult<Void>>

        {
            val children =
                    MediaItemTree.getChildren(parentId)
                            ?:return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        )
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun

        onGetChildren(
                session:MediaLibrarySession,
                browser:ControllerInfo,
                parentId:String,
                page:Int,
                pageSize:Int,
                params:LibraryParams?
        ):ListenableFuture<LibraryResult<ImmutableList<MediaItem>>>

        {
            val children =
                    MediaItemTree.getChildren(parentId)
                            ?:return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        )

            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }

        override fun

        onAddMediaItems(
                mediaSession:MediaSession,
                controller:MediaSession.ControllerInfo,
                mediaItems:List<MediaItem>
        ):ListenableFuture<List<MediaItem>>

        {
            val updatedMediaItems:List<MediaItem> =
            mediaItems.map {
            mediaItem ->
            if (mediaItem.requestMetadata.searchQuery != null)
                getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery !!)
          else MediaItemTree.getItem(mediaItem.mediaId) ?:mediaItem
        }
            return Futures.immediateFuture(updatedMediaItems)
        }

        private fun getMediaItemFromSearchQuery(query:String):

        MediaItem {
            // Only accept query with pattern "play [Title]" or "[Title]"
            // Where [Title]: must be exactly matched
            // If no media with exact name found, play a random media instead
            val mediaTitle =
            if (query.startsWith("play ", ignoreCase = true)) {
                query.drop(5)
            } else {
                query
            }

            return MediaItemTree.getItemFromTitle(mediaTitle) ?:MediaItemTree.getRandomItem()
        }
    }*/

    private void initializeSessionAndPlayer() {
        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build();
//        MediaItemTree.initialize(assets);
        mediaLibrarySession = new MediaLibrarySession.Builder(this, player, new MediaLibrarySession.Callback() {
            @Override
            public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(MediaLibrarySession session, MediaSession.ControllerInfo browser, String parentId, int page, int pageSize, @Nullable LibraryParams params) {
                return MediaLibrarySession.Callback.super.onGetChildren(session, browser, parentId, page, pageSize, params);
            }
        }).build();
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
