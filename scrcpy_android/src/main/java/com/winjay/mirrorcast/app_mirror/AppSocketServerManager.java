package com.winjay.mirrorcast.app_mirror;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.util.LogUtil;

import java.net.InetSocketAddress;

/**
 * @author F2848777
 * @date 2022-11-29
 */
public class AppSocketServerManager {
    private static final String TAG = AppSocketServerManager.class.getSimpleName();
    private static AppSocketServerManager mInstance;

    private AppSocketServer mAppSocketServer;

    private AppSocketServerManager() {
    }

    public static AppSocketServerManager getInstance() {
        synchronized (AppSocketServerManager.class) {
            if (mInstance == null) {
                mInstance = new AppSocketServerManager();
            }
        }
        return mInstance;
    }

    public void startServer() {
        if (mAppSocketServer == null) {
            LogUtil.d(TAG);
            mAppSocketServer = new AppSocketServer(new InetSocketAddress(Constants.APP_SOCKET_PORT));
            mAppSocketServer.start();
        }
    }

//    public void restartServer() {
//        LogUtil.d(TAG);
//        mAppSocketServer = new AppSocketServer(new InetSocketAddress(Constants.APP_SOCKET_PORT));
//        mAppSocketServer.start();
//    }

    public void setAppSocketServerListener(AppSocketServer.OnAppSocketServerListener listener) {
        if (mAppSocketServer != null) {
            mAppSocketServer.setAppSocketServerListener(listener);
        }
    }

    public void stopServer() {
        if (mAppSocketServer != null) {
            LogUtil.d(TAG);
            try {
                mAppSocketServer.stop();
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (mAppSocketServer != null) {
            LogUtil.d(TAG, "message=" + message);
            mAppSocketServer.sendMessage(message);
        }
    }
}
