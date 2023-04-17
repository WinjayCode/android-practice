package com.winjay.practice.net.socket.websocket;

import com.winjay.practice.utils.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocketClient
 *
 * @author Winjay
 * @date 2023-04-17
 */
public class MyWebSocketClient extends WebSocketClient {
    private static final String TAG = MyWebSocketClient.class.getSimpleName();
    private OnWebSocketClientListener mOnWebSocketClientListener;

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtil.d(TAG);
        if (mOnWebSocketClientListener != null) {
            mOnWebSocketClientListener.onOpen();
        }
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(TAG, message);
        if (mOnWebSocketClientListener != null) {
            mOnWebSocketClientListener.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtil.d(TAG, reason);
        if (mOnWebSocketClientListener != null) {
            mOnWebSocketClientListener.onClose(reason);
        }
    }

    @Override
    public void onError(Exception ex) {
        LogUtil.w(TAG, ex.getMessage());
        if (mOnWebSocketClientListener != null) {
            mOnWebSocketClientListener.onError(ex.getMessage());
        }
    }

    public void setOnWebSocketClientListener(OnWebSocketClientListener listener) {
        mOnWebSocketClientListener = listener;
    }

    public interface OnWebSocketClientListener {
        void onOpen();

        void onMessage(String message);

        void onClose(String reason);

        void onError(String errorMessage);
    }
}
