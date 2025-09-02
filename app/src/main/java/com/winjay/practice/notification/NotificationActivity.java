package com.winjay.practice.notification;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.Collections;

/**
 * Notification学习（未适配高版本）
 *
 * @author Winjay
 * @date 2020/10/13
 */
public class NotificationActivity extends BaseActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();

    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;
    private NotificationChannel mSilentNotificationChannel;

    private int NOTIFICATION_ID = 0;

    RadioGroup visibility_rg;

    private int notificationGrade = 1;

    public static final String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    protected String[] permissions() {
        // android 13 运行时通知权限
        return new String[]{Manifest.permission.POST_NOTIFICATIONS};
    }

    @Override
    protected int getLayoutId() {
        return R.layout.notification_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestNotificationPermission();

        visibility_rg = findViewById(R.id.visibility_rg);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        // Silent NotificationChannel
        mSilentNotificationChannel = new NotificationChannel(Constants.SILENT_NOTIFICATION_CHANNEL_ID,
                Constants.SILENT_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        mSilentNotificationChannel.setSound(null, null);
        mSilentNotificationChannel.enableVibration(false);
        // 是否在桌面应用图标上显示角标
        mSilentNotificationChannel.setShowBadge(false);

        mNotificationManager.createNotificationChannel(mNotificationChannel);
        mNotificationManager.createNotificationChannel(mSilentNotificationChannel);

        visibility_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    notificationGrade = 1;
                } else if (checkedId == R.id.rb_2) {
                    notificationGrade = 2;
                } else if (checkedId == R.id.rb_3) {
                    notificationGrade = 3;
                }
            }
        });

        handleReplyNotification();

        findViewById(R.id.basic_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basic(v);
            }
        });
        findViewById(R.id.collapsed_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed();
            }
        });
        findViewById(R.id.headsup_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headsup();
            }
        });
        findViewById(R.id.reply_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyNotification();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        replyNotification();
//                    }
//                }, 5000);
            }
        });
        findViewById(R.id.silent_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silentNotification();
            }
        });
        findViewById(R.id.test_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNotification();
            }
        });
        findViewById(R.id.visibility_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibility();
            }
        });
    }

    /**
     * 跳转到通知读取权限设置界面
     */
    public void goToNotificationPermissionPage() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    /**
     * 检查权限是否已授权
     */
    public boolean isNotificationPermissionGranted() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(),
                "enabled_notification_listeners"
        );
        return enabledListeners != null && enabledListeners.contains(getPackageName());
    }

    public void requestNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要通知权限");
            builder.setMessage("请允许应用读取通知以继续使用功能");
            builder.setPositiveButton("去设置", (dialog, which) ->
                    goToNotificationPermissionPage()
            );
            builder.show();
        }
    }

    /**
     * 基础类型的通知
     *
     * @param view
     */
    void basic(View view) {
        LogUtil.d(TAG);
        // 创建点击意图
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Uri uri2 = Uri.parse("android.resource://com.winjay.practice/" + R.mipmap.ic_launcher_round);
        Person person = new Person.Builder()
                .setName("Name:Winjay")
                .setIcon(IconCompat.createWithContentUri(uri2))
//                .setUri(uri2.toString())
                .setImportant(true)
                .build();

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
        builder.setContentTitle("Basic Notification");
        builder.setContentText("I am a basic notification");
        builder.setSubText("it is really basic");
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // android13之前的版本可以实现常驻通知栏，android14之后用户在未锁屏的情况下可以删除，但是在锁屏下仍然不可以删除
        builder.setOngoing(true);
        builder.addPerson(person);
        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setGroup("111");

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());

//        test();
    }

    private void test() {
        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.mipmap.icon) // 设置通知小图标
                .setContentTitle("Sienna") // 设置通知标题
                .setContentText("Sounds good. See you there!") // 设置通知内容
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 设置通知优先级
                .setAutoCancel(true); // 设置通知自动取消

        // 如果有头像资源，可以这样设置
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.artwork_placeholder);
        builder.setLargeIcon(bitmap);

        // 设置时间戳
        long timestamp = System.currentTimeMillis();
        builder.setWhen(timestamp);

        // 如果有消息时间，可以这样设置
        builder.setShowWhen(true);
        builder.setTicker("5m ago");

        // 如果是消息类型的通知，可以使用MessagingStyle
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Sienna")
                .setConversationTitle("Chat Title");

        // 添加消息
        messagingStyle.addMessage("Hi", timestamp, "Sienna");
        messagingStyle.addMessage("How are you?", timestamp + 1000, "Sienna");
        messagingStyle.addMessage("Sounds good. See you there!", timestamp + 2000, "Sienna");

        builder.setStyle(messagingStyle);

        /*// 获取NotificationManager服务
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // 显示通知
        notificationManager.notify(1, builder.build());*/

        Bundle bundle = new Bundle();
        bundle.putBoolean("key", false);
        builder.addExtras(bundle);

        visibility_rg.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.notify(1, builder.build());
            }
        }, 5000);
