package com.winjay.practice.net.socket.websocket;

import android.text.TextUtils;

import com.winjay.practice.utils.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * WebSocketServer
 *
 * @author Winjay
 * @date 2023-04-17
 */
public class MyWebSocketServer extends WebSocketServer {
    private static final String TAG = MyWebSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;
    private OnWebSocketServerListener mOnWebSocketServerListener;


    public MyWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogUtil.d(TAG);
        mWebSocket = conn;
        if (mOnWebSocketServerListener != null) {
            mOnWebSocketServerListener.onOpen();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtil.d(TAG, reason);
        if (mOnWebSocketServerListener != null) {
            mOnWebSocketServerListener.onClose(reason);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, message);
        if (mOnWebSocketServerListener != null) {
            mOnWebSocketServerListener.onMessage(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (mOnWebSocketServerListener != null) {
            LogUtil.w(TAG, ex.getMessage());
            mOnWebSocketServerListener.onError(ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG);
        if (mOnWebSocketServerListener != null) {
            mOnWebSocketServerListener.onStart();
        }
    }

    public void sendMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mWebSocket != null && !TextUtils.isEmpty(message)) {
            mWebSocket.send(message);
        }
    }

    public void close() {
        if (mWebSocket != null) {
            mWebSocket.close();
        }
    }

    public void setOnWebSocketServerListener(OnWebSocketServerListener listener) {
        mOnWebSocketServerListener = listener;
    }

    public interface OnWebSocketServerListener {
        void onOpen();

        void onStart();

        void onMessage(String message);

        void onClose(String reason);

        void onError(String errorMessage);
    }
}
