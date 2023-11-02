package com.winjay.practice.media.media3.medialibraryservice.service;

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
import androidx.media3.session.SessionResult;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.winjay.practice.R;
import com.winjay.practice.media.media3.Media3Constant;
import com.winjay.practice.media.media3.medialibraryservice.Media3LibraryActivity;
import com.winjay.practice.media.media3.medialibraryservice.Media3LibraryPlayerActivity;
import com.winjay.practice.media.media3.medialibraryservice.data.MediaItemTree;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * androidx.media3 服务端
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3LibraryService extends MediaLibraryService {
    private static final String TAG = Media3LibraryService.class.getSimpleName();
    private ExoPlayer player;
    private MediaLibrarySession mediaLibrarySession = null;
    private List<CommandButton> customCommandButtons = new ArrayList<>();

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);
        customCommandButtons.add(getShuffleCommandButton(new SessionCommand(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)));
        customCommandButtons.add(getShuffleCommandButton(new SessionCommand(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)));

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
            for (CommandButton customCommand : customCommandButtons) {
                if (customCommand.sessionCommand != null) {
                    availableSessionCommands.add(customCommand.sessionCommand);
                }
            }

            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build();
        }

        @Override
        public ListenableFuture<SessionResult> onCustomCommand(MediaSession session,
                                                               MediaSession.ControllerInfo controller,
                                                               SessionCommand customCommand, Bundle args) {
            String customAction = customCommand.customAction;
            LogUtil.d(TAG, "customAction=" + customAction);
            switch (customAction) {
                case Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON:
                    // Enable shuffling.
                    player.setShuffleModeEnabled(true);
                    // Change the custom layout to contain the `Disable shuffling` command.
                    session.setCustomLayout(ImmutableList.of(customCommandButtons.get(1)));
                    break;
                case Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF:
                    // Disable shuffling.
                    player.setShuffleModeEnabled(false);
                    // Change the custom layout to contain the `Enable shuffling` command.
                    session.setCustomLayout(ImmutableList.of(customCommandButtons.get(0)));
                    break;
            }
            return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
        }

        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(MediaLibrarySession session,
                                                                           MediaSession.ControllerInfo browser,
                                                                           @Nullable LibraryParams params) {
            LogUtil.d(TAG);
            if (params != null && params.isRecent) {
                // The service currently does not support playback resumption. Tell System UI by returning
                // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
                // https://github.com/androidx/media/issues/355
                return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED));
            }
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params));
        }

        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetItem(MediaLibrarySession session,
                                                                    MediaSession.ControllerInfo browser,
                                                                    String mediaId) {
            LogUtil.d(TAG);
            MediaItem item = MediaItemTree.getItem(mediaId);
            if (item == null) {
                return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            return Futures.immediateFuture(LibraryResult.ofItem(item, null));
        }

        @Override
        public ListenableFuture<LibraryResult<Void>> onSubscribe(MediaLibrarySession session,
                                                                 MediaSession.ControllerInfo browser,
                                                                 String parentId, @Nullable LibraryParams params) {
            LogUtil.d(TAG);
            List<MediaItem> children = MediaItemTree.getChildren(parentId);
            if (children == null) {
                return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            session.notifyChildrenChanged(browser, parentId, children.size(), params);
            return Futures.immediateFuture(LibraryResult.ofVoid());
        }

        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(MediaLibrarySession session,
                                                                                       MediaSession.ControllerInfo browser,
                                                                                       String parentId,
                                                                                       int page,
                                                                                       int pageSize,
                                                                                       @Nullable LibraryParams params) {
            LogUtil.d(TAG);
            List<MediaItem> children = MediaItemTree.getChildren(parentId);
            if (children == null) {
                return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            return Futures.immediateFuture(LibraryResult.ofItemList(children, params));
        }

        @Override
        public ListenableFuture<List<MediaItem>> onAddMediaItems(MediaSession mediaSession,
                                                                 MediaSession.ControllerInfo controller,
                                                                 List<MediaItem> mediaItems) {
            LogUtil.d(TAG);
            List<MediaItem> updatedMediaItems = new ArrayList<>();
            for (MediaItem mediaItem : mediaItems) {
                if (mediaItem.requestMetadata.searchQuery != null) {
                    MediaItem updatedItem = getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery);
                    updatedMediaItems.add(updatedItem);
                } else {
                    MediaItem item = MediaItemTree.getItem(mediaItem.mediaId);
                    if (item != null) {
                        updatedMediaItems.add(item);
                    } else {
                        updatedMediaItems.add(mediaItem);
                    }
                }
            }
            return Futures.immediateFuture(updatedMediaItems);
        }

        private MediaItem getMediaItemFromSearchQuery(String query) {
            LogUtil.d(TAG, "query=" + query);
            // Only accept query with pattern "play [Title]" or "[Title]"
            // Where [Title]: must be exactly matched
            // If no media with exact name found, play a random media instead
            String mediaTitle;
            if (query.toLowerCase().startsWith("play ")) {
                mediaTitle = query.substring(5);
            } else {
                mediaTitle = query;
            }

            MediaItem item = MediaItemTree.getItemFromTitle(mediaTitle);
            if (item == null) {
                item = MediaItemTree.getRandomItem();
            }
            return item;
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
        MediaItemTree.initialize(getAssets());
        mediaLibrarySession = new MediaLibrarySession.Builder(this, player, new CustomMediaLibrarySessionCallback())
                .setId(TAG) // used when app supports multiple playback
                .setSessionActivity(getSingleTopActivity())
                .setCustomLayout(ImmutableList.of(customCommandButtons.get(0)))
                .setBitmapLoader(new CacheBitmapLoader(new DataSourceBitmapLoader(/* context= */ this)))
                .build();
    }

    private PendingIntent getSingleTopActivity() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, Media3LibraryPlayerActivity.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private PendingIntent getBackStackedActivity() {
        return TaskStackBuilder.create(this)
                .addNextIntent(new Intent(Media3LibraryService.this, Media3LibraryActivity.class))
                .addNextIntent(new Intent(Media3LibraryService.this, Media3LibraryPlayerActivity.class))
                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private CommandButton getShuffleCommandButton(SessionCommand sessionCommand) {
        boolean isOn = sessionCommand.customAction.equals(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON);
        return new CommandButton.Builder()
                .setDisplayName(getString(isOn ? R.string.exo_controls_shuffle_on_description : R.string.exo_controls_shuffle_off_description))
                .setSessionCommand(sessionCommand)
                .setIconResId(isOn ? R.drawable.exo_icon_shuffle_off : R.drawable.exo_icon_shuffle_on)
                .build();
    }

    @UnstableApi
    private class MediaSessionServiceListener implements MediaSessionService.Listener {
        @Override
        public void onForegroundServiceStartNotAllowedException() {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Media3LibraryService.this);
            ensureNotificationChannel(notificationManagerCompat);
            PendingIntent pendingIntent = TaskStackBuilder.create(Media3LibraryService.this)
                    .addNextIntent(new Intent(Media3LibraryService.this, Media3LibraryActivity.class))
                    .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Media3LibraryService.this, Media3Constant.CHANNEL_ID)
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
            notificationManagerCompat.notify(Media3Constant.NOTIFICATION_ID, builder.build());
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void ensureNotificationChannel(NotificationManagerCompat notificationManagerCompat) {
        if (Util.SDK_INT < 26 || notificationManagerCompat.getNotificationChannel(Media3Constant.CHANNEL_ID) != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(Media3Constant.CHANNEL_ID, "Playback cannot be resumed", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManagerCompat.createNotificationChannel(channel);
    }
}
