package com.winjay.mirrorcast.car.client;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.mirrorcast.BaseActivity;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.SendCommands;
import com.winjay.mirrorcast.app_mirror.AppSocketServer;
import com.winjay.mirrorcast.app_mirror.AppSocketServerManager;
import com.winjay.mirrorcast.car.server.CarLauncherActivity;
import com.winjay.mirrorcast.databinding.ActivityCarShowBinding;
import com.winjay.mirrorcast.decode.ScreenDecoderSocketServer;
import com.winjay.mirrorcast.decode.ScreenDecoderSocketServerManager;
import com.winjay.mirrorcast.util.ActivityListUtil;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

/**
 * @author F2848777
 * @date 2022-11-25
 */
public class CarShowActivity extends BaseActivity {
    private static final String TAG = CarShowActivity.class.getSimpleName();
    private ActivityCarShowBinding binding;

    private ScreenDecoderSocketServerManager mCarLauncherScreenDecoderSocketServerManager;
    private ScreenDecoderSocketServerManager mPhoneMainScreenDecoderSocketServerManager;
    private ScreenDecoderSocketServerManager mPhoneAppScreenDecoderSocketServerManager;

    private String mServerIp;

    private float carLauncherWidthRatio;
    private float carLauncherHeightRatio;

    private float phoneMainScreenWidthRatio;
    private float phoneMainScreenHeightRatio;

    private float phoneAppScreenWidthRatio;
    private float phoneAppScreenHeightRatio;

    private int[] screenSize = new int[2];

    private String phoneAppPackageName;
    private String phoneAppEnterClassName;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected View viewBinding() {
        binding = ActivityCarShowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenSize = DisplayUtil.getScreenSize(this);

        mServerIp = getIntent().getStringExtra("serverIp");

        binding.carLauncherSv
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                                LogUtil.d(TAG);
                                connectAppMirrorSocket();
                            }

                            @Override
                            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                            }

                            @Override
                            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                                LogUtil.d(TAG);
                            }
                        });

        binding.carLauncherSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCarLauncherScreenDecoderSocketServerManager != null) {
                    String mockEvent = Constants.SCRCPY_COMMAND_MOTION_EVENT
                            + Constants.COMMAND_SPLIT
                            + event.getAction()
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getX() * carLauncherWidthRatio)
                            + Constants.COMMAND_SPLIT
                            + (int) (event.getY() * carLauncherHeightRatio);
