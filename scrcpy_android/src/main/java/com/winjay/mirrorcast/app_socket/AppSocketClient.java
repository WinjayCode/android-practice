package com.winjay.mirrorcast.app_socket;

import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class AppSocketClient extends WebSocketClient {
    private static final String TAG = AppSocketClient.class.getSimpleName();

    private AppSocketClientListener appSocketClientListener;

    private AppSocketClientCloseListener mAppSocketClientCloseListener;

    private Timer timer;
    private TimerTask timerTask;

    public AppSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtil.d(TAG);

        // 使用心跳机制，保持websocket长链接（因为websocket在不确定的时间内两端没有任何通信时会断开连接）
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                if (isOpen()) {
                    send("ping");
                } else {
                    LogUtil.d(TAG, "Websocket had been disconnected!");
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }
        };
        // 间隔30s
        timer.schedule(timerTask,0, 30000);
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (message.equals("pong")) {
//            LogUtil.d(TAG, "receive pong.");
            return;
        }

        if (appSocketClientListener != null) {
            appSocketClientListener.onMessage(message);
        }
        AppSocketManager.getInstance().handleMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtil.d(TAG, "code=" + code + ", reason=" + reason + ", remote=" + remote);
        if (timer != null) {
            timer.cancel();
        }

        if (mAppSocketClientCloseListener!= null) {
            mAppSocketClientCloseListener.onClose(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        LogUtil.e(TAG, ex.getMessage());
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setAppSocketClientListener(AppSocketClientListener listener) {
        appSocketClientListener = listener;
    }

    public interface AppSocketClientListener {
        void onMessage(String message);
    }

    public void setAppSocketClientCloseListener(AppSocketClientCloseListener listener) {
        mAppSocketClientCloseListener = listener;
    }

    public interface AppSocketClientCloseListener {
        void onClose(int code, String reason, boolean remote);
    }
}
