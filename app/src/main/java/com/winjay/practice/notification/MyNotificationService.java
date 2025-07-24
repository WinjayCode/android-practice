package com.winjay.practice.notification;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.winjay.practice.utils.LogUtil;

/**
 * 监听其他应用的通知消息（权限请求放到 NotificationActivity 类中）
 *
 * @author Winjay
 * @date 2025/07/24
 */
public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "MyNotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 接收到新通知时触发
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        // 解析通知内容
        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);

        LogUtil.d(TAG, "来自应用: " + packageName);
        LogUtil.d(TAG, "标题: " + title);
        LogUtil.d(TAG, "内容: " + text);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 通知被移除时触发
        String packageName = sbn.getPackageName();
        LogUtil.d(TAG, packageName + " 移除通知！");
    }
}
