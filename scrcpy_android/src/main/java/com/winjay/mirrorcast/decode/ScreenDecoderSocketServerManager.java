package com.winjay.mirrorcast.decode;

import android.view.Surface;

import com.winjay.mirrorcast.util.LogUtil;

import java.net.InetSocketAddress;

/**
 * @author F2848777
 * @date 2022-12-01
 */
public class ScreenDecoderSocketServerManager implements ScreenDecoderSocketServer.OnReceiveByteDataListener {
    private static final String TAG = ScreenDecoderSocketServerManager.class.getSimpleName();

    private ScreenDecoderSocketServer mScreenDecoderSocketServer;

    private ScreenDecoder mScreenDecoder;

    public void startServer(int port, ScreenDecoderSocketServer.OnSocketServerListener listener) {
        if (mScreenDecoderSocketServer == null) {
            LogUtil.d(TAG);
            mScreenDecoderSocketServer = new ScreenDecoderSocketServer(new InetSocketAddress(port), listener, this);
            mScreenDecoderSocketServer.start();
        }
    }

    public void stopServer() {
        if (mScreenDecoderSocketServer != null) {
            LogUtil.d(TAG);
            try {
                mScreenDecoderSocketServer.stop();
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "error=" + e.getMessage());
                e.printStackTrace();
            }
        }
        if (mScreenDecoder != null) {
            mScreenDecoder.stopDecode();
        }
    }

    public void sendMessage(String message) {
        if (mScreenDecoderSocketServer != null) {
            LogUtil.d(TAG, "message=" + message);
            mScreenDecoderSocketServer.sendMessage(message);
        }
    }

    public void startScreenDecode(Surface surface, int videoWidth, int videoHeight) {
        mScreenDecoder = new ScreenDecoder();
        mScreenDecoder.startDecode(surface, videoWidth, videoHeight);
    }

    @Override
    public void onReceiveByteData(byte[] data) {
        if (mScreenDecoder != null) {
            mScreenDecoder.decodeData(data);
        }
    }
}
