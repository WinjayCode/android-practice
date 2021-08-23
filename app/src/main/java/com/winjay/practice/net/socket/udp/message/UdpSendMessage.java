package com.winjay.practice.net.socket.udp.message;


import com.winjay.practice.utils.ByteUtil;
import com.winjay.practice.utils.CRC16Util;

public abstract class UdpSendMessage extends UdpMessage {

    /**
     * 获取完整消息
     *
     * @return
     */
    public byte[] getMessage() {
        mMessageBody = setMessageBody();
        byte[] message = new byte[MESSAGE_START.length + mMessageHead.length + mMessageBody.length + mMessageCheck.length + MESSAGE_END.length];
        System.arraycopy(MESSAGE_START, 0, message, 0, MESSAGE_START.length);
        System.arraycopy(setMessageHead(), 0, message, MESSAGE_START.length, mMessageHead.length);
        System.arraycopy(mMessageBody, 0, message, MESSAGE_START.length + mMessageHead.length, mMessageBody.length);
        System.arraycopy(setMessageCheck(), 0, message, MESSAGE_START.length + mMessageHead.length + mMessageBody.length, mMessageCheck.length);
        System.arraycopy(MESSAGE_END, 0, message, MESSAGE_START.length + mMessageHead.length + mMessageBody.length + mMessageCheck.length, MESSAGE_END.length);
        return message;
    }

    private byte[] setMessageHead() {
        mMessageId = setMessageId();
        System.arraycopy(mMessageId, 0, mMessageHead,
                1 + 1 + mEthernetEncryptHead.length + 1, mMessageId.length);
        System.arraycopy(ByteUtil.intToByte2(mMessageBody.length), 0, mMessageHead,
                1 + 1 + mEthernetEncryptHead.length + 1 + mMessageId.length,
                mMessageBodyLength.length);
        return mMessageHead;
    }

    private byte[] setMessageCheck() {
        byte[] calculateCRCData = new byte[mMessageHead.length + mMessageBody.length + mEthernetDataIntegrity.length];
        System.arraycopy(mMessageHead, 0, calculateCRCData, 0, mMessageHead.length);
        System.arraycopy(mMessageBody, 0, calculateCRCData, mMessageHead.length, mMessageBody.length);
        System.arraycopy(mEthernetDataIntegrity, 0, calculateCRCData, mMessageHead.length + mMessageBody.length, mEthernetDataIntegrity.length);
        System.arraycopy(ByteUtil.intToByte2(CRC16Util.calculateCRC16(calculateCRCData)), 0, mMessageCheck, mEthernetDataIntegrity.length, mMessageCRC.length);
        return mMessageCheck;
    }

    protected abstract byte[] setMessageBody();

    protected abstract byte[] setMessageId();

    public byte getSN() {
        return mMessageHead[0];
    }

    public void setSN(byte mSN) {
        mMessageHead[0] = mSN;
    }

    public byte getMessageSerialNumber() {
        return mMessageHead[6];
    }

    public void setMessageSerialNumber(byte mMessageSerialNumber) {
        mMessageHead[6] = mMessageSerialNumber;
    }
}
