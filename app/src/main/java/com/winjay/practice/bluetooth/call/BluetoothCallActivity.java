package com.winjay.practice.bluetooth.call;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 蓝牙电话（连接车机打电话）
 *
 * @author Winjay
 * @date 2021-05-19
 */
public class BluetoothCallActivity extends BaseActivity {
    private static final String TAG = BluetoothCallActivity.class.getSimpleName();

    EditText number_et;

    @Override
    protected int getLayoutId() {
        return R.layout.bluetooth_call_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number_et = findViewById(R.id.number_et);
        number_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }

    void call() {
        String number = number_et.getText().toString();
        if (TextUtils.isEmpty(number)) {
            toast("请输入电话号码！");
            return;
        }
        getConnectStatus(new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                LogUtil.d(TAG);
                List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();
                if (!connectedDevices.isEmpty()) {
                    for (BluetoothDevice connectedDevice : connectedDevices) {
                        dial(proxy, connectedDevice, number);
                        break;
                    }
                } else {
                    LogUtil.e(TAG, "connected devices is empty! profile=" + profile);
                    toast("蓝牙未连接！");
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                LogUtil.e(TAG, "profile=" + profile);
                toast("蓝牙未连接！");
            }
        });
    }

    private void getConnectStatus(BluetoothProfile.ServiceListener serviceListener) {
        final int bluetooth_Profile_HEADSET_CLIENT;
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (!adapter.isEnabled()) {
                LogUtil.e(TAG, "bluetooth is not open!");
                toast("蓝牙未打开！");
                return;
            }
            bluetooth_Profile_HEADSET_CLIENT = (int) BluetoothProfile.class.getField("HEADSET_CLIENT").get(null);
            LogUtil.d(TAG, "profile=" + bluetooth_Profile_HEADSET_CLIENT);
            // 获取蓝牙电话的连接状态
            int isConnected = adapter.getProfileConnectionState(bluetooth_Profile_HEADSET_CLIENT);
            if (isConnected == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.e(TAG, "bluetooth disconnected!");
                toast("蓝牙未连接！");
                return;
            }
            adapter.getProfileProxy(this, serviceListener, bluetooth_Profile_HEADSET_CLIENT);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            toast(e.getMessage());
        }
    }

    private void dial(BluetoothProfile proxy, BluetoothDevice bluetoothDevice, String number) {
        if (proxy == null || bluetoothDevice == null || (TextUtils.isEmpty(number))) {
            return;
        }
        try {
            Class BluetoothHeadsetClient = Class.forName("android.bluetooth.BluetoothHeadsetClient");
            Method method = BluetoothHeadsetClient.getMethod("dial", BluetoothDevice.class, String.class);
            method.invoke(proxy, bluetoothDevice, number);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            toast(e.getMessage());
        }
    }
}
