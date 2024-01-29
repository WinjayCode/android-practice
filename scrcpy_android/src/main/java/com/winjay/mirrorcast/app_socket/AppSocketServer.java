package com.winjay.mirrorcast.app_socket;

import android.text.TextUtils;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class AppSocketServer extends WebSocketServer {
    private static final String TAG = AppSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;
    private OnAppSocketServerListener mOnAppSocketServerListener;
    private OnAppSocketServerErrorListener mOnAppSocketServerErrorListener;

    public AppSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
        LogUtil.d(TAG);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogUtil.d(TAG);
        mWebSocket = conn;
        LogUtil.d(TAG, "client ip=" + conn.getRemoteSocketAddress().getAddress());
        AppApplication.destDeviceIp = conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtil.d(TAG, "onClose:" + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, "message=" + message);
        if (message.equals("ping")) {
//            LogUtil.d(TAG, "receive ping and send pong.");
            sendMessage("pong");
            return;
        }

        if (mOnAppSocketServerListener != null) {
            mOnAppSocketServerListener.onMessage(message);
        }
        AppSocketManager.getInstance().handleMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtil.e(TAG, ex.getMessage());

        if (mOnAppSocketServerErrorListener != null) {
            mOnAppSocketServerErrorListener.onError();
        }
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

    public void setOnAppSocketServerErrorListener(OnAppSocketServerErrorListener listener) {
        mOnAppSocketServerErrorListener = listener;
    }

    public interface OnAppSocketServerErrorListener {
        void onError();
    }
}
