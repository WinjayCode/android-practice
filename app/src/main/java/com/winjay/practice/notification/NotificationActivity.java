package com.winjay.practice.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Notification学习（未适配高版本）
 *
 * @author Winjay
 * @date 2020/10/13
 */
public class NotificationActivity extends BaseActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();
    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 0;

    @BindView(R.id.visibility_rg)
    RadioGroup visibility_rg;

    private int notificationGrade = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.notification_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        visibility_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_1:
                        notificationGrade = 1;
                        break;
                    case R.id.rb_2:
                        notificationGrade = 2;
                        break;
                    case R.id.rb_3:
                        notificationGrade = 3;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 基础类型的通知
     *
     * @param view
     */
    @OnClick(R.id.basic_notification)
    void basic(View view) {
        LogUtil.d(TAG, "basic()");
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
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    /**
     * 折叠类型的通知
     *
     * @param view
     */
    @OnClick(R.id.collapsed_notification)
    void collapsed(View view) {
        LogUtil.d(TAG, "collapsed()");
        // 通过RemoteViews来创建自定义的Collapsed Notification视图
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_notification_layout);
        collapsedView.setTextViewText(R.id.collapsed_notification_tv, "show me when collapsed");
        // 通过RemoteViews来创建自定义的Expanded Notification视图
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_notification_layout);
        expandedView.setTextViewText(R.id.collapsed_notification_tv, "show me when collapsed");

        // 创建点击意图
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 创建通知
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.kui_icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.kui_icon));
//        builder.setCustomContentView(collapsedView);
//        builder.setCustomBigContentView(expandedView);

        Notification notification = builder.build();
        notification.contentView = collapsedView;
        notification.bigContentView = expandedView;

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    /**
     * 悬挂类型的通知
     *
     * @param view
     */
    @OnClick(R.id.headsup_notification)
    void headsup(View view) {
        LogUtil.d(TAG, "headsup()");
        // 创建通知
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.kui_icon)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("Headsup Notification")
                .setContentText("I am a Headsup notification.");

        Intent push = new Intent();
        push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        push.setClass(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, push, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentText("Heads-Up Notification on Android 5.0")
                .setFullScreenIntent(pendingIntent, true);

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    /**
     * Notification显示等级
     *
     * @param view
     */
    @OnClick(R.id.visibility_btn)
    void visibility(View view) {
        LogUtil.d(TAG, "visibility()");
        // 创建通知
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Notification for Visibility Test")
                .setSmallIcon(R.drawable.kui_icon);
        if (notificationGrade == 1) {
            // 任何情况下都会显示
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            builder.setContentText("Public");
        } else if (notificationGrade == 2) {
            // 只有当没有锁屏的时候会显示
            builder.setVisibility(Notification.VISIBILITY_PRIVATE);
            builder.setContentText("Private");
        } else if (notificationGrade == 3) {
            // 在pin、password等安全锁和没有锁屏的情况下才能够显示
            builder.setVisibility(Notification.VISIBILITY_SECRET);
            builder.setContentText("Secret");
        }

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }
}