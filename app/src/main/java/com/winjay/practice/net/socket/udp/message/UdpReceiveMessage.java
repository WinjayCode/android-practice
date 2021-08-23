package com.winjay.practice.net.socket.udp.message;

public abstract class UdpReceiveMessage extends UdpMessage {
    public void setMessage(byte[] receivedData) {
        // message head
        System.arraycopy(receivedData, MESSAGE_START.length, mMessageHead, 0, mMessageHead.length);
        // message body
        int messageBodyLength = receivedData.length - MESSAGE_START.length - mMessageHead.length - mMessageCheck.length - MESSAGE_END.length;
        byte[] messageBody = new byte[messageBodyLength];
        System.arraycopy(receivedData, MESSAGE_START.length + mMessageHead.length, messageBody, 0, messageBodyLength);
        mMessageBody = messageBody;
        // message check
        System.arraycopy(receivedData, MESSAGE_START.length + mMessageHead.length + messageBodyLength, mMessageCheck, 0, mMessageCheck.length);
    }

    public byte getMessageSerialNumber() {
        return mMessageHead[1 + 1 + mEthernetEncryptHead.length];
    }

    public byte[] getMessageId() {
        byte[] messageId = new byte[2];
        System.arraycopy(mMessageHead, 1 + 1 + mEthernetEncryptHead.length + 1, messageId, 0, messageId.length);
        return messageId;
    }
}