//                    LogUtil.d(TAG, "mirror screen event=" + mockEvent);
                    mCarLauncherScreenDecoderSocketServerManager.sendMessage(mockEvent);
                }
                return true;
            }
        });
    }

    private void connectAppMirrorSocket() {
        if (TextUtils.isEmpty(mServerIp)) {
            toast("手机连接异常！");
            finish();
            return;
        }

        AppSocketServerManager.getInstance().setAppSocketServerListener(new AppSocketServer.OnAppSocketServerListener() {
            @Override
            public void onMessage(String message) {
                LogUtil.d(TAG, "message=" + message);
                if (message.startsWith(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID)) {
                    String[] split = message.split(Constants.COMMAND_SPLIT);
                    String displayId = split[1];
                    LogUtil.d(TAG, "displayId=" + displayId);
                    mCarLauncherScreenDecoderSocketServerManager = startScrcpyServer(
                            Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT,
                            Math.max(screenSize[0], screenSize[1]),
                            displayId,
                            binding.carLauncherSv);
                }
                // car launcher: 镜像投屏
                else if (message.equals(Constants.APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            carLauncherPhoneMainScreenMirrorCast();
                        }
                    });
                }
                // car launcher: 应用投屏
                else if (message.startsWith(Constants.APP_COMMAND_PHONE_APP_MIRROR_CAST)) {
                    String[] split = message.split(Constants.COMMAND_SPLIT);
                    if (split.length < 4) {
                        LogUtil.e(TAG, "miss parameters.");
                        return;
                    }
                    if (TextUtils.isEmpty(split[1])) {
                        LogUtil.e(TAG, "miss app package name.");
                        return;
                    }
                    if (TextUtils.isEmpty(split[2])) {
                        LogUtil.e(TAG, "miss app enter class.");
                        return;
                    }
                    if (TextUtils.isEmpty(split[3])) {
                        LogUtil.e(TAG, "miss display id.");
                        return;
                    }
                    phoneAppPackageName = split[1];
                    phoneAppEnterClassName = split[2];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            carLauncherPhoneAppMirrorCast(split[3]);
                        }
                    });
                }
                // return car system
                else if (message.equals(Constants.APP_COMMAND_RETURN_CAR_SYSTEM)) {
                    // TODO 退出应用？
                    for (int i = ActivityListUtil.getActivityCount() - 1; i >= 0; i--) {
                        if (ActivityListUtil.getActivityByIndex(i) != null) {
                            ActivityListUtil.getActivityByIndex(i).finish();
                        }
                    }

//                    finish();
                }
            }
        });
        AppSocketServerManager.getInstance().sendMessage(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY + Constants.COMMAND_SPLIT + getRequestedOrientation());
    }

    private void carLauncherPhoneMainScreenMirrorCast() {
        if (binding.phoneMainScreenLayout.mirrorWindowRl.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.phoneMainScreenLayout.mirrorWindowRl.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.phoneMainScreenLayout.mirrorWindowRl.getLayoutParams();
        binding.phoneMainScreenLayout.moveView.setOnTouchListener(new MirrorWindowBarOnTouchListener(binding.phoneMainScreenLayout.mirrorWindowRl, layoutParams));
        binding.phoneMainScreenLayout.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.phoneMainScreenLayout.mirrorWindowRl.setVisibility(View.GONE);
                if (mPhoneMainScreenDecoderSocketServerManager != null) {
                    mPhoneMainScreenDecoderSocketServerManager.stopServer();
                }
            }
        });

        binding.phoneMainScreenLayout.mirrorWindowSv.setOnTouchListener(new View.OnTouchListener() {
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

        mPhoneMainScreenDecoderSocketServerManager = startScrcpyServer(
                Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT,
                Math.min(screenSize[0], screenSize[1]) - 200,
                "0",
                binding.phoneMainScreenLayout.mirrorWindowSv);
    }

    private void carLauncherPhoneAppMirrorCast(String displayId) {
        if (binding.phoneAppScreenLayout.mirrorWindowRl.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.phoneAppScreenLayout.mirrorWindowRl.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.phoneAppScreenLayout.mirrorWindowRl.getLayoutParams();
        binding.phoneAppScreenLayout.moveView.setOnTouchListener(new MirrorWindowBarOnTouchListener(binding.phoneAppScreenLayout.mirrorWindowRl, layoutParams));
        binding.phoneAppScreenLayout.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.phoneAppScreenLayout.mirrorWindowRl.setVisibility(View.GONE);
                if (mPhoneAppScreenDecoderSocketServerManager != null) {
                    mPhoneAppScreenDecoderSocketServerManager.stopServer();
                }
            }
        });

        binding.phoneAppScreenLayout.mirrorWindowSv.setOnTouchListener(new View.OnTouchListener() {
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

        mPhoneAppScreenDecoderSocketServerManager = startScrcpyServer(
                Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT,
                Math.min(screenSize[0], screenSize[1]) - 200,
                displayId,
                binding.phoneAppScreenLayout.mirrorWindowSv);
    }

    private ScreenDecoderSocketServerManager startScrcpyServer(int serverPort, int maxSize, String displayId, SurfaceView surfaceView) {
        ScreenDecoderSocketServerManager screenDecoderSocketServerManager = new ScreenDecoderSocketServerManager();
        screenDecoderSocketServerManager.startServer(serverPort, new ScreenDecoderSocketServer.OnSocketServerListener() {
            @Override
            public void onOpen() {
                // car launcher
                if (serverPort == Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT) {
                    screenDecoderSocketServerManager.sendMessage(
                            Constants.SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST
                                    + Constants.COMMAND_SPLIT
                                    + getPackageName()
                                    + Constants.COMMAND_SPLIT
                                    + CarLauncherActivity.class.getName()
                                    + Constants.COMMAND_SPLIT
                                    + displayId);

                    AppSocketServerManager.getInstance().sendMessage(Constants.APP_COMMAND_SHOW_TIPS);
                }
                // phone app screen
                if (serverPort == Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT) {
                    screenDecoderSocketServerManager.sendMessage(
                            Constants.SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST
                                    + Constants.COMMAND_SPLIT
                                    + phoneAppPackageName
                                    + Constants.COMMAND_SPLIT
                                    + phoneAppEnterClassName
                                    + Constants.COMMAND_SPLIT
                                    + displayId);
                    phoneAppPackageName = "";
                    phoneAppEnterClassName = "";
                }
            }

            @Override
            public void onClose() {
                LogUtil.d(TAG);
                if (serverPort == Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT) {
                    LogUtil.d(TAG, "car launcher mirror stopped.");
                    finish();
                }
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
                            // car launcher
                            if (serverPort == Constants.CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                                layoutParams.width = videoWidth;
                                layoutParams.height = videoHeight;
                                surfaceView.setLayoutParams(layoutParams);

                                carLauncherWidthRatio = widthRatio;
                                carLauncherHeightRatio = heightRatio;
                            }
                            // phone main screen
                            if (serverPort == Constants.PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT) {
                                RelativeLayout.LayoutParams rootRLLayoutParams = (RelativeLayout.LayoutParams) binding.phoneMainScreenLayout.mirrorWindowRl.getLayoutParams();
                                rootRLLayoutParams.width = videoWidth;
                                rootRLLayoutParams.height = videoHeight + DisplayUtil.dp2px(CarShowActivity.this, 30);
                                binding.phoneMainScreenLayout.mirrorWindowRl.setLayoutParams(rootRLLayoutParams);

                                RelativeLayout.LayoutParams controlBarRlLayoutParams = (RelativeLayout.LayoutParams) binding.phoneMainScreenLayout.controlBarRl.getLayoutParams();
                                controlBarRlLayoutParams.width = videoWidth;
                                binding.phoneMainScreenLayout.controlBarRl.setLayoutParams(controlBarRlLayoutParams);

                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                                layoutParams.width = videoWidth;
                                layoutParams.height = videoHeight;
                                surfaceView.setLayoutParams(layoutParams);

                                phoneMainScreenWidthRatio = widthRatio;
                                phoneMainScreenHeightRatio = heightRatio;
                            }
                            // phone app screen
                            if (serverPort == Constants.PHONE_APP_MIRROR_CAST_SERVER_PORT) {
                                RelativeLayout.LayoutParams rootRLLayoutParams = (RelativeLayout.LayoutParams) binding.phoneAppScreenLayout.mirrorWindowRl.getLayoutParams();
                                rootRLLayoutParams.width = videoWidth;
                                rootRLLayoutParams.height = videoHeight + DisplayUtil.dp2px(CarShowActivity.this, 30);
                                binding.phoneAppScreenLayout.mirrorWindowRl.setLayoutParams(rootRLLayoutParams);

                                RelativeLayout.LayoutParams controlBarRlLayoutParams = (RelativeLayout.LayoutParams) binding.phoneAppScreenLayout.controlBarRl.getLayoutParams();
                                controlBarRlLayoutParams.width = videoWidth;
                                binding.phoneAppScreenLayout.controlBarRl.setLayoutParams(controlBarRlLayoutParams);

                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                                layoutParams.width = videoWidth;
                                layoutParams.height = videoHeight;
                                surfaceView.setLayoutParams(layoutParams);

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
                if (SendCommands.getInstance(CarShowActivity.this).startMirrorCast(mServerIp, serverPort, 0, maxSize, displayId)) {
                    LogUtil.d(TAG, "scrcpy server start success.");
                } else {
                    LogUtil.e(TAG, "scrcpy server start failure!");
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

    private class MirrorWindowBarOnTouchListener implements View.OnTouchListener {

        private int
                mTouchStartX,
                mTouchStartY,
                mTouchCurrentX,
                mTouchCurrentY,
                mMoveX,
                mMoveY;

        private RelativeLayout mirrorWindowRl;
        private RelativeLayout.LayoutParams mirrorWindowLayoutParams;

        public MirrorWindowBarOnTouchListener(RelativeLayout mirrorWindowRl, RelativeLayout.LayoutParams mirrorWindowLayoutParams) {
            this.mirrorWindowRl = mirrorWindowRl;
            this.mirrorWindowLayoutParams = mirrorWindowLayoutParams;
        }

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

                mirrorWindowLayoutParams.leftMargin += mMoveX;
                mirrorWindowLayoutParams.topMargin += mMoveY;


                if (mirrorWindowLayoutParams.leftMargin < 0) {
                    mirrorWindowLayoutParams.leftMargin = 0;
                }
                int rightBorder = binding.getRoot().getMeasuredWidth() - mirrorWindowLayoutParams.width;
                if (mirrorWindowLayoutParams.leftMargin > rightBorder) {
                    mirrorWindowLayoutParams.leftMargin = rightBorder;
                }
                if (mirrorWindowLayoutParams.topMargin < 0) {
                    mirrorWindowLayoutParams.topMargin = 0;
                }
                int bottomBorder = binding.getRoot().getMeasuredHeight() - mirrorWindowLayoutParams.height;
                if (mirrorWindowLayoutParams.topMargin > bottomBorder) {
                    mirrorWindowLayoutParams.topMargin = bottomBorder;
                }

                mirrorWindowRl.setLayoutParams(mirrorWindowLayoutParams);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mirrorWindowRl.setLayoutParams(mirrorWindowLayoutParams);
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCarLauncherScreenDecoderSocketServerManager != null) {
            mCarLauncherScreenDecoderSocketServerManager.stopServer();
        }
        if (mPhoneMainScreenDecoderSocketServerManager != null) {
            mPhoneMainScreenDecoderSocketServerManager.stopServer();
        }
        if (mPhoneAppScreenDecoderSocketServerManager != null) {
            mPhoneAppScreenDecoderSocketServerManager.stopServer();
        }
//        binding = null;
//        if (isAdded) {
//            isAdded = false;
//            windowManager.removeView(floatView);
//        }
    }


    //////////////////////////////////////////////// 镜像悬浮窗实现 ////////////////////////////////////////////////
    /*@Override
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

    private void mirrorMainScreenOnWindow(String serverPort, String displayId) {
        if (!Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
            startActivity(intent);
        } else {
            if (!isAdded) {
                createWindow();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG);
                if (SendCommands.getInstance(getContext()).startMirrorCast(mServerIp, serverPort, 0, DisplayUtil.getScreenSize(getContext())[1], displayId)) {
                    LogUtil.d(TAG, "scrcpy start success.");
                    floatSurfaceView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSocketClientManager = initSocketManager(Integer.parseInt(serverPort), floatSurfaceView.getHolder().getSurface(), displayId);
                        }
                    }, 2000);
                } else {
                    LogUtil.e(TAG, "scrcpy start failure!");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("镜像启动失败!");
                        }
                    });
                }
            }
        }).start();
    }

    private void createWindow() {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();

        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        layoutParams.width = 800;
        layoutParams.height = 1000;

        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR; //竖屏
        layoutParams.gravity = Gravity.START | Gravity.TOP;

//        layoutParams.x = dipToPx(this, 34);
        layoutParams.y = DisplayUtil.dip2px(getContext(), 100);

        floatView = LayoutInflater.from(getContext()).inflate(R.layout.window_float_view, null);
        floatSurfaceView = floatView.findViewById(R.id.sv_screen);

        floatView.setOnTouchListener(new FloatingOnTouchListener());

        floatSurfaceView.setFocusable(true);
        floatSurfaceView.setFocusableInTouchMode(true);
        floatSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (mSocketClientManager_Second != null) {
//                    String mockEvent = "" + event.getAction() + "/" + (int) event.getX() + "/" + (int) event.getY();
//                    LogUtil.d(TAG, "float view event=" + mockEvent);
//                    mSocketClientManager_Second.sendEvent(mockEvent);
//                }
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
    }*/
}
