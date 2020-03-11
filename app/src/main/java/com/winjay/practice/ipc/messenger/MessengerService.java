package com.winjay.practice.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.winjay.practice.Constants;
import com.winjay.practice.utils.LogUtil;

/**
 * IPC-Messenger-服务端
 *
 * @author Winjay
 * @date 2020-02-18
 */
public class MessengerService extends Service {
    private static final String TAG = MessengerService.class.getSimpleName();

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_FROM_CLIENT:
                    // 无回复的
                    LogUtil.d(TAG, "receive msg from Client:" + msg.getData().getString("msg"));

                    // 有回复的
                    Messenger client = msg.replyTo;

                    Message replyMessage = Message.obtain(null, Constants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "roger that, reply later.");
                    replyMessage.setData(bundle);

                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
