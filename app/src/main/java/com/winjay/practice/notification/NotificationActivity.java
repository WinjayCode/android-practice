package com.winjay.practice.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.OnClick;

public class NotificationActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.notification_activity;
    }

    @OnClick(R.id.basic_notification)
    void basic(View view) {
        // 创建点击意图
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 创建通知
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.kui_icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.kui_icon));
        builder.setContentTitle("Basic Notification");
        builder.setContentText("I am a basic notification");
        builder.setSubText("it is really basic");

        // 发出通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @OnClick(R.id.collapsed_notification)
    void collapsed(View view) {
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.collapsed_notification_layout);
    }
}
