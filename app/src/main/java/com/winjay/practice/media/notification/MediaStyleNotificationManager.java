package com.winjay.practice.media.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.media.mediasession.MediaSessionActivity;
import com.winjay.practice.thread.HandlerManager;

import java.util.concurrent.ExecutionException;

/**
 * 音乐通知栏（无法添加自定义按钮？）
 * <p>
 * 系统MediaStyle样式
 * Notification noti = new NotificationCompat.Builder()
 * .setSmallIcon(R.drawable.ic_stat_player)
 * .setContentTitle("Track title")
 * .setContentText("Artist - Album")
 * .setLargeIcon(albumArtBitmap))
 * .setStyle(new NotificationCompat.MediaStyle()
 * .setMediaSession(mySession))
 * .build();
 *
 * @author Winjay
 * @date 2023-02-28
 */
public class MediaStyleNotificationManager {
    private static final String TAG = MediaStyleNotificationManager.class.getSimpleName();
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

    public void getNotification(MediaControllerCompat controller, MediaDescriptionCompat description,
                                MediaSessionCompat.Token token, NotificationCallback callback) {
        resolveUriAsBitmap(description.getIconUri(), new LargeIconCallback() {
            @Override
            public void onLargeIconDone(Bitmap bitmap) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, Constants.NOTIFICATION_CHANNEL_ID);
                builder
                        // Add the metadata for the currently playing track
                        .setContentTitle(description.getTitle())
                        .setContentText(description.getSubtitle())
                        .setSubText(description.getDescription())
                        .setLargeIcon(bitmap)

                        // Enable launching the player by clicking the notification
                        .setContentIntent(controller.getSessionActivity()) // null?
//                        .setContentIntent(createContentIntent())

                        // Stop the service when the notification is swiped away
//                        .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
//                                PlaybackStateCompat.ACTION_STOP))

                        // Make the transport controls visible on the lockscreen
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                        // Add an app icon and set its accent color
                        // Be careful about the color
                        .setSmallIcon(R.mipmap.icon)
//                        .setColor(ContextCompat.getColor(mContext, R.color.primaryDark))

                        // Add a pause button
//                        .addAction(
//                                R.drawable.ic_play_arrow_white_24dp, "pause",
//                                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
//                                        PlaybackStateCompat.ACTION_PLAY_PAUSE))

                        // Take advantage of MediaStyle features
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(token)
                        )
                        .setOngoing(true);
                callback.onPrepared(builder.build());
            }
        });
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mContext, MediaSessionActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mContext, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void resolveUriAsBitmap(Uri uri, LargeIconCallback callback) {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                RequestOptions requestOptions = new RequestOptions()
                        .fallback(R.mipmap.icon)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
                try {
                    Bitmap bitmap = Glide.with(mContext).applyDefaultRequestOptions(requestOptions)
                            .asBitmap()
                            .load(uri)
                            .submit(144, 144)
                            .get();
                    callback.onLargeIconDone(bitmap);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface LargeIconCallback {
        void onLargeIconDone(Bitmap bitmap);
    }

    public interface NotificationCallback {
        void onPrepared(Notification notification);
    }
}
