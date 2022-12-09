package com.winjay.mirrorcast.app_mirror;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.car.server.TipsActivity;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;

public class AppSocketClient extends WebSocketClient {
    private static final String TAG = AppSocketClient.class.getSimpleName();

    private int orientation;

    public AppSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtil.d(TAG);

        int isExist = 0;
        File file = new File("data/local/tmp/scrcpy-server.jar");
        if (file.exists()) {
            isExist = 1;
        }
        send(Constants.APP_REPLY_CHECK_SCRCPY_SERVER_JAR + Constants.COMMAND_SPLIT + isExist);
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(TAG, "message=" + message);
        if (message.startsWith(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            orientation = Integer.parseInt(split[1]);
            createVirtualDisplay();
        }
        if (message.equals(Constants.APP_COMMAND_SHOW_TIPS)) {
            Intent intent = new Intent(AppApplication.context, TipsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppApplication.context.startActivity(intent);
        }
    }

    private void createVirtualDisplay() {
        try {
            LogUtil.d(TAG);
            DisplayManager displayManager = (DisplayManager) AppApplication.context.getSystemService(Context.DISPLAY_SERVICE);
            int[] screenSize = DisplayUtil.getScreenSize(AppApplication.context);
            int flags = 139;
//            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
            VirtualDisplay display;
            if (orientation == 0) {
                display = displayManager.createVirtualDisplay("app_mirror",
                        screenSize[1], screenSize[0], screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                        flags);
            } else {
                display = displayManager.createVirtualDisplay("app_mirror",
                        screenSize[0], screenSize[1], screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                        flags);
            }

            int displayId = display.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display ID=" + displayId);

//            PackageManager packageManager = AppApplication.context.getPackageManager();
//            boolean ret = packageManager.hasSystemFeature(PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS);
//            LogUtil.d(TAG, "onCreate: have " + PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS + "   " + ret);
//
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.winjay.mirrorcast","com.winjay.mirrorcast.car.server.CarLauncherActivity"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//            ActivityOptions options = ActivityOptions.makeBasic();
//            options.setLaunchDisplayId(displayId);
//            Bundle optsBundle = options.toBundle();
//
//            AppApplication.context.startActivity(intent, optsBundle);

            send(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID + Constants.COMMAND_SPLIT + displayId);
        } catch (Exception e) {
            LogUtil.e(TAG, "createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtil.d(TAG, "reason=" + reason);
    }

    @Override
    public void onError(Exception ex) {
        LogUtil.e(TAG, ex.getMessage());

//        HandlerManager.getInstance().postOnSubThread(new Runnable() {
//            @Override
//            public void run() {
//                // reconnect
//                try {
//                    reconnectBlocking();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
