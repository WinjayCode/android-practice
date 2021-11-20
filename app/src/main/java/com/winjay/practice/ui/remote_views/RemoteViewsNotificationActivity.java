package com.winjay.practice.ui.remote_views;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.OnClick;

/**
 * RemoteViews学习
 * 主要用在通知栏和桌面小部件
 *
 * @author Winjay
 * @date 2021-11-18
 */
public class RemoteViewsNotificationActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.remote_views_activity;
    }

    @OnClick(R.id.notification_btn)
    void notification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, RemoteViewsNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remoteviews_notification_layout);
        remoteViews.setTextViewText(R.id.notification_tv, "RemoteViews Notification");
        remoteViews.setImageViewResource(R.id.notification_iv, R.drawable.kui_icon);
        remoteViews.setOnClickPendingIntent(R.id.notification_iv, pendingIntent);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("RemoteViews Test Notification")
                .setContentText("lalalalalala")
                .setSmallIcon(R.drawable.kui_icon)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, mBuilder.build());
    }
}
