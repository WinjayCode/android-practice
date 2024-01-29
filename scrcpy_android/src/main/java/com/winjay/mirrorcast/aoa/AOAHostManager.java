package com.winjay.mirrorcast.aoa;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Looper;
import android.text.TextUtils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.winjay.mirrorcast.protobuf.TestProto;
import com.winjay.mirrorcast.util.LogUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Winjay
 * @date 2023-04-19
 */
public class AOAHostManager {
    private static final String TAG = AOAHostManager.class.getSimpleName();
    private static volatile AOAHostManager INSTANCE;
    private Context context;

    private static final int STORAGE_INTERFACE_COUNT = 1;
    private static final int STORAGE_INTERFACE_ID = 0;
    private static final int STORAGE_INTERFACE_CLASS = 8;
    private static final int STORAGE_INTERFACE_SUBCLASS = 6;
    private static final int STORAGE_INTERFACE_PROTOCOL = 80;
    private static final int VID_IPHONE = 0x05AC;

    private final LinkedBlockingQueue<UsbDevice> pendingPermissionDevices = new LinkedBlockingQueue<>();

    private UsbManager mUsbManager;
    private static final int AOA_GET_PROTOCOL = 51;
    private static final int AOA_SEND_IDENT = 52;
    private static final int AOA_START_ACCESSORY = 53;
    private static final String AOA_MANUFACTURER = "MobileDriveTech";
    private static final String AOA_MODEL_NAME = "SuperLink";
    private static final String AOA_DESCRIPTION = "MobileDriveTech SuperLink";
    private static final String AOA_VERSION = "1.0";
    private static final String AOA_URI = "http://www.MobileDriveTech.com.cn/";
    private static final String AOA_SERIAL_NUMBER = "0123456789";
    private PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION = "com.winjay.mirrorcast.USB_PERMISSION";
    private static final int VID_ACCESSORY = 0x18D1;
    private static final int PID_ACCESSORY_ONLY = 0x2D00;
    private static final int PID_ACCESSORY_AUDIO_ADB_BULK = 0x2D05;
    private UsbInterface usbInterface;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;

    private AOAHostListener mAOAHostListener;

    private AOAHostManager() {
    }

    public static AOAHostManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AOAHostManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AOAHostManager();
                }
            }
        }
        return INSTANCE;
    }

    public void start(Context context) {
        this.context = context.getApplicationContext();
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // PendingIntent.FLAG_INMUTABLE 会导致权限请求失败！！！
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);

        registerUsbReceiver();
        checkDevice();
    }

    private void checkDevice() {
        LogUtil.d(TAG);
        pendingPermissionDevices.clear();
        for (UsbDevice device : mUsbManager.getDeviceList().values()) {
            if (isUsbStorageDevice(device)) {
                continue;
            }

            boolean hasPermission = mUsbManager.hasPermission(device);
            LogUtil.d(TAG, "UsbAccessoryScanner deal with " + device + " has permission " + hasPermission);
            if (hasPermission) {
                if (openAccessory(device)) {
                    // 成功打开usb accessory，清空权限列表，返回设备
                    pendingPermissionDevices.clear();
                    break;
                }
            } else if (device.getVendorId() != VID_IPHONE) {
                // 过滤掉iPhone
                pendingPermissionDevices.add(device);
            }
        }
        requestPendingPermission();
    }

    private void requestPendingPermission() {
        UsbDevice usbDevice = pendingPermissionDevices.poll();
        if (usbDevice != null) {
            LogUtil.d(TAG);
            mUsbManager.requestPermission(usbDevice, mPermissionIntent);
        }
    }

    private boolean isUsbStorageDevice(UsbDevice device) {
        if (device == null) {
            LogUtil.d(TAG, "this device is null");
            return false;
        }

        if (STORAGE_INTERFACE_COUNT == device.getInterfaceCount()) {
            UsbInterface usbInter = device.getInterface(STORAGE_INTERFACE_ID);
            if (STORAGE_INTERFACE_CLASS == usbInter.getInterfaceClass()
                    && STORAGE_INTERFACE_SUBCLASS == usbInter.getInterfaceSubclass()
                    && STORAGE_INTERFACE_PROTOCOL == usbInter.getInterfaceProtocol()) {
                LogUtil.d(TAG, "this device is mass storage");
                return true;
            }
        }
        return false;
    }

    private void registerUsbReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, intentFilter);
    }

    private void unregisterUsbReceiver() {
        if (usbReceiver != null) {
            context.unregisterReceiver(usbReceiver);
            usbReceiver = null;
        }
    }

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "action=" + action);
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case ACTION_USB_PERMISSION:
                    synchronized (this) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (device != null) {
                                if (openAccessory(device)) {
                                    // 成功打开usb accessory，清空权限列表，返回设备
                                    pendingPermissionDevices.clear();
                                    return;
                                }
                            }
                        }
