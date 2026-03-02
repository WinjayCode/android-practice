package com.winjay.practice.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import com.winjay.practice.Constants;
import com.winjay.practice.utils.LogUtil;

/**
 * 在广播中处理通知的快速回复，避免为每次回复启动 NotificationActivity（从而避免 Activity 被多次启动）。
 */
public class NotificationReplyReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReplyReceiver";
    private static final long DUPLICATE_WINDOW_MS = 1000L;
    private static long sLastReplyAtMs;
    private static int sLastReplyNotificationId = -1;
    private static String sLastReplyMsg;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Constants.ACTION_NOTIFICATION_REPLY.equals(intent.getAction())) {
            return;
        }
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent == null) {
            LogUtil.d(TAG, "no reply msg!");
            return;
        }
        CharSequence replyCharSeq = resultsFromIntent.getCharSequence(NotificationActivity.KEY_TEXT_REPLY);
        String replyMsg = replyCharSeq != null ? replyCharSeq.toString() : "";
        LogUtil.d(TAG, "reply msg=" + replyMsg);

        int notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, -1);
        if (isDuplicateReply(notificationId, replyMsg)) {
            LogUtil.d(TAG, "duplicate reply ignored, id=" + notificationId);
            return;
        }

        if (notificationId >= 0) {
            android.app.NotificationManager nm = (android.app.NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancel(notificationId);
            }
        }

        NotificationActivity.postReplyNotification(context, replyMsg);
    }

    private static synchronized boolean isDuplicateReply(int notificationId, String replyMsg) {
        long now = System.currentTimeMillis();
        if (now - sLastReplyAtMs <= DUPLICATE_WINDOW_MS
                && sLastReplyNotificationId == notificationId
                && replyMsg.equals(sLastReplyMsg)) {
            return true;
        }
        sLastReplyAtMs = now;
        sLastReplyNotificationId = notificationId;
        sLastReplyMsg = replyMsg;
        return false;
    }
}
