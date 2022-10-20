package com.winjay.practice.media.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.music.MusicPlayActivity;

/**
 * 音乐通知栏
 *
 * @author Winjay
 * @date 21/02/34
 */
public class MusicNotificationManager {
    private static volatile MusicNotificationManager instance;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationChannel notificationChannel;

    private MusicNotificationManager(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_notification);
        remoteViews.setImageViewResource(R.id.album_iv, R.drawable.bird);

        Intent contentIntent = new Intent(context, MusicPlayActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setContent(remoteViews)
                .setWhen(System.currentTimeMillis())
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        // 上一首
        Intent intentPrev = new Intent(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 0, intentPrev, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.prev_iv, prevPendingIntent);

        // 播放
        Intent intentPlay = new Intent(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.play_iv, playPendingIntent);

        // 暂停
        Intent intentPause = new Intent(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, intentPause, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.pause_iv, pausePendingIntent);

        // 下一首
        Intent intentNext = new Intent(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.next_iv, nextPendingIntent);

        // 关闭通知栏
        Intent intentClose = new Intent(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(context, 0, intentClose, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.close_iv, closePendingIntent);

        notification = notificationBuilder.build();
        notification.flags = notification.FLAG_ONGOING_EVENT;

        notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "播放控制", NotificationManager.IMPORTANCE_DEFAULT);
        // 关闭通知提示音
        notificationChannel.setSound(null, null);
//        //是否绕过请勿打扰模式
//        notificationChannel.canBypassDnd();
//        //闪光灯
//        notificationChannel.enableLights(true);
        // 锁屏显示通知
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//        //闪关灯的灯光颜色
//        notificationChannel.setLightColor(Color.RED);
//        //桌面launcher的消息角标
//        notificationChannel.canShowBadge();
//        //是否允许震动
//        notificationChannel.enableVibration(true);
//        //获取系统通知响铃声音的配置
//        notificationChannel.getAudioAttributes();
//        //获取通知取到组
//        notificationChannel.getGroup();
//        //设置可绕过  请勿打扰模式
//        notificationChannel.setBypassDnd(true);
//        //设置震动模式
//        notificationChannel.setVibrationPattern(new long[]{100, 100, 200});
//        //是否会有灯光
//        notificationChannel.shouldShowLights();
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static MusicNotificationManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicNotificationManager.class) {
                if (instance == null) {
                    instance = new MusicNotificationManager(context);
                }
            }
        }
        return instance;
    }

    public void setMediaData(AudioBean audioBean) {
        if (!TextUtils.isEmpty(audioBean.getDisplayName())) {
            remoteViews.setTextViewText(R.id.music_name_tv, audioBean.getDisplayName());
        }
    }

    public void showMusicNotification(boolean isPlaying) {
        if (isPlaying) {
            remoteViews.setViewVisibility(R.id.play_iv, View.GONE);
            remoteViews.setViewVisibility(R.id.pause_iv, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.play_iv, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.pause_iv, View.GONE);
        }
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    public void cancel() {
        notificationManager.cancel(Constants.NOTIFICATION_ID);
    }
}
