package com.winjay.practice.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

/**
 * U盘插拔状态监听（好像已无法收到？）
 * <p>
 * sd卡被插入，且已经挂载	Intent.ACTION_MEDIA_MOUNTED
 * sd卡存在，但还没有挂载	Intent.ACTION_MEDIA_UNMOUNTED
 * sd卡被移除	Intent.ACTION_MEDIA_REMOVED
 * sd卡作为 USB大容量存储被共享，挂载被解除	Intent.ACTION_MEDIA_SHARED
 * sd卡已经从sd卡插槽拔出，但是挂载点还没解除	Intent.ACTION_MEDIA_BAD_REMOVAL
 * 开始扫描	Intent.ACTION_MEDIA_SCANNER_STARTED
 * 扫描完成	Intent.ACTION_MEDIA_SCANNER_FINISHED
 *
 * @author Winjay
 * @date 2020/12/4
 */
public class MediaMountedReceiver extends BroadcastReceiver {
    private static final String TAG = MediaMountedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 这里可以拿到插入的USB设备对象
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice != null) {
            LogUtil.d(TAG, "device name=" + usbDevice.getDeviceName());
        }
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                // 外部媒体资源挂载
                case Intent.ACTION_MEDIA_MOUNTED:
                    String pathMounted = intent.getData().getPath();
                    LogUtil.d(TAG, "ACTION_MEDIA_MOUNTED: path=" + pathMounted);

                    Toast.makeText(context, "media mounted usb=" + pathMounted, Toast.LENGTH_SHORT).show();

                    // test code
//                    boolean result = FileUtil.copyFile(Environment.getExternalStorageDirectory() + "Log.txt", pathMounted + "/Log.txt");
//                    LogUtil.d(TAG, "result=" + result);
//                    Toast.makeText(context, "result=" + result, Toast.LENGTH_SHORT).show();
                    break;
                // 外部媒体资源未挂载
                case Intent.ACTION_MEDIA_UNMOUNTED:
                    LogUtil.i(TAG, "ACTION_MEDIA_UNMOUNTED");

                    String pathUnmounted = intent.getData().getPath();
                    Toast.makeText(context, "media unmounted usb=" + pathUnmounted, Toast.LENGTH_SHORT).show();
                    break;
                //
                case Intent.ACTION_MEDIA_SCANNER_STARTED:
                    LogUtil.i(TAG, "ACTION_MEDIA_SCANNER_STARTED");
                    break;
                //
                case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                    LogUtil.i(TAG, "ACTION_MEDIA_SCANNER_FINISHED");
                    break;
                // USB设备插入（静态广播无法收到？）
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    LogUtil.i(TAG, "ACTION_USB_DEVICE_ATTACHED");
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    LogUtil.d(TAG, "usb attach=" + device.toString());
//                    Toast.makeText(context, device.toString(), Toast.LENGTH_SHORT).show();
                    break;
                // USB设备拔出（静态广播无法收到？）
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    LogUtil.i(TAG, "ACTION_USB_DEVICE_DETACHED");
                    UsbDevice device2 = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    LogUtil.d(TAG, "usb detach=" + device2.toString());
//                    Toast.makeText(context, device2.toString(), Toast.LENGTH_SHORT).show();
                    break;
                // 开机广播
                case Intent.ACTION_BOOT_COMPLETED:
                    LogUtil.i(TAG, "ACTION_BOOT_COMPLETED");
                    break;
                default:
                    break;
            }
        }
    }
}
