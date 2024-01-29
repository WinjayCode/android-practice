package com.winjay.mirrorcast.aoa;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.mirrorcast.ADBCommands;
import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityVehicleAoaBinding;
import com.winjay.mirrorcast.decode.ScreenDecoder;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Winjay
 * @date 2023-04-10
 */
public class VehicleAOAActivity extends BaseActivity implements View.OnClickListener, AOAHostManager.AOAHostListener {
    private static final String TAG = VehicleAOAActivity.class.getSimpleName();

    private ActivityVehicleAoaBinding binding;

    private static final int MESSAGE_STR = 1;
    private static final int MESSAGE_START_SCREEN_DECODE = 2;

    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterface;

    private int[] screenSize = new int[2];
    private ScreenDecoder mScreenDecoder;
    private float phoneMainScreenWidthRatio;
    private float phoneMainScreenHeightRatio;

    private boolean startMirrorCastSucceed = false;
    private boolean isStartMirrorCast = false;

    private int adbCommandRetryCount = 0;

    private int type;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected View viewBinding() {
        binding = ActivityVehicleAoaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG);

        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {

        }
        if (type == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        initView();

        AOAHostManager.getInstance().setAOAHostListener(this);
        AOAHostManager.getInstance().start(this);
    }

    private void initView() {
        screenSize = DisplayUtil.getScreenSize(this);

        binding.aoaSendBtn.setOnClickListener(this);

        binding.phoneMainScreenTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String mockEvent = Constants.SCRCPY_COMMAND_MOTION_EVENT
                        + Constants.COMMAND_SPLIT
                        + event.getAction()
                        + Constants.COMMAND_SPLIT
                        + (int) (event.getX() * phoneMainScreenWidthRatio)
                        + Constants.COMMAND_SPLIT
                        + (int) (event.getY() * phoneMainScreenHeightRatio);
                LogUtil.d(TAG, "mirror screen event=" + mockEvent);
                // 发送给手机AOAAccessory，手机AOAAccessory再发送给手机scrcpy的websocketclient端
                AOAHostManager.getInstance().sendAOAMessage(mockEvent);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.aoaSendBtn) {
            if (TextUtils.isEmpty(binding.aoaEd.getText().toString())) {
                dialogToast("内容不能为空！");
                return;
            }

            AOAHostManager.getInstance().sendAOAMessage(binding.aoaEd.getText().toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);

        if (mScreenDecoder != null) {
            mScreenDecoder.stopDecode();
        }

        AOAHostManager.getInstance().stop();
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STR:
                    binding.aoaMsgTv.setText(binding.aoaMsgTv.getText() + "\n" + (String) msg.obj);
                    break;
                case MESSAGE_START_SCREEN_DECODE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.phoneMainScreenTv.getLayoutParams();
                    layoutParams.width = msg.arg1;
                    layoutParams.height = msg.arg2;
                    binding.phoneMainScreenTv.setLayoutParams(layoutParams);

                    binding.phoneMainScreenTv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                        @Override
                        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                            if (binding.phoneMainScreenTv.getVisibility() == View.VISIBLE) {
                                mScreenDecoder = new ScreenDecoder();
                                mScreenDecoder.startDecode(new Surface(surface), msg.arg1, msg.arg2);
                                isStartMirrorCast = true;
                            }
                        }

