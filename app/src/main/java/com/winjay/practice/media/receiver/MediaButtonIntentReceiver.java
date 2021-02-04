package com.winjay.practice.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.winjay.practice.utils.LogUtil;

/**
 * 多媒体按键广播接收器
 *
 * @author Winjay
 * @date 1/16/21
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private static final String TAG = MediaButtonIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }
            int keycode = event.getKeyCode();
            LogUtil.d(TAG, "keycode=" + keycode);
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    //CMD STOP
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    //CMD TOGGLE PAUSE
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //CMD NEXT 这里处理播放器逻辑 下一曲
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    //CMD PREVIOUS 这里处理播放器逻辑 上一曲
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    //CMD PAUSE 这里处理播放器逻辑 暂停
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    //CMD PLAY 这里处理播放器逻辑 播放
                    break;
            }
        }
    }
}