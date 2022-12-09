package com.winjay.mirrorcast.app_mirror;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.util.LogUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author F2848777
 * @date 2022-11-29
 */
public class AppSocketClientManager {
    private static final String TAG = AppSocketClientManager.class.getSimpleName();
    private static AppSocketClientManager mInstance;

    private AppSocketClient mAppSocketClient;

    private AppSocketClientManager() {
    }

    public static AppSocketClientManager getInstance() {
        synchronized (AppSocketClientManager.class) {
            if (mInstance == null) {
                mInstance = new AppSocketClientManager();
            }
        }
        return mInstance;
    }

    public void connect(String serverIp) {
        if (mAppSocketClient == null) {
            try {
                URI uri = new URI("ws://" + serverIp + ":" + Constants.APP_SOCKET_PORT);
                mAppSocketClient = new AppSocketClient(uri);
                mAppSocketClient.connect();
            } catch (URISyntaxException e) {
                LogUtil.e(TAG, "error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (mAppSocketClient != null) {
            mAppSocketClient.send(message);
        }
    }

    public void close() {
        LogUtil.d(TAG);
        if (mAppSocketClient != null) {
            mAppSocketClient.close();
        }
    }
}
