package com.winjay.practice.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.UsbUtil;

/**
 * USB设备插拔广播监听器
 *
 * @author Winjay
 * @date 2021-03-27
 */
public class USBDeviceReceiver extends BroadcastReceiver {
    private static final String TAG = USBDeviceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.d(TAG, "USBReceiver:action=" + action);
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    LogUtil.d(TAG, "usb attach=" + device.toString());
                    if (device.getConfiguration(0).getInterface(0).getInterfaceClass() != UsbConstants.USB_CLASS_MASS_STORAGE) {
                        Toast.makeText(context, "不支持该USB设备", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UsbUtil.getUSBPath(context, new UsbUtil.USBDeviceMountedListener() {
                        @Override
                        public void getUSBPath(String path) {
                            LogUtil.d(TAG, "usb path=" + path);
                        }
                    });
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:

                    break;
                case VolumeInfo.ACTION_VOLUME_STATE_CHANGED:
                    int state = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE, -1);
                    if (state == VolumeInfo.STATE_MOUNTED || state == VolumeInfo.STATE_MOUNTED_READ_ONLY) {
                        LogUtil.d(TAG, "VolumeInfo mounted");
                    } else if (state == VolumeInfo.STATE_UNMOUNTED || state == VolumeInfo.STATE_BAD_REMOVAL) {
                        LogUtil.d(TAG, "VolumeInfo unmounted");
                    }
                    break;
            }
        }
    }
}
