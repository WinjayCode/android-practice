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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
}
