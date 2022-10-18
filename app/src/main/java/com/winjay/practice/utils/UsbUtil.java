package com.winjay.practice.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * USB相关工具类
 * <p>
 * OTG通信的封装类库：compile 'com.github.mjdev:libaums:+'
 *
 * @author Winjay
 * @date 2021-03-27
 */
public class UsbUtil {
    private static final String TAG = UsbUtil.class.getSimpleName();
    public static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    public static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0";

    public static boolean hasUsbDevices(Context mContext) {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = usbManager.getDeviceList();
        if (null != devices && devices.size() > 0) {
            //mLastUsbState = true;
            LogUtil.d(TAG, "hasUsbDevices devices.size() = " + devices.size());
            try {
                for (Map.Entry<String, UsbDevice> entry : devices.entrySet()) {
                    UsbDevice temp = entry.getValue();
                    if (null != temp) {
                        LogUtil.d(TAG, "hasUsbDevices temp = " + temp.toString());
                        //Log.i(TAG, "hasUsbDevices = " + temp.toString());
                        LogUtil.d(TAG, "hasUsbDevices getDeviceName = " + temp.getDeviceName());
                        int i = temp.getConfigurationCount();
                        for (int ii = 0; ii < i; ii++) {
                            UsbConfiguration usbConfiguration = temp.getConfiguration(ii);
                            if (null != usbConfiguration) {
                                int j = usbConfiguration.getInterfaceCount();
                                for (int jj = 0; jj < j; jj++) {
                                    UsbInterface usbInterface = usbConfiguration.getInterface(jj);
                                    if (null != usbInterface && usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
                                        return true;
                                    }
                                }
                            }

                        }
                    }
                    /*if(temp.getConfiguration(0).getInterface(0).getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE){

                    }*/
                }
                return false;
            } catch (Exception e) {
                LogUtil.e(TAG, "hasUsbDevices Exception " + e.toString());
                return false;
            }

        }
        //mLastUsbState = false;
        return false;
        //return true;
    }

    public HashMap<String, UsbDevice> readDeviceList(Context mContext) {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDevices = usbManager.getDeviceList();
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        if (null != mDevices && mDevices.size() != 0) {
            Iterator<UsbDevice> iterator = mDevices.values().iterator();
            while (iterator.hasNext()) {
                UsbDevice usb = iterator.next();
                if (!usbManager.hasPermission(usb)) {
                    LogUtil.i(TAG, "has not permission");
                    usbManager.requestPermission(usb, mPendingIntent);
                } else {
                    LogUtil.i(TAG, "has permission");
                }
            }
        }
        return mDevices;
    }

    /**
     * OTG通讯
     *
     * @param mContext
     * @param usbDevice
     */
    private void OTGCommunicate(Context mContext, UsbDevice usbDevice) {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        UsbInterface usbInterface = usbDevice.getInterface(1);
        UsbEndpoint inEndpoint = usbInterface.getEndpoint(0);
        UsbEndpoint outEndpoint = usbInterface.getEndpoint(1);
        UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
        connection.claimInterface(usbInterface, true);

        String sendStringMsg = "0x88";
        byte[] sendBytes = ByteUtil.hexToByteArray(sendStringMsg);
        int out = connection.bulkTransfer(outEndpoint, sendBytes, sendBytes.length, 5000);
        LogUtil.d(TAG, "发送：" + out + " # " + sendStringMsg + " # " + sendBytes);

        byte[] receiveMsgBytes = new byte[32];
        int in = connection.bulkTransfer(inEndpoint, receiveMsgBytes, receiveMsgBytes.length, 10000);
        String receiveMsgString = ByteUtil.bytesToHex(receiveMsgBytes);
        LogUtil.d(TAG, "应答：" + in + " # " + receiveMsgString + " # " + receiveMsgBytes.length);
    }

    /**
     * 获取当前连接的U盘设备路径（old method）
     *
     * @param mContext
     * @return
     */
    public static String getUSBPath(Context mContext) {
        String targetpath = "";
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                String path = (String) getPath.invoke(storageVolumeElement);
                LogUtil.d(TAG, "userLabel=" + userLabel);
                LogUtil.d(TAG, "path=" + path);
                if (!INTERNAL_STORAGE_PATH.equals(path)) {
                    targetpath = path.replace("storage", "mnt/media_rw");
                    return targetpath;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return targetpath;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void getUSBPath(Context context, USBDeviceMountedListener listener) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        storageManager.registerStorageVolumeCallback(Executors.newSingleThreadExecutor(), new StorageManager.StorageVolumeCallback() {
            @Override
            public void onStateChanged(@NonNull StorageVolume volume) {
                super.onStateChanged(volume);
                if (volume.isRemovable() && Environment.MEDIA_MOUNTED.equals(volume.getState())) {
                    if (listener != null) {
                        listener.getUSBPath(volume.getDirectory().toString());
                    }
                }
            }
        });
    }

    public interface USBDeviceMountedListener {
        void getUSBPath(String path);
    }
}
