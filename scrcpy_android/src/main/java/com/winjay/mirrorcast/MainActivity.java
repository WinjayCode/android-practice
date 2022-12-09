package com.winjay.mirrorcast;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRouter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.app_mirror.AppSocketServer;
import com.winjay.mirrorcast.app_mirror.AppSocketServerManager;
import com.winjay.mirrorcast.car.client.CarShowActivity;
import com.winjay.mirrorcast.databinding.ActivityMainScrcpyBinding;
import com.winjay.mirrorcast.decode.ScreenDecoderActivity;
import com.winjay.mirrorcast.server.ScreenService;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.wifidirect.ClientThread;
import com.winjay.mirrorcast.wifidirect.ServerThread;
import com.winjay.mirrorcast.wifidirect.WIFIDirectActivity;

/**
 * @author F2848777
 * @date 2022-11-09
 */
public class MainActivity extends com.winjay.mirrorcast.BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainScrcpyBinding binding;

    private MediaProjectionManager mediaProjectionManager;
    private static final int PROJECTION_REQUEST_CODE = 1;

    private boolean isRecording = false;

    private ServerThread serverThread;

    @Override
    protected View viewBinding() {
        binding = ActivityMainScrcpyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected String[] permissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET};
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        initView();

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        serverThread = new ServerThread();
        serverThread.start();

        AppSocketServerManager.getInstance().startServer();
    }

    private void initView() {
        binding.btnWifiP2p.setOnClickListener(this);
        binding.btnStartRecord.setOnClickListener(this);
        binding.connectMirrorCastServer.setOnClickListener(this);
        binding.btnStartReceive.setOnClickListener(this);
        binding.btnCarLauncher.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // wifi p2p
        if (v == binding.btnWifiP2p) {
            Intent intent = new Intent(this, WIFIDirectActivity.class);
            startActivity(intent);

//            Intent intent = new Intent(this, CarLauncherActivity.class);
//            startActivity(intent);
        }
        if (v == binding.btnStartRecord) {
//            if (!isRecording) {
//                isRecording = true;
//                startProjection();
//                binding.btnStartRecord.setText("停止投屏");
//            } else {
//                isRecording = false;
//                stopProjection();
//                binding.btnStartRecord.setText("开始投屏");
//            }
        }
        // 连接投屏服务
        if (v == binding.connectMirrorCastServer) {
            if (TextUtils.isEmpty(AppApplication.destDeviceIp) && TextUtils.isEmpty(binding.ipEd.getText().toString())) {
                toast("请输入需要投屏的设备IP地址！");
                return;
            }

            if (!TextUtils.isEmpty(AppApplication.destDeviceIp)) {
                connectMirrorCastServer(AppApplication.destDeviceIp);
                return;
            }

            if (!TextUtils.isEmpty(binding.ipEd.getText().toString())) {
                connectMirrorCastServer(binding.ipEd.getText().toString());
                return;
            }
        }
        // 接收投屏
        if (v == binding.btnStartReceive) {
            if (!mMirrorCastServerConnected) {
                toast("请先连接投屏服务！");
                return;
            }

            Intent intent = new Intent(this, ScreenDecoderActivity.class);
            String serverIp = "";
            if (!TextUtils.isEmpty(AppApplication.destDeviceIp)) {
                serverIp = AppApplication.destDeviceIp;
            } else if (!TextUtils.isEmpty(binding.ipEd.getText().toString())) {
                serverIp = binding.ipEd.getText().toString();
            }

            intent.putExtra("serverIp", serverIp);
            startActivity(intent);
        }
        // car launcher
        if (v == binding.btnCarLauncher) {
            if (!mMirrorCastServerConnected) {
                toast("请先连接投屏服务！");
                return;
            }

            Intent intent = new Intent(this, CarShowActivity.class);
            String serverIp = "";
            if (!TextUtils.isEmpty(AppApplication.destDeviceIp)) {
                serverIp = AppApplication.destDeviceIp;
            } else if (!TextUtils.isEmpty(binding.ipEd.getText().toString())) {
                serverIp = binding.ipEd.getText().toString();
            }

//            new ClientThread(serverIp, true).start();

            intent.putExtra("serverIp", serverIp);
            startActivity(intent);


//            createVirtualDisplay();

//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.winjay.mirrorcast", "com.winjay.mirrorcast.car.server.CarLauncherActivity"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            ActivityOptions options = ActivityOptions.makeBasic();
//            options.setLaunchDisplayId(2);
//            Bundle optsBundle = options.toBundle();
//            startActivity(intent, optsBundle);
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

            VirtualDisplay virtualDisplay = displayManager.createVirtualDisplay("app_mirror",
                    screenSize[0], screenSize[1], screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                    flags);
            int displayId = virtualDisplay.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display ID=" + displayId);

            for (Display display : displayManager.getDisplays()) {
                LogUtil.d(TAG, "dispaly: " + display.getName() + ", id " + display.getDisplayId() + " :" + display.toString());
//                if (display.getDisplayId() != 0) {
//                    SecondeDid = display.getDisplayId();
//                }
            }

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.winjay.mirrorcast", "com.winjay.mirrorcast.car.server.CarLauncherActivity"));

            ActivityOptions activityOptions = ActivityOptions.makeBasic();
            MediaRouter mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
            MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
            if (route != null) {
                Display presentationDisplay = route.getPresentationDisplay();
                LogUtil.d(TAG, "displayId=" + presentationDisplay.getDisplayId());
                Bundle bundle = activityOptions.setLaunchDisplayId(presentationDisplay.getDisplayId()).toBundle();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, bundle);
            }


//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//            ActivityOptions options = ActivityOptions.makeBasic();
//            options.setLaunchDisplayId(9);
//            Bundle optsBundle = options.toBundle();

//            AppApplication.context.startActivity(intent, optsBundle);
        } catch (Exception e) {
            LogUtil.e(TAG, "createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean mMirrorCastServerConnected = false;

    private void connectMirrorCastServer(String serverIp) {
        if (mMirrorCastServerConnected) {
            toast("投屏服务已连接！");
            return;
        }

        AppSocketServerManager.getInstance().setAppSocketServerListener(new AppSocketServer.OnAppSocketServerListener() {
            @Override
            public void onMessage(String message) {
                LogUtil.d(TAG, "message=" + message);
                if (message.startsWith(Constants.APP_REPLY_CHECK_SCRCPY_SERVER_JAR)) {
                    String[] split = message.split(Constants.COMMAND_SPLIT);
                    if (split[1].equals("0")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "serverIp=" + serverIp);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoadingDialog("正在连接投屏服务");
                                    }
                                });

                                if (SendCommands.getInstance(MainActivity.this).sendServerJar(serverIp) == 0) {
                                    mMirrorCastServerConnected = true;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            toast("投屏服务连接成功！");
                                        }
                                    });
                                } else {
                                    mMirrorCastServerConnected = false;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            toast("投屏服务连接失败！");
                                        }
                                    });
                                }
                            }
                        }).start();
                    } else if (split[1].equals("1")) {
                        mMirrorCastServerConnected = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                toast("投屏服务连接成功！");
                            }
                        });
                    }
                }
            }
        });
        new ClientThread(serverIp, true).start();
    }

    // 请求开始录屏
    private void startProjection() {
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQUEST_CODE);
    }

    private void stopProjection() {
        Intent service = new Intent(this, ScreenService.class);
        stopService(service);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PROJECTION_REQUEST_CODE) {
            Intent service = new Intent(this, ScreenService.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            startForegroundService(service);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        serverThread.close();
        AppSocketServerManager.getInstance().stopServer();
//        System.exit(0);
    }
}
