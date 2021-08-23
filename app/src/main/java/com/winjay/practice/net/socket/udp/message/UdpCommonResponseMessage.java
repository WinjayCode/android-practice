package com.winjay.practice.net.socket.udp.message;


import com.winjay.practice.utils.ByteUtil;

/**
 * 子设备通用应答
 */
public class UdpCommonResponseMessage extends UdpSendMessage {
    /**
     * 消息Id
     */
    private static final int MESSAGE_ID = 0x0001;

    /**
     * 应答流水号（1个BYTE）
     */
    private byte mResponseSerialNumber;
    /**
     * 应答Id（2个BYTE）
     */
    private byte[] mResponseId = new byte[2];
    /**
     * 0：成功/确认；1：失败；2：消息有误；3：不支持
     */
    private byte mResult;

    public byte getResponseSerialNumber() {
        return mResponseSerialNumber;
    }

    public void setResponseSerialNumber(byte mResponseSerialNumber) {
        this.mResponseSerialNumber = mResponseSerialNumber;
    }

    public byte[] getResponseId() {
        return mResponseId;
    }

    public void setResponseId(byte[] mResponseId) {
        this.mResponseId = mResponseId;
    }

    public byte getResult() {
        return mResult;
    }

    public void setResult(byte mResult) {
        this.mResult = mResult;
    }

    @Override
    protected byte[] setMessageId() {
        return ByteUtil.intToByte2(MESSAGE_ID);
    }

    @Override
    protected byte[] setMessageBody() {
        byte[] body = new byte[1 + mResponseId.length + 1];
        body[0] = mResponseSerialNumber;
        System.arraycopy(mResponseId, 0, body, 1, mResponseId.length);
        body[1 + mResponseId.length] = mResult;
        return body;
    }
}
