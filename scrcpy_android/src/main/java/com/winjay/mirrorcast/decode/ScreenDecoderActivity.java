package com.winjay.mirrorcast.decode;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.winjay.mirrorcast.BaseActivity;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.SendCommands;
import com.winjay.mirrorcast.app_mirror.AppSocketClient;
import com.winjay.mirrorcast.app_mirror.AppSocketClientManager;
import com.winjay.mirrorcast.app_mirror.AppSocketServer;
import com.winjay.mirrorcast.app_mirror.AppSocketServerManager;
import com.winjay.mirrorcast.databinding.ActivityClientBinding;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

public class ScreenDecoderActivity extends BaseActivity {
    private static final String TAG = ScreenDecoderActivity.class.getSimpleName();

    private String mServerIp;

    private ActivityClientBinding binding;

    private ScreenDecoderSocketServerManager mPhoneMainScreenDecoderSocketServerManager;
    private ScreenDecoderSocketServerManager mPhoneAppScreenDecoderSocketServerManager;

    private float phoneMainScreenWidthRatio;
    private float phoneMainScreenHeightRatio;

    private float phoneAppScreenWidthRatio;
    private float phoneAppScreenHeightRatio;

    private int[] screenSize = new int[2];
    private int maxSize;

    @Override
    protected View viewBinding() {
        binding = ActivityClientBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenSize = DisplayUtil.getScreenSize(ScreenDecoderActivity.this);

        maxSize = screenSize[1];
//        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            maxSize = Math.min(screenSize[0], screenSize[1]);
//        } else {
//            maxSize = Math.max(screenSize[0], screenSize[1]);
//        }

        mServerIp = getIntent().getStringExtra("serverIp");

        binding.phoneMainScreenSv
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                                mPhoneMainScreenDecoderSocketServerManager = startScrcpyServer(
                                        Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT,
                                        maxSize,
                                        "0",
                                        binding.phoneMainScreenSv);
                            }

                            @Override
                            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                            }

                            @Override
                            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                            }
                        });

        binding.phoneMainScreenSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPhoneMainScreenDecoderSocketServerManager != null) {
                    String mockEvent = Constants.SCRCPY_COMMAND_MOTION_EVENT
                            + Constants.COMMAND_SPLIT
                            + event.getAction()
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getX() * phoneMainScreenWidthRatio)
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getY() * phoneMainScreenHeightRatio);
//                    LogUtil.d(TAG, "mirror screen event=" + mockEvent);
                    mPhoneMainScreenDecoderSocketServerManager.sendMessage(mockEvent);
                }
                return true;
            }
        });
        binding.phoneAppScreenSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPhoneAppScreenDecoderSocketServerManager != null) {
                    String mockEvent = Constants.SCRCPY_COMMAND_MOTION_EVENT
                            + Constants.COMMAND_SPLIT
                            + event.getAction()
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getX() * phoneAppScreenWidthRatio)
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getY() * phoneAppScreenHeightRatio);
//                    LogUtil.d(TAG, "mirror screen event=" + mockEvent);
                    mPhoneAppScreenDecoderSocketServerManager.sendMessage(mockEvent);
                }
                return true;
            }
        });

        binding.appMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!Settings.canDrawOverlays(ClientActivity.this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    startActivity(intent);
//                } else {
//                    if (!isAdded) {
//                        createWindow();
//                    }
//                }

                binding.phoneAppScreenSv.setVisibility(View.VISIBLE);

                AppSocketServerManager.getInstance().setAppSocketServerListener(new AppSocketServer.OnAppSocketServerListener() {
                    @Override
                    public void onMessage(String message) {
                        LogUtil.d(TAG, "message=" + message);
                        if (message.startsWith(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID)) {
                            String[] split = message.split(Constants.COMMAND_SPLIT);
                            String displayId = split[1];
                            LogUtil.d(TAG, "displayId=" + displayId);
                            mPhoneAppScreenDecoderSocketServerManager = startScrcpyServer(
                                    Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT,
                                    maxSize,
                                    displayId,
                                    binding.phoneAppScreenSv);
                        }
                    }
                });
                AppSocketServerManager.getInstance().sendMessage(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY + Constants.COMMAND_SPLIT + getRequestedOrientation());


//                AppSocketClientManager.getInstance().createVirtualDisplay(mServerIp, getRequestedOrientation(), new AppSocketClient.OnMessageListener() {
//                    @Override
//                    public void onMessage(String message) {
//                        LogUtil.d(TAG, "message=" + message);
//                        if (message.startsWith(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID)) {
//                            String[] split = message.split(Constants.COMMAND_SPLIT);
//                            String displayId = split[1];
//                            LogUtil.d(TAG, "displayId=" + displayId);
//                            mPhoneAppScreenDecoderSocketServerManager = startScrcpyServer(
//                                    Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT,
//                                    Math.max(screenSize[0], screenSize[1]),
//                                    displayId,
//                                    binding.phoneAppScreenSv);
//                        }
//                    }
//                });
            }
        });


