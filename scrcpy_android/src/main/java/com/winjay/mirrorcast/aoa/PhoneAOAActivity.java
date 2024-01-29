package com.winjay.mirrorcast.aoa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityPhoneAoaBinding;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

import java.io.File;

/**
 * @author Winjay
 * @date 2023-04-10
 */
public class PhoneAOAActivity extends BaseActivity implements View.OnClickListener,
        AOAAccessoryManager.AOAAccessoryListener {
    private static final String TAG = PhoneAOAActivity.class.getSimpleName();

    private ActivityPhoneAoaBinding binding;

    private static final int MESSAGE_STR = 1;

    private PhoneAOAService.MyBinder myBinder;

    private boolean isAccessoryOpened = false;
    private boolean isServiceConnected = false;

    @Override
    protected View viewBinding() {
        binding = ActivityPhoneAoaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        initView();

        Intent intent = new Intent(this, PhoneAOAService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        AOAAccessoryManager.getInstance().setAOAAccessoryListener(this);
        AOAAccessoryManager.getInstance().start(this);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG);
            myBinder = (PhoneAOAService.MyBinder) service;

            isServiceConnected = true;
            if (isAccessoryOpened) {
                // 通知车机端当前手机端设备data/local/tmp/目录下是否存在scrcpy-server.jar文件
                notifyScrcpyServerJarIsExist();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };

    private void initView() {
        binding.aoaSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.aoaSendBtn) {
            if (TextUtils.isEmpty(binding.aoaEd.getText().toString())) {
                dialogToast("内容不能为空！");
                return;
            }

            AOAAccessoryManager.getInstance().sendAOAMessage(binding.aoaEd.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);

        unbindService(connection);

        AOAAccessoryManager.getInstance().stop();
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STR:
                    binding.aoaMsgTv.setText(binding.aoaMsgTv.getText() + "\n" + (String) msg.obj);
                    break;
            }
        }
    };

    @Override
    public void connectSucceed() {
        LogUtil.d(TAG);
        AppApplication.connectType = Constants.CONNECT_TYPE_AOA;

        isAccessoryOpened = true;

        if (isServiceConnected) {
            // 通知车机端当前手机端设备data/local/tmp/目录下是否存在scrcpy-server.jar文件
            notifyScrcpyServerJarIsExist();
        }
    }

    @Override
    public void onReceivedData(byte[] data, int length) {
        String message = new String(data, 0, length);
        LogUtil.d(TAG, "message=" + message);

        Message m = Message.obtain(mHandler, MESSAGE_STR);
        m.obj = message;
        mHandler.sendMessage(m);

        if (message.startsWith(Constants.APP_COMMAND_AOA_SERVER_PORT)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            int port = Integer.parseInt(split[1]);
            if (myBinder != null) {
                myBinder.getService().setServerPort(port);
            }
            return;
        }

        if (message.startsWith(Constants.APP_COMMAND_CREATE_VIRTUAL_DISPLAY)) {
            String[] split = message.split(Constants.COMMAND_SPLIT);
            int orientation = Integer.parseInt(split[1]);
            int virtualDisplayId = DisplayUtil.createVirtualDisplay(orientation);
            if (virtualDisplayId != -1) {
                if (myBinder != null) {
                    myBinder.getService().setDisplayId(String.valueOf(virtualDisplayId));
                }
                AOAAccessoryManager.getInstance().sendAOAMessage(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID + Constants.COMMAND_SPLIT + virtualDisplayId);
            }
            return;
        }

        // 转发AOA Host端的数据给scrcpy-server.jar
        if (myBinder != null) {
            myBinder.getService().sendMessage(message);
        }
    }

    @Override
    public void onDetached() {
        LogUtil.d(TAG);

        finish();
        System.exit(0);
    }

    private void notifyScrcpyServerJarIsExist() {
        int isExist = 0;
        File file = new File("data/local/tmp/scrcpy-server.jar");
        if (file.exists()) {
            isExist = 1;
        }
        AOAAccessoryManager.getInstance().sendAOAMessage(Constants.APP_REPLY_CHECK_SCRCPY_SERVER_JAR + Constants.COMMAND_SPLIT + isExist);
    }
}
