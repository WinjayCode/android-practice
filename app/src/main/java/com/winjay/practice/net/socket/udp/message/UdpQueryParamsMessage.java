package com.winjay.practice.net.socket.udp.message;


import com.winjay.practice.utils.ByteUtil;

/**
 * 查询参数
 */
public class UdpQueryParamsMessage extends UdpSendMessage {
    /**
     * 车工号？
     */
    public static final int CHE_GONG_NUM = 0xFF0B;
    /**
     * VIN码
     */
    public static final int VIN = 0xF10D;
    /**
     * 消息Id
     */
    private static final int MESSAGE_ID = 0x0103;
    /**
     * 参数总数（1个Byte）
     */
    private byte mParamsSum;
    /**
     * 参数 ID 列表（4*n个BYTE）
     */
    private byte[] mParamsIdList;

    public byte[] getParamsIdList() {
        return mParamsIdList;
    }

    public void setParamsIdList(int... paramsIdList) {
        mParamsSum = (byte) paramsIdList.length;
        mParamsIdList = new byte[paramsIdList.length * 4];
        for (int i = 0; i < paramsIdList.length; i++) {
            byte[] paramId = ByteUtil.intToByte4(paramsIdList[i]);
            System.arraycopy(paramId, 0, mParamsIdList, i * 4, paramId.length);
        }
    }

    @Override
    public byte[] setMessageBody() {
        byte[] body = new byte[(int) mParamsSum * 4 + 1];
        System.arraycopy(new byte[]{mParamsSum}, 0, body, 0, 1);
        System.arraycopy(mParamsIdList, 0, body, 1, mParamsIdList.length);
        return body;
    }

    @Override
    protected byte[] setMessageId() {
        return ByteUtil.intToByte2(MESSAGE_ID);
    }
}