//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        layoutParams = new WindowManager.LayoutParams();
    }

    private ScreenDecoderSocketServerManager startScrcpyServer(int serverPort, int maxSize, String displayId, SurfaceView surfaceView) {
        ScreenDecoderSocketServerManager screenDecoderSocketServerManager = new ScreenDecoderSocketServerManager();
        screenDecoderSocketServerManager.startServer(serverPort, new ScreenDecoderSocketServer.OnSocketServerListener() {
            @Override
            public void onOpen() {
                if (serverPort == Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT) {
                    screenDecoderSocketServerManager.sendMessage(
                            Constants.SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST
                                    + Constants.COMMAND_SPLIT
                                    + displayId);
                }
            }

            @Override
            public void onClose() {
                LogUtil.d(TAG);
            }

            @Override
            public void onMessage(String message) {
                if (message.startsWith(Constants.SCRCPY_REPLY_VIDEO_SIZE)) {
                    String[] split = message.split(Constants.COMMAND_SPLIT);
                    int videoWidth = Integer.parseInt(split[1]);
                    int videoHeight = Integer.parseInt(split[2]);
                    float widthRatio = Float.parseFloat(split[3]);
                    float heightRatio = Float.parseFloat(split[4]);
                    LogUtil.d(TAG, "computed videoWidth=" + videoWidth + " videoHeight=" + videoHeight + ", widthRatio=" + widthRatio + ", heightRatio=" + heightRatio);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                            layoutParams.width = videoWidth;
                            layoutParams.height = videoHeight;
                            surfaceView.setLayoutParams(layoutParams);

                            if (serverPort == Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT) {
                                phoneMainScreenWidthRatio = widthRatio;
                                phoneMainScreenHeightRatio = heightRatio;
                            } else if (serverPort == Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT) {
                                phoneAppScreenWidthRatio = widthRatio;
                                phoneAppScreenHeightRatio = heightRatio;
                            }
                            screenDecoderSocketServerManager.startScreenDecode(surfaceView.getHolder().getSurface(), videoWidth, videoHeight);
                        }
                    });
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "serverPort=" + serverPort + ",maxSize=" + maxSize + ",displayId=" + displayId);
                if (SendCommands.getInstance(ScreenDecoderActivity.this).startMirrorCast(mServerIp, serverPort, 0, maxSize, displayId)) {
                    LogUtil.d(TAG, "scrcpy start success.");
                } else {
                    LogUtil.e(TAG, "scrcpy start failure!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("投屏启动失败!");
                        }
                    });
                }
            }
        }).start();

        return screenDecoderSocketServerManager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPhoneMainScreenDecoderSocketServerManager != null) {
            mPhoneMainScreenDecoderSocketServerManager.stopServer();
        }
        if (mPhoneAppScreenDecoderSocketServerManager != null) {
            mPhoneAppScreenDecoderSocketServerManager.stopServer();
        }
    }

    //////////////////////////////////////////////// 应用流转浮窗实现 ////////////////////////////////////////////////
    /*
    @Override
    public void onBackPressed() {
        if (isAdded) {
            isAdded = false;
            windowManager.removeView(floatView);
        } else {
            super.onBackPressed();
        }
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View floatView;
    private SurfaceView floatSurfaceView;
    private boolean isAdded;

    private void createWindow() {
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        layoutParams.width = 800;
        layoutParams.height = 1600;

        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR; //竖屏
        layoutParams.gravity = Gravity.START | Gravity.TOP;

//        layoutParams.x = dipToPx(this, 34);
        layoutParams.y = DisplayUtil.dip2px(this, 100);

        floatView = LayoutInflater.from(this).inflate(R.layout.window_float_view, null);
        floatSurfaceView = floatView.findViewById(R.id.sv_screen);

//        floatView.setOnTouchListener(new FloatingOnTouchListener());

        floatSurfaceView.setFocusable(true);
        floatSurfaceView.setFocusableInTouchMode(true);
        floatSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocketClientManager_Second != null) {
                    String mockEvent = "" + event.getAction() + "/" + (int) event.getX() + "/" + (int) event.getY();
                    LogUtil.d(TAG, "float view event=" + mockEvent);
                    mSocketClientManager_Second.sendEvent(mockEvent);
                }
                return true;
            }
        });

        windowManager.addView(floatView, layoutParams);
        isAdded = true;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {

        private int
                mTouchStartX,
                mTouchStartY,
                mTouchCurrentX,
                mTouchCurrentY,
                mMoveX,
                mMoveY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchStartX = (int) event.getRawX();
                mTouchStartY = (int) event.getRawY();

                mMoveX = 0;
                mMoveY = 0;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchCurrentX = (int) event.getRawX();
                mTouchCurrentY = (int) event.getRawY();
                mMoveX = mTouchCurrentX - mTouchStartX;
                mMoveY = mTouchCurrentY - mTouchStartY;
                mTouchStartX = mTouchCurrentX;
                mTouchStartY = mTouchCurrentY;
                layoutParams.x += mMoveX;
                layoutParams.y += mMoveY;

                windowManager.updateViewLayout(floatView, layoutParams);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                windowManager.updateViewLayout(floatView, layoutParams);
            }
            return false;
        }
    }
    */
}
