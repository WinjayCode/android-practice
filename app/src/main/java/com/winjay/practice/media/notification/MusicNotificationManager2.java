package com.winjay.practice.media.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.media.mediasession.MediaSessionActivity;

/**
 * @author F2848777
 * @date 2023-02-28
 */
public class MusicNotificationManager2 {
    private static volatile MusicNotificationManager2 instance;
    private final Context mContext;
    private static final int REQUEST_CODE = 501;

    public static MusicNotificationManager2 getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicNotificationManager2.class) {
                if (instance == null) {
                    instance = new MusicNotificationManager2(context);
                }
            }
        }
        return instance;
    }

    private MusicNotificationManager2(Context context) {
        mContext = context.getApplicationContext();
    }

    public Notification getNotification(MediaDescriptionCompat description, MediaSessionCompat.Token token) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, Constants.NOTIFICATION_CHANNEL_ID);

        builder
                // Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())

                // Enable launching the player by clicking the notification
                .setContentIntent(createContentIntent())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
//                .setSmallIcon(R.drawable.notification_icon)
                .setSmallIcon(R.mipmap.icon)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))

                .addAction(new NotificationCompat.Action(
                        android.R.drawable.ic_media_previous, mContext.getString(R.string.music_previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        android.R.drawable.ic_media_pause, mContext.getString(R.string.music_pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_PAUSE)))

                .addAction(new NotificationCompat.Action(
                        android.R.drawable.ic_media_next, mContext.getString(R.string.music_next),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

                .setOngoing(true)

                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1 , 2)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_STOP)));
        return builder.build();
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mContext, MediaSessionActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mContext, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
