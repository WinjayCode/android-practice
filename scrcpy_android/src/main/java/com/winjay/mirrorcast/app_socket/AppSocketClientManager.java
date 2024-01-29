package com.winjay.mirrorcast.app_socket;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.util.LogUtil;

import java.net.URI;

/**
 * @author Winjay
 * @date 2022-11-29
 */
public class AppSocketClientManager implements AppSocketClient.AppSocketClientCloseListener {
    private static final String TAG = AppSocketClientManager.class.getSimpleName();
    private static volatile AppSocketClientManager mInstance;

    private AppSocketClient mAppSocketClient;

    private String serverIp;
    private int port = Constants.APP_SOCKET_PORT;

    private AppSocketClientManager() {
    }

    public static AppSocketClientManager getInstance() {
        if (mInstance == null) {
            synchronized (AppSocketClientManager.class) {
                if (mInstance == null) {
                    mInstance = new AppSocketClientManager();
                }
            }
        }
        return mInstance;
    }

    public void connect(String serverIp) {
        if (mAppSocketClient == null) {
            LogUtil.d(TAG, "serverIp=" + serverIp + ", port=" + port);
            this.serverIp = serverIp;
            try {
                URI uri = new URI("ws://" + serverIp + ":" + port);
                mAppSocketClient = new AppSocketClient(uri);
                mAppSocketClient.setAppSocketClientCloseListener(this);
                mAppSocketClient.connect();
            } catch (Exception e) {
                LogUtil.e(TAG, "error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (mAppSocketClient != null) {
            LogUtil.d(TAG, "message=" + message);
            try {
                mAppSocketClient.send(message);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
    }

    public void setAppSocketClientListener(AppSocketClient.AppSocketClientListener listener) {
        if (mAppSocketClient != null) {
            mAppSocketClient.setAppSocketClientListener(listener);
        }
    }

    public void close() {
        if (mAppSocketClient != null) {
            LogUtil.d(TAG);
            mAppSocketClient.close();
            mAppSocketClient = null;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // TODO
        // 两种情况：1.对方app还没有打开服务端口监听（code=-1, reason=failed to connect to /192.168.49.1 (port 13346) from /:: (port 48086): connect failed: ECONNREFUSED (Connection refused), remote=false）
        // 2.对方app服务端口被占用（）
        if (code == -1 && !remote) {
            LogUtil.w(TAG, "connect error, change port and reconnect!");
            close();
            ++port;
            connect(serverIp);
        }
    }
}