//        mNotificationManager.notify(1, builder.build());
    }

    /**
     * 折叠类型的通知
     */
    void collapsed() {
        LogUtil.d(TAG);
        // 通过RemoteViews来创建自定义的Collapsed Notification视图
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_notification_layout);
        collapsedView.setTextViewText(R.id.collapsed_notification_tv, "show me when collapsed");
        // 通过RemoteViews来创建自定义的Expanded Notification视图
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_notification_layout);
        expandedView.setTextViewText(R.id.collapsed_notification_tv, "show me when expanded");

        // 创建点击意图
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
        builder.setCustomContentView(collapsedView);
        builder.setCustomBigContentView(expandedView);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        Notification notification = builder.build();
//        notification.contentView = collapsedView;
//        notification.bigContentView = expandedView;

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    /**
     * 悬挂类型的通知
     */
    void headsup() {
        LogUtil.d(TAG);
        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOngoing(true)
                .setTimeoutAfter(10 * 60 * 1000)
                .setContentTitle("Headsup Notification")
                .setContentText("I am a Headsup notification.");

        Intent push = new Intent();
        push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        push.setClass(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, push, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentText("Heads-Up Notification on Android 5.0")
                .setFullScreenIntent(pendingIntent, true);

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    /**
     * Notification显示等级
     */
    void visibility() {
        LogUtil.d(TAG);
        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Notification for Visibility Test")
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (notificationGrade == 1) {
            // 任何情况下都会显示
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setContentText("Public-任何情况下都会显示");
        } else if (notificationGrade == 2) {
            // 只有当没有锁屏的时候会显示
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            builder.setContentText("Private-只有当没有锁屏的时候会显示");
        } else if (notificationGrade == 3) {
            // 在pin、password等安全锁和没有锁屏的情况下才能够显示
            builder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
            builder.setContentText("Secret-在pin、password等安全锁和没有锁屏的情况下才能够显示");
        }

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    void replyNotification() {
        LogUtil.d(TAG);
        sendReplyNotification("I am a reply notification.");
    }

    private void sendReplyNotification(String content) {
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.mipmap.ic_launcher_round)
                + '/' + getResources().getResourceTypeName(R.mipmap.ic_launcher_round)
                + '/' + getResources().getResourceEntryName(R.mipmap.ic_launcher_round));
        Uri uri2 = Uri.parse("android.resource://com.winjay.practice/" + R.mipmap.ic_launcher_round);
        Person person = new Person.Builder()
                .setName("Name:Winjay")
                .setIcon(IconCompat.createWithContentUri(uri2))
                .setUri(uri2.toString())
                .setImportant(true)
                .build();

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("Reply Notification")
                .setContentText(content)
                .setGroup("haha")
                .addPerson(person);

        Intent replyIntent = new Intent(this, NotificationActivity.class);
        replyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("快速回复")
                .build();
        NotificationCompat.Action actionReply = new NotificationCompat.Action.Builder(0, "回复", pendingIntent)
                .addRemoteInput(remoteInput).build();
        builder.setFullScreenIntent(pendingIntent, true)
                .addAction(actionReply);

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat
                .MessagingStyle(person).
                addMessage("MessageStyle", 1000, "sender");
        builder.setStyle(messagingStyle);

        // Create a sharing shortcut.
        String shortcutId = "shortcutId";
        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, shortcutId).setCategories(Collections.singleton("CATEGORY_TEXT_SHARE_TARGET")).setIntent(new Intent(Intent.ACTION_DEFAULT)).setLongLived(true).setShortLabel(person.getName()).build();

        // Create a bubble metadata.
        NotificationCompat.BubbleMetadata bubbleData = new NotificationCompat.BubbleMetadata.Builder(pendingIntent, IconCompat.createWithResource(this, R.drawable.album_jazz_blues)).setDesiredHeight(600).build();

        builder.setBubbleMetadata(bubbleData);
        builder.setShortcutId(shortcutId);
        builder.setShortcutInfo(shortcut);

        // 发出通知
        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    // 该操作建议放到BroadCastReceiver中或者其他地方处理，否则会启动多次activity
    private void handleReplyNotification() {
        LogUtil.d(TAG);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
            if (resultsFromIntent == null) {
                LogUtil.d(TAG, "no reply msg!");
                return;
            }

            String replyMsg = (String) resultsFromIntent.getCharSequence(KEY_TEXT_REPLY);
            LogUtil.d(TAG, "reply msg=" + replyMsg);

            sendReplyNotification(replyMsg);

            // 可实现类似回复短信，显示最近3条回复的效果
//            mNotificationManager.cancel(1);
        }
    }

    /**
     * 静默通知
     */
    private void silentNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Silent Notification")
                .setContentText("I am a silent notification!")
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // 关键设置：不显示在状态栏
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                // 关键设置：无提示
                .setSilent(true)
                // 关键设置：不显示通知图标
                .setShowWhen(false);
//                .setOngoing(true); // 加这个选项，SystemUI通知栏中的Silent横条后不显示 清除 按钮

        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());
    }

    private void testNotification() {
        /*Intent push = new Intent();
        push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        push.setClass(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, push, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a new call with the user as caller.
        Person incoming_caller = new Person.Builder()
                .setName("Jane Doe")
                .setImportant(true)
                .build();

        // Create a call style notification for an incoming call.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.icon)
                .setStyle(
                        NotificationCompat.CallStyle.forIncomingCall(incoming_caller, pendingIntent, pendingIntent))
                .setFullScreenIntent(pendingIntent, true)
                .addPerson(incoming_caller);

        mNotificationManager.notify(++NOTIFICATION_ID, builder.build());*/
    }
}
