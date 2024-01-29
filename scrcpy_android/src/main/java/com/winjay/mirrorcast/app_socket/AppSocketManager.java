package com.winjay.mirrorcast.app_socket;

import android.content.Intent;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.car.server.TipsActivity;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

import java.io.File;

/**
 * @author Winjay
 * @date 2023-05-05
 */
public class AppSocketManager {
    private static final String TAG = AppSocketManager.class.getSimpleName();
    private static volatile AppSocketManager instance;

    private boolean isWiFiDirectGroupOwner;
    private AppSocketListener appSocketListener;

    public static AppSocketManager getInstance() {
        if (instance == null) {
            synchronized (AppSocketManager.class) {
                if (instance == null) {
                    instance = new AppSocketManager();
                }
            }
        }
        return instance;
    }

    private AppSocketManager() {
    }

    public void setIsWiFiDirectGroupOwner(boolean isWiFiDirectGroupOwner) {
        this.isWiFiDirectGroupOwner = isWiFiDirectGroupOwner;
    }

    public void sendMessage(String message) {
        LogUtil.d(TAG, "isWiFiDirectGroupOwner=" + isWiFiDirectGroupOwner + ", message=" + message);
        if (isWiFiDirectGroupOwner) {
            AppSocketServerManager.getInstance().sendMessage(message);
        } else {
            AppSocketClientManager.getInstance().sendMessage(message);
        }
    }

    public void setAppSocketListener(AppSocketListener listener) {
        this.appSocketListener = listener;
        if (isWiFiDirectGroupOwner) {
            AppSocketServerManager.getInstance().setAppSocketServerListener(new AppSocketServer.OnAppSocketServerListener() {
                @Override
                public void onMessage(String message) {
                    if (appSocketListener != null) {
                        appSocketListener.onMessage(message);
                    }
                }
            });
        } else {
            AppSocketClientManager.getInstance().setAppSocketClientListener(new AppSocketClient.AppSocketClientListener() {
                @Override
                public void onMessage(String message) {
                    if (appSocketListener != null) {
                        appSocketListener.onMessage(message);
                    }
                }
            });
        }
    }

    public interface AppSocketListener {
        void onMessage(String message);
    }

    public void handleMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (message.startsWith(Constants.APP_COMMAND_CHECK_SCRCPY_SERVER_JAR)) {
            int isExist = 0;
            File file = new File("data/local/tmp/scrcpy-server.jar");
            if (file.exists()) {
                isExist = 1;
            }
            sendMessage(Constants.APP_REPLY_CHECK_SCRCPY_SERVER_JAR + Constants.COMMAND_SPLIT + isExist);
        }
        if (message.startsWith(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            int orientation = Integer.parseInt(split[1]);
            int virtualDisplayId = DisplayUtil.createVirtualDisplay(orientation);
            if (virtualDisplayId != -1) {
                sendMessage(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID + Constants.COMMAND_SPLIT + virtualDisplayId);
            }
        }
        if (message.equals(Constants.APP_COMMAND_SHOW_TIPS)) {
            Intent intent = new Intent(AppApplication.context, TipsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppApplication.context.startActivity(intent);
        }
    }
}
