package com.winjay.mirrorcast.aoa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.car.server.CarLauncherActivity;
import com.winjay.mirrorcast.car.server.TipsActivity;
import com.winjay.mirrorcast.util.LogUtil;

import java.net.InetSocketAddress;

/**
 * @author Winjay
 * @date 2023-05-15
 */
public class PhoneAOAService extends Service implements PhoneAOASocketServer.OnWebSocketServerListener {
    private static final String TAG = PhoneAOAService.class.getSimpleName();

    private PhoneAOASocketServer phoneAOASocketServer;
    private String displayId;
    private int serverPort;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public PhoneAOAService getService() {
            return PhoneAOAService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel("2", TAG, NotificationManager.IMPORTANCE_HIGH));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2");
        startForeground(2, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);

        stopForeground(true);

        if (phoneAOASocketServer != null) {
            try {
                phoneAOASocketServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setServerPort(int port) {
        serverPort = port;
        phoneAOASocketServer = new PhoneAOASocketServer(new InetSocketAddress(port));
        phoneAOASocketServer.setOnWebSocketServerListener(this);
        phoneAOASocketServer.start();
    }

    public void sendMessage(String message) {
        if (phoneAOASocketServer != null) {
            phoneAOASocketServer.sendMessage(message);
        }
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    @Override
    public void onOpen() {
        LogUtil.d(TAG);
        if (serverPort == Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT) {
//            showTips();
        }
        if (serverPort == Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT && !TextUtils.isEmpty(displayId)) {
            // 启动手机端car launcher到虚拟屏上
            sendMessage(
                    Constants.SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST
                            + Constants.COMMAND_SPLIT
                            + getPackageName()
                            + Constants.COMMAND_SPLIT
                            + CarLauncherActivity.class.getName()
                            + Constants.COMMAND_SPLIT
                            + displayId);

            // 手机端显示Tips页面
            showTips();
        }
    }

    private void showTips() {
        Intent intent = new Intent(AppApplication.context, TipsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppApplication.context.startActivity(intent);
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        // 转发scrcpy-server.jar的消息数据给AOA Host端
        AOAAccessoryManager.getInstance().sendAOAMessage(message);
    }

    @Override
    public void onReceiveByteData(byte[] data) {
        LogUtil.d(TAG, "encode data.length=" + data.length);
        // 转发scrcpy-server.jar的录屏数据给AOA Host端
        AOAAccessoryManager.getInstance().sendAOAByte(data);
    }

    @Override
    public void onClose(String reason) {
        LogUtil.d(TAG, "reason=" + reason);
    }

    @Override
    public void onError(String errorMessage) {
        LogUtil.e(TAG, "errorMessage=" + errorMessage);
    }
}
