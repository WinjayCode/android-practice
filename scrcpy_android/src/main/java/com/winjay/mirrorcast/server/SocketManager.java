package com.winjay.mirrorcast.server;

import android.media.projection.MediaProjection;

import com.winjay.mirrorcast.util.LogUtil;

import java.net.InetSocketAddress;

public class SocketManager {
    private static final String TAG = SocketManager.class.getSimpleName();

    private static final int SOCKET_PORT = 12345;
    private final ScreenSocketServer mScreenSocketServer;
    private ScreenEncoder mScreenEncoder;

    public SocketManager() {
        mScreenSocketServer = new ScreenSocketServer(new InetSocketAddress(SOCKET_PORT));
    }

    public void start(MediaProjection mediaProjection) {
        LogUtil.d(TAG, "start()");
        // 启动webSocketServer  此时当前设备就可以作为一个服务器了
        mScreenSocketServer.start();
        mScreenEncoder = new ScreenEncoder(this, mediaProjection);
        // 开始编码
        mScreenEncoder.startEncode();
    }

    // 关闭服务端
    public void close() {
        LogUtil.d(TAG, "close()");
        try {
            mScreenSocketServer.stop();
            mScreenSocketServer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mScreenEncoder != null) {
            mScreenEncoder.stopEncode();
        }
    }

    // 发送编码后的数据
    public void sendData(byte[] bytes) {
//    Log.d(TAG, "sendData():" + bytes.length);
        mScreenSocketServer.sendData(bytes);
    }
}
