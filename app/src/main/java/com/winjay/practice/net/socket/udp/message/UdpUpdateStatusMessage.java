package com.winjay.practice.net.socket.udp.message;


import com.winjay.practice.utils.ByteUtil;

/**
 * 子设备更新状态
 */
public class UdpUpdateStatusMessage extends UdpSendMessage {
    /**
     * 消息Id
     */
    private static final int MESSAGE_ID = 0x0FA8;
    /**
     * 更新状态
     */
    private byte mResourceUpdateState = 0x00;
    /**
     * 外部设备文件（485 设备）发送结果
     */
    private byte mDownloadState = 0x00;

    @Override
    protected byte[] setMessageId() {
        return ByteUtil.intToByte2(MESSAGE_ID);
    }

    @Override
    protected byte[] setMessageBody() {
        byte[] body = new byte[7];
        // 更新状态（0x00: 终端资源文件列表更新完成 0x01：终端资源文件列表中的数据更新完成）
        body[0] = mResourceUpdateState;
        // 参数ID
        body[1] = 0x00;
        // 参数长度
        body[2] = ByteUtil.hexToByte(Integer.toHexString(1));
        // 参数值
        // 资源文件类型
        // 0x06: 语音主机音视频文件
        body[3] = 0x06;
        // 参数ID
        body[4] = 0x08;
        // 参数长度
        body[5] = ByteUtil.hexToByte(Integer.toHexString(1));
        // 参数值
        // 外部设备文件（485 设备）发送结果
        // 0x00：发送成功
        // 0x01：发送失败
        // 0x02：设备不在线
        // 0x03：ftp 文件下载失败
        body[6] = mDownloadState;
        return body;
    }

    /**
     * 更新状态
     *
     * @param status 0x00: 终端资源文件列表更新完成
     *               0x01：终端资源文件列表中的数据更新完成
     */
    public void setResourceUpdateState(int status) {
        mResourceUpdateState = ByteUtil.hexToByte(Integer.toHexString(status));
    }

    /**
     * 外部设备文件（485 设备）发送结果
     *
     * @param status 0x00：发送成功
     *               0x01：发送失败
     *               0x02：设备不在线
     *               0x03：ftp 文件下载失败
     */
    public void setDownloadState(int status) {
        mDownloadState = ByteUtil.hexToByte(Integer.toHexString(status));
    }
}