//                        requestPendingPermission();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    // 外部 USB 设备已连接
                    UsbDevice attachDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (attachDevice != null) {
                        if (isUsbStorageDevice(attachDevice)) {
                            return;
                        }

                        LogUtil.d(TAG, "UsbAccessoryScanner ACTION_USB_DEVICE_ATTACHED with " + attachDevice);

                        if (mUsbManager.hasPermission(attachDevice)) {
                            if (openAccessory(attachDevice)) {
                                return;
                            }
                        } else {
                            pendingPermissionDevices.add(attachDevice);
                            requestPendingPermission();
                        }
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    // 外部 USB 设备已断开
                    UsbDevice detachedDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (detachedDevice != null && isAccessory(detachedDevice)) {
                        LogUtil.d(TAG, "device had been detached");
                        stop();

                        if (mAOAHostListener != null) {
                            mAOAHostListener.onDetached();
                        }
                    }
                    break;
            }
        }
    };

    private boolean openAccessory(UsbDevice device) {
        LogUtil.d(TAG);
        usbInterface = device.getInterface(0);
        usbDeviceConnection = mUsbManager.openDevice(device);
        usbDeviceConnection.claimInterface(usbInterface, true);
        findEndpoint();

        if (isAccessory(device)) {
            if (mAOAHostListener != null) {
                mAOAHostListener.connectSucceed(device, usbDeviceConnection);
            }

            receiveAOAMessage();
            return true;
        }

        if (getProtocolVersion()) {
            if (sendIdentityStrings()) {
                if (startAccessoryMode()) {
                    closeAccessory();
                }
            }
        }
        return false;
    }

    private void findEndpoint() {
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    inEndpoint = endpoint;
                } else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    outEndpoint = endpoint;
                }
            }
        }
    }

    private void closeAccessory() {
        if (usbDeviceConnection != null) {
            LogUtil.d(TAG);
            usbDeviceConnection.releaseInterface(usbInterface);
            usbDeviceConnection.close();
            usbDeviceConnection = null;
        }
    }

    private boolean isAccessory(UsbDevice usbDevice) {
        return usbDevice.getVendorId() == VID_ACCESSORY
                && usbDevice.getProductId() >= PID_ACCESSORY_ONLY
                && usbDevice.getProductId() <= PID_ACCESSORY_AUDIO_ADB_BULK;
    }

    private boolean getProtocolVersion() {
        byte[] buffer = new byte[2];
        return controlTransferIn(AOA_GET_PROTOCOL, 0, 0, buffer) >= 0;
    }

    private boolean sendIdentityStrings() {
        if (controlTransferOut(AOA_SEND_IDENT, 0, 0, AOA_MANUFACTURER.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_MANUFACTURER fail");
            return false;
        }
        if (controlTransferOut(AOA_SEND_IDENT, 0, 1, AOA_MODEL_NAME.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_MODEL_NAME fail");
            return false;
        }
        if (controlTransferOut(AOA_SEND_IDENT, 0, 2, AOA_DESCRIPTION.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_DESCRIPTION fail");
            return false;
        }
        if (controlTransferOut(AOA_SEND_IDENT, 0, 3, AOA_VERSION.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_VERSION fail");
            return false;
        }
        if (controlTransferOut(AOA_SEND_IDENT, 0, 4, AOA_URI.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_URI fail");
            return false;
        }
        if (controlTransferOut(AOA_SEND_IDENT, 0, 5, AOA_SERIAL_NUMBER.getBytes()) < 0) {
            LogUtil.w(TAG, "send identity AOA_SERIAL_NUMBER fail");
            return false;
        }
        LogUtil.d(TAG, "send identity string success");
        return true;
    }

    private boolean startAccessoryMode() {
        if (controlTransferOut(AOA_START_ACCESSORY, 0, 0, null) < 0) {
            LogUtil.w(TAG, "start accessory mode fail");
            return false;
        }
        LogUtil.d(TAG, "start accessory mode success");
        return true;
    }

    private int controlTransferOut(int request, int value, int index, byte[] buffer) {
        if (usbDeviceConnection == null) {
            return -1;
        }
        return usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, request,
                value, index, buffer, buffer == null ? 0 : buffer.length, 0);
    }

    private int controlTransferIn(int request, int value, int index, byte[] buffer) {
        if (usbDeviceConnection == null) {
            return -1;
        }
        return usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR, request,
                value, index, buffer, buffer == null ? 0 : buffer.length, 0);
    }

    private void receiveAOAMessage() {
        LogUtil.d(TAG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int length = 0;
                // buffer大小决定了视频是否花屏卡顿
                byte[] buffer = new byte[163840];
                while (length >= 0) {
                    if (usbDeviceConnection != null) {
                        length = usbDeviceConnection.bulkTransfer(inEndpoint, buffer, buffer.length, 0);
//                        LogUtil.d(TAG, "length=" + length);

                        // protobuf
                        /*if (length > 0) {
                            try {
                                byte[] validData = new byte[length];
                                System.arraycopy(buffer, 0, validData, 0, length);
                                TestProto.Test test = TestProto.Test.parseFrom(validData);
                                byte[] bytes = test.getTestBytes().toByteArray();
                                LogUtil.d(TAG, "bytes.length=" + bytes.length);
                                if (bytes.length > 0) {
                                    if (mAOAHostListener != null) {
                                        mAOAHostListener.onReceivedData(bytes, bytes.length);
                                    }
                                }
                            } catch (InvalidProtocolBufferException e) {
                                LogUtil.e(TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }*/


                        if (length > 0) {
                            if (mAOAHostListener != null) {
                                byte[] validData = new byte[length];
                                System.arraycopy(buffer, 0, validData, 0, length);
                                mAOAHostListener.onReceivedData(validData, length);
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void sendAOAMessage(String msg) {
        LogUtil.d(TAG, "msg=" + msg);
        if (usbDeviceConnection != null && usbInterface != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendData(msg.getBytes());
                    }
                }).start();
            } else {
                sendData(msg.getBytes());
            }
        }
    }

    private void sendData(byte[] data) {
        int result = usbDeviceConnection.bulkTransfer(outEndpoint, data, data.length, 0);
//        LogUtil.d(TAG, "host send result=" + result);
    }

    public void stop() {
        unregisterUsbReceiver();
        closeAccessory();
    }

    public interface AOAHostListener {
        void connectSucceed(UsbDevice device, UsbDeviceConnection usbDeviceConnection);

        void onReceivedData(byte[] data, int length);

        void onDetached();
    }

    public void setAOAHostListener(AOAHostListener aOAHostListener) {
        mAOAHostListener = aOAHostListener;
    }
}
