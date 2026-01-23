package com.winjay.practice.notification;

import android.app.Notification;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
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

        getMediaMetadata(notification);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 通知被移除时触发
        String packageName = sbn.getPackageName();
        LogUtil.d(TAG, packageName + " 移除通知！");
    }

    // 获取多媒体通知元数据
    private void getMediaMetadata(Notification notification) {
        // 1. 取出 MediaSession.Token
        MediaSession.Token token = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            token = notification.extras.getParcelable(
                    Notification.EXTRA_MEDIA_SESSION,
                    MediaSession.Token.class);
        } else {
            // 旧版本写法
            token = (MediaSession.Token) notification.extras.get(
                    Notification.EXTRA_MEDIA_SESSION);
        }
        if (token == null) return;   // 不是媒体通知

        // 2. 构造 MediaController
        MediaController controller = new MediaController(this, token);

        // 3. 读取元数据
        MediaMetadata metadata = controller.getMetadata();
        if (metadata == null) return;

        String media_title   = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        String artist  = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        String album   = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
        long duration  = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        Bitmap artwork = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);

        LogUtil.d(TAG, media_title + " — " + artist + "  (" + duration + " ms)");

        // 如果还想实时拿播放状态，也可以：
        PlaybackState state = controller.getPlaybackState();
        if (state != null) {
            boolean isPlaying = state.getState() == PlaybackState.STATE_PLAYING;
            LogUtil.d(TAG, "isPlaying = " + isPlaying);
        }
    }
}
