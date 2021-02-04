package com.winjay.practice.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.winjay.practice.Constants;
import com.winjay.practice.media.interfaces.IMediaStatus;
import com.winjay.practice.media.notification.MusicNotificationManager;
import com.winjay.practice.utils.LogUtil;

/**
 * 多媒体通知栏广播接收
 *
 * @author Winjay
 * @date 21/02/34
 */
public class MediaNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = MediaNotificationReceiver.class.getSimpleName();
    private IMediaStatus iMediaStatus;

    public MediaNotificationReceiver(IMediaStatus iMediaStatus) {
        this.iMediaStatus = iMediaStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG);
        if (intent == null || TextUtils.isEmpty(intent.getAction()) || iMediaStatus == null) {
            return;
        }
        LogUtil.d(TAG, "action=" + intent.getAction());
        switch (intent.getAction()) {
            case Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV:
                iMediaStatus.prev();
                break;
            case Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY:
                iMediaStatus.play();
                break;
            case Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE:
                iMediaStatus.pause();
                break;
            case Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT:
                iMediaStatus.next();
                break;
            case Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE:
                MusicNotificationManager.getInstance(context).cancel();
                break;
            default:
                break;
        }
    }
}
