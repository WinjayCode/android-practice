package com.winjay.mirrorcast.aoa;

import android.text.TextUtils;

import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 负责转发scrcpy-server.jar的数据给AOA Host端
 *
 * @author Winjay
 * @date 2023-04-24
 */
public class PhoneAOASocketServer extends WebSocketServer {
    private static final String TAG = PhoneAOASocketServer.class.getSimpleName();
    private WebSocket mWebSocket;
    private OnWebSocketServerListener mOnWebSocketServerListener;


    public PhoneAOASocketServer(InetSocketAddress address) {
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
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (mOnWebSocketServerListener != null) {
            mOnWebSocketServerListener.onReceiveByteData(buf);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (mOnWebSocketServerListener != null) {
            LogUtil.e(TAG, ex.getMessage());
            mOnWebSocketServerListener.onError(ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG);
    }

    public void sendMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mWebSocket != null && !TextUtils.isEmpty(message) && mWebSocket.isOpen()) {
            mWebSocket.send(message);
        }
    }

    public void setOnWebSocketServerListener(OnWebSocketServerListener listener) {
        mOnWebSocketServerListener = listener;
    }

    public interface OnWebSocketServerListener {
        void onOpen();

        void onMessage(String message);

        void onClose(String reason);

        void onError(String errorMessage);

        void onReceiveByteData(byte[] data);
    }
}
