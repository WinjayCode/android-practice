package com.winjay.mirrorcast.decode;

import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author F2848777
 * @date 2022-12-01
 */
public class ScreenDecoderSocketServer extends WebSocketServer {
    private static final String TAG = ScreenDecoderSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;
    private OnSocketServerListener mOnSocketServerListener;
    private OnReceiveByteDataListener mOnReceiveByteDataListener;

    public ScreenDecoderSocketServer(InetSocketAddress inetSocketAddress,
                                     OnSocketServerListener onSocketServerListener,
                                     OnReceiveByteDataListener onReceiveByteDataListener) {
        super(inetSocketAddress);
        LogUtil.d(TAG);
        mOnSocketServerListener = onSocketServerListener;
        mOnReceiveByteDataListener = onReceiveByteDataListener;
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogUtil.d(TAG);
        mWebSocket = conn;
        if (mOnSocketServerListener != null) {
            mOnSocketServerListener.onOpen();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtil.d(TAG, "onClose =" + reason);
        if (mOnSocketServerListener != null) {
            mOnSocketServerListener.onClose();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mOnSocketServerListener != null) {
            mOnSocketServerListener.onMessage(message);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (mOnReceiveByteDataListener != null) {
            mOnReceiveByteDataListener.onReceiveByteData(buf);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtil.d(TAG, "onError =" + ex.toString());
    }

    public void sendMessage(String message) {
        if (mWebSocket != null) {
            mWebSocket.send(message);
        }
    }

    public interface OnSocketServerListener {
        void onOpen();

        void onClose();

        void onMessage(String message);
    }

    public interface OnReceiveByteDataListener {
        void onReceiveByteData(byte[] data);
    }
}
