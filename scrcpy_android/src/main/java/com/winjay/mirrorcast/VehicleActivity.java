package com.winjay.mirrorcast;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.aoa.VehicleAOAActivity;
import com.winjay.mirrorcast.app_socket.AppSocketManager;
import com.winjay.mirrorcast.app_socket.AppSocketServerManager;
import com.winjay.mirrorcast.car.client.ShowCarLauncherActivity;
import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityVehicleBinding;
import com.winjay.mirrorcast.server.ScreenService;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.wifidirect.WIFIDirectActivity;

/**
 * @author Winjay
 * @date 2023-03-31
 */
public class VehicleActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = VehicleActivity.class.getSimpleName();

    private ActivityVehicleBinding binding;

    private MediaProjectionManager mediaProjectionManager;
    private static final int PROJECTION_REQUEST_CODE = 1;

    private boolean isRecording = false;

    @Override
    protected View viewBinding() {
        binding = ActivityVehicleBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        initView();

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        AppSocketServerManager.getInstance().startServer();
    }

    private void initView() {
        binding.btnWifiP2p.setOnClickListener(this);
        binding.btnStartRecord.setOnClickListener(this);
        binding.connectMirrorCastServer.setOnClickListener(this);
        binding.btnStartReceive.setOnClickListener(this);
        binding.btnCarHome.setOnClickListener(this);
        binding.btnAoa.setOnClickListener(this);
        binding.btnAoaCarLauncher.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // wifi p2p
        if (v == binding.btnWifiP2p) {
            startActivity(WIFIDirectActivity.class);

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
                dialogToast("请输入需要投屏的设备IP地址！");
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
                dialogToast("请先连接投屏服务！");
                return;
            }

            Intent intent = new Intent(this, TestScreenDecoderActivity.class);
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
        if (v == binding.btnCarHome) {
            if (!mMirrorCastServerConnected) {
                dialogToast("请先连接投屏服务！");
                return;
            }

            Intent intent = new Intent(this, ShowCarLauncherActivity.class);
            String serverIp = "";
            if (!TextUtils.isEmpty(AppApplication.destDeviceIp)) {
                serverIp = AppApplication.destDeviceIp;
            } else if (!TextUtils.isEmpty(binding.ipEd.getText().toString())) {
                serverIp = binding.ipEd.getText().toString();
            }

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

        // aoa test
        if (v == binding.btnAoa) {
            Intent intent = new Intent(this, VehicleAOAActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        }

        // aoa car launcher
        if (v == binding.btnAoaCarLauncher) {
            Intent intent = new Intent(this, VehicleAOAActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        }
    }

    private boolean mMirrorCastServerConnected = false;

    private void connectMirrorCastServer(String serverIp) {
        if (mMirrorCastServerConnected) {
            dialogToast("投屏服务已连接！");
            return;
        }

        AppSocketManager.getInstance().setAppSocketListener(new AppSocketManager.AppSocketListener() {
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

                                if (ADBCommands.getInstance(VehicleActivity.this).sendServerJar(serverIp)) {
                                    mMirrorCastServerConnected = true;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            dialogToast("投屏服务连接成功！");
                                        }
                                    });
                                } else {
                                    mMirrorCastServerConnected = false;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            dialogToast("投屏服务连接失败！");
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
                                dialogToast("投屏服务连接成功！");
                            }
                        });
                    }
                }
            }
        });
        AppSocketManager.getInstance().sendMessage(Constants.APP_COMMAND_CHECK_SCRCPY_SERVER_JAR);
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
        AppSocketServerManager.getInstance().stopServer();
//        System.exit(0);
    }
}
