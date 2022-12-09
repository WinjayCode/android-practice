package com.winjay.mirrorcast.app_mirror;

import android.text.TextUtils;

import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class AppSocketServer extends WebSocketServer {
    private static final String TAG = AppSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;
    private OnAppSocketServerListener mOnAppSocketServerListener;

    public AppSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
        LogUtil.d(TAG);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogUtil.d(TAG);
        mWebSocket = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtil.d(TAG, "onClose:" + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mOnAppSocketServerListener != null) {
            mOnAppSocketServerListener.onMessage(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtil.e(TAG, ex.getMessage());

//        HandlerManager.getInstance().postOnSubThread(new Runnable() {
//            @Override
//            public void run() {
//                AppMirrorSocketServerManager.getInstance().restartServer();
//            }
//        });
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG);
    }

    public void sendMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mWebSocket != null && !TextUtils.isEmpty(message)) {
            mWebSocket.send(message);
        }
    }

    public void setAppSocketServerListener(OnAppSocketServerListener listener) {
        mOnAppSocketServerListener = listener;
    }

    public interface OnAppSocketServerListener {
        void onMessage(String message);
    }
}
