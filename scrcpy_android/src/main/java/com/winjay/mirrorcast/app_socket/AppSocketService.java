package com.winjay.mirrorcast.app_socket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.util.LogUtil;

import java.net.InetSocketAddress;

/**
 * @author Winjay
 * @date 2023-05-25
 */
public class AppSocketService extends Service implements AppSocketServer.OnAppSocketServerErrorListener {
    private static final String TAG = AppSocketService.class.getSimpleName();

    private AppSocketServer mAppSocketServer;
    private int port = Constants.APP_SOCKET_PORT;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AppSocketService.MyBinder();
    }

    public class MyBinder extends Binder {

        public AppSocketService getService() {
            return AppSocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel("1", TAG, NotificationManager.IMPORTANCE_HIGH));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        stopForeground(true);
    }

    public void startServer() {
        if (mAppSocketServer == null) {
            LogUtil.d(TAG);
            mAppSocketServer = new AppSocketServer(new InetSocketAddress(port));
            mAppSocketServer.setOnAppSocketServerErrorListener(this);
            mAppSocketServer.start();
        }
    }

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
            } finally {
                mAppSocketServer = null;
            }
        }
    }

    public void sendMessage(String message) {
        if (mAppSocketServer != null) {
            LogUtil.d(TAG, "message=" + message);
            mAppSocketServer.sendMessage(message);
        }
    }

    @Override
    public void onError() {
        LogUtil.w(TAG, "start server error, change port and restart server!");
        ++port;
        stopServer();
        startServer();
    }
}
