package com.winjay.practice.media.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.media.mediasession.MediaSessionActivity;

/**
 * 音乐通知栏（系统MediaStyle样式）
 *
 * @author Winjay
 * @date 2023-02-28
 */
public class MediaStyleNotificationManager {
    private static volatile MediaStyleNotificationManager instance;
    private final Context mContext;
    private static final int REQUEST_CODE = 501;

    public static MediaStyleNotificationManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MediaStyleNotificationManager.class) {
                if (instance == null) {
                    instance = new MediaStyleNotificationManager(context);
                }
            }
        }
        return instance;
    }

    private MediaStyleNotificationManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public Notification getNotification(MediaDescriptionCompat description, MediaSessionCompat.Token token) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.cancelAll();

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.media_style_notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, Constants.NOTIFICATION_CHANNEL_ID);

        builder
//                .setContent(remoteViews)
//                .setCustomContentView(remoteViews)
//                .setCustomBigContentView(remoteViews)
//                .setCustomHeadsUpContentView(remoteViews)
//                .setAllowSystemGeneratedContextualActions(true)

                // Add the metadata for the currently playing track
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
//                .setLargeIcon(description.getIconBitmap())
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.album_youtube_audio_library_rock_2))

                // Enable launching the player by clicking the notification
                .setContentIntent(createContentIntent())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .setDefaults(0)

//                .setWhen(System.currentTimeMillis())
//                .setShowWhen(true)
//                .setUsesChronometer(true)

                // Add an app icon and set its accent color
                // Be careful about the color
//                .setSmallIcon(R.drawable.notification_icon)
                .setSmallIcon(R.mipmap.icon)
//                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_play_arrow_white_24dp, mContext.getString(R.string.music_previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_play_arrow_white_24dp, mContext.getString(R.string.music_pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_PAUSE)))

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_play_arrow_white_24dp, mContext.getString(R.string.music_next),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1, 2)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                                PlaybackStateCompat.ACTION_STOP))
                )

                .setOngoing(true);
        return builder.build();
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mContext, MediaSessionActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mContext, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
