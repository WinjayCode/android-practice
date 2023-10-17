package com.winjay.practice.media.media3.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSourceBitmapLoader;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CacheBitmapLoader;
import androidx.media3.session.CommandButton;
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
import com.winjay.practice.media.media3.Media3SessionActivity;
import com.winjay.practice.media.media3.data.Media3DataHelper;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Winjay
 * @date 2023-10-13
 */
public class Media3SessionService extends MediaSessionService {
    private static final String TAG = Media3SessionService.class.getSimpleName();
    private MediaSession mediaSession = null;
    private ExoPlayer player;
    private Media3DataHelper media3DataHelper;
    private List<CommandButton> customCommandButtons = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        customCommandButtons.add(getShuffleCommandButton(new SessionCommand(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)));
        customCommandButtons.add(getShuffleCommandButton(new SessionCommand(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)));

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
        public MediaSession.ConnectionResult onConnect(MediaSession session,
                                                       MediaSession.ControllerInfo controller) {
            LogUtil.d(TAG);
            SessionCommands.Builder availableSessionCommands =
                    MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon();

            // 增加自定义Command
            SessionCommand customSessionCommand = new SessionCommand(Media3Constant.CUSTOM_COMMAND, Bundle.EMPTY);
            availableSessionCommands.add(customSessionCommand);

            for (CommandButton customCommandButton : customCommandButtons) {
                if (customCommandButton.sessionCommand != null) {
                    availableSessionCommands.add(customCommandButton.sessionCommand);
                }
            }

            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build();
        }

        @Override
        public ListenableFuture<SessionResult> onCustomCommand(MediaSession session,
                                                               MediaSession.ControllerInfo controller,
                                                               SessionCommand customCommand,
                                                               Bundle args) {
            String customAction = customCommand.customAction;
            LogUtil.d(TAG, "customAction=" + customAction);
            switch (customAction) {
                case Media3Constant.CUSTOM_COMMAND:

                    break;
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
    }

    @OptIn(markerClass = UnstableApi.class)
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
                .setCustomLayout(ImmutableList.of(customCommandButtons.get(0)))
                .setBitmapLoader(new CacheBitmapLoader(new DataSourceBitmapLoader(/* context= */ this)))
                .setCallback(new MediaSessionCallback())
                .build();
    }

    private CommandButton getShuffleCommandButton(SessionCommand sessionCommand) {
        boolean isOn = sessionCommand.customAction.equals(Media3Constant.CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON);
        return new CommandButton.Builder()
                .setDisplayName(getString(isOn ? R.string.exo_controls_shuffle_on_description : R.string.exo_controls_shuffle_off_description))
                .setSessionCommand(sessionCommand)
                .setIconResId(isOn ? R.drawable.exo_icon_shuffle_off : R.drawable.exo_icon_shuffle_on)
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
