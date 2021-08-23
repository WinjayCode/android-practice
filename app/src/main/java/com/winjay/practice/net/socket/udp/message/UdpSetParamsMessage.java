package com.winjay.practice.net.socket.udp.message;

import android.util.Log;


import com.winjay.practice.utils.ByteUtil;

import java.util.Arrays;

/**
 * 设置子设备参数
 */
public class UdpSetParamsMessage extends UdpReceiveMessage {
    private static final String TAG = UdpSetParamsMessage.class.getSimpleName();
    /**
     * 消息Id
     */
    public static final int MESSAGE_ID = 0x8103;

    public String getVIN() {
        Log.d(TAG, "messageBody=" + ByteUtil.bytesToHex(mMessageBody));
        int paramsCount = mMessageBody[0] & 0xFF;
        Log.d(TAG, "paramsCount=" + paramsCount);
        int allParamsValueLength = 0;
        for (int i = 0; i < paramsCount; i++) {
            byte[] paramId = new byte[4];
            int scrPos = (paramId.length + 1) * i + allParamsValueLength + 1;
            System.arraycopy(mMessageBody, scrPos, paramId, 0, paramId.length);
            Log.d(TAG, "paramId=" + ByteUtil.bytesToHex(paramId));
            int paramValuePos = (paramId.length + 1) * (i + 1) + allParamsValueLength;
            int paramValueLength = mMessageBody[paramValuePos] & 0xFF;
            allParamsValueLength += paramValueLength;
            if (Arrays.equals(ByteUtil.intToByte4(UdpQueryParamsMessage.VIN), paramId)) {
                byte[] paramValue = new byte[paramValueLength];
                System.arraycopy(mMessageBody, paramValuePos + 1, paramValue, 0, paramValueLength);
                return new String(paramValue);
            }
        }
        return "";
    }
}