                        @Override
                        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

                        }

                        @Override
                        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                            return false;
                        }

                        @Override
                        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

                        }
                    });
                    /*binding.phoneMainScreenSv.getHolder().addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(@NonNull SurfaceHolder holder) {
                        }

                        @Override
                        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                            if (binding.phoneMainScreenSv.getVisibility() == View.VISIBLE) {
                                mScreenDecoder = new ScreenDecoder();
                                mScreenDecoder.startDecode(binding.phoneMainScreenSv.getHolder().getSurface(), msg.arg1, msg.arg2);
                                isStartMirrorCast = true;
                            }
                        }

                        @Override
                        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                        }
                    });*/
                    binding.phoneMainScreenTv.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void connectSucceed(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
        LogUtil.d(TAG);
        AppApplication.connectType = Constants.CONNECT_TYPE_AOA;

        this.usbDevice = usbDevice;
        this.usbDeviceConnection = usbDeviceConnection;
    }

    @Override
    public void onReceivedData(byte[] data, int length) {
        String message = new String(data, 0, length);
//        LogUtil.d(TAG, "message=" + message);

        // 接收手机端是否存在scrcpy-server.jar文件的结果
        if (message.startsWith(Constants.APP_REPLY_CHECK_SCRCPY_SERVER_JAR)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            if (split[1].equals("0")) {
                // 车机推送scrcpy-server.jar包到手机端
                boolean result = sendServerJar();
                if (!result) {
                    return;
                }

                if (type == 1) {
                    // 使用adb启动手机端scrcpy服务开始投屏
                    startMirrorCast(Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT, screenSize[1], "0");
                }
                if (type == 2) {
                    // car launcher
                    createCarLauncherVirtualDisplay();
                }
            } else if (split[1].equals("1")) {
                if (type == 1) {
                    // 使用adb启动手机端scrcpy服务开始投屏
                    startMirrorCast(Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT, screenSize[1], "0");
                }
                if (type == 2) {
                    // car launcher
                    createCarLauncherVirtualDisplay();
                }
            }
            return;
        }

        // 开启手机端car launcher投屏
        if (message.startsWith(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            String displayId = split[1];
            LogUtil.d(TAG, "displayId=" + displayId);
            startMirrorCast(Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT, Math.max(screenSize[0], screenSize[1]), displayId);
            return;
        }

        // 解析投屏视频宽高信息
        if (message.startsWith(Constants.SCRCPY_REPLY_VIDEO_SIZE)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            int videoWidth = Integer.parseInt(split[1]);
            int videoHeight = Integer.parseInt(split[2]);
            float widthRatio = Float.parseFloat(split[3]);
            float heightRatio = Float.parseFloat(split[4]);
            LogUtil.d(TAG, "computed videoWidth=" + videoWidth + " videoHeight=" + videoHeight + ", widthRatio=" + widthRatio + ", heightRatio=" + heightRatio);

            phoneMainScreenWidthRatio = widthRatio;
            phoneMainScreenHeightRatio = heightRatio;

            Message m = Message.obtain(mHandler, MESSAGE_START_SCREEN_DECODE);
            m.arg1 = videoWidth;
            m.arg2 = videoHeight;
            mHandler.sendMessage(m);
            return;
        }

        // return car system
        if (message.startsWith(Constants.APP_COMMAND_RETURN_CAR_SYSTEM)) {
            goHome();
            return;
        }

        LogUtil.d(TAG, "startMirrorCastSucceed=" + startMirrorCastSucceed + ", isStartMirrorCast=" + isStartMirrorCast);
        if (startMirrorCastSucceed && isStartMirrorCast) {
            if (mScreenDecoder != null) {
//                LogUtil.d(TAG, "decoding...");
                // 解析投屏视频数据
                mScreenDecoder.decodeData(data);
            }
            return;
        }

        Message m = Message.obtain(mHandler, MESSAGE_STR);
        m.obj = message;
        mHandler.sendMessage(m);
    }

    @Override
    public void onDetached() {
        finish();
    }

    private boolean sendServerJar() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            Message m1 = Message.obtain(mHandler, MESSAGE_STR);
            m1.obj = "开始推送scrcpy-server.jar";
            mHandler.sendMessage(m1);

            usbInterface = findAdbInterface(usbDevice);
            if (usbDeviceConnection.claimInterface(usbInterface, false)) {
                boolean sendResult = ADBCommands.getInstance(VehicleAOAActivity.this).sendServerJar(usbDeviceConnection, usbInterface);
                LogUtil.d(TAG, "sendResult=" + sendResult);

                Message m2 = Message.obtain(mHandler, MESSAGE_STR);
                m2.obj = sendResult ? "scrcpy-server.jar推送完成！" : "scrcpy-server.jar推送失败！";
                mHandler.sendMessage(m2);

                return sendResult;
            }
            return false;
        });
        boolean result = false;
        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        LogUtil.d(TAG, "result=" + result);
        return result;
    }

    // searches for an adb interface on the given USB device
    private UsbInterface findAdbInterface(UsbDevice device) {
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == 255 && intf.getInterfaceSubclass() == 66 &&
                    intf.getInterfaceProtocol() == 1) {
                return intf;
            }
        }
        return null;
    }

    private void startMirrorCast(int serverPort, int maxSize, String displayId) {
        LogUtil.d(TAG, "serverPort=" + serverPort + ", maxSize=" + maxSize + ", displayId=" + displayId);

        AOAHostManager.getInstance().sendAOAMessage(Constants.APP_COMMAND_AOA_SERVER_PORT + Constants.COMMAND_SPLIT + serverPort);

        adbCommand(serverPort, maxSize, displayId);
    }

    private void adbCommand(int serverPort, int maxSize, String displayId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (usbInterface == null) {
                    usbInterface = findAdbInterface(usbDevice);
                }
                if (ADBCommands.getInstance(VehicleAOAActivity.this).startMirrorCast(usbDeviceConnection, usbInterface, "localhost",
                        serverPort, 0, maxSize, displayId)) {
                    LogUtil.d(TAG, "scrcpy start success.");
                    startMirrorCastSucceed = true;

                    Message m = Message.obtain(mHandler, MESSAGE_STR);
                    m.obj = "投屏启动成功!";
                    mHandler.sendMessage(m);
                } else {
                    LogUtil.e(TAG, "scrcpy start failure!");
                    startMirrorCastSucceed = false;

                    Message m = Message.obtain(mHandler, MESSAGE_STR);
                    m.obj = "投屏启动失败!";
                    mHandler.sendMessage(m);

//                    if (adbCommandRetryCount == 1) {
//                        // 目前先做一次重试
//                        return;
//                    }
//
//                    LogUtil.d(TAG, "resend adb command!");
//                    adbCommandRetryCount++;
//                    adbCommand(serverPort, maxSize, displayId);
                }
            }
        }).start();
    }

    private void createCarLauncherVirtualDisplay() {
        AOAHostManager.getInstance().sendAOAMessage(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY + Constants.COMMAND_SPLIT + getRequestedOrientation());
    }
}
