package com.winjay.practice.net.socket.udp.message;

/**
 * 消息结构：
 * 数据起始符+消息头+消息体+校验数据+数据结束符
 */
public abstract class UdpMessage {
    /**
     * 数据起始符：使用‘Y’‘-’‘T’三字符的 ASCII 值作为起始标志，占 3 个 BYTE。
     */
    public static final byte[] MESSAGE_START = new byte[]{0x59, 0x2D, 0x54};

    /**
     * 数据结束符：使用‘K’‘+’‘C’三字符的 ASCII 值作为结束标志，占 3 个 BYTE。
     */
    public static byte[] MESSAGE_END = new byte[]{0x4B, 0x2B, 0x43};

    /**
     * 消息头：
     * SN（1个BYTE）：为兼容自动驾驶域间通讯协议保留该字段，各设备填充各自IP地址最后一个字节（例：192.168.100.47，取47）
     * 以太网加密及协议版本标识（1个BYTE）：全为0即可
     * 以太网加密标识头部信息（4个BYTE）：全为0即可
     * 消息流水号（1个BYTE）：按发送顺序从 0 开始循环累加
     * 消息ID（2个BYTE）
     * 消息体长度（2个BYTE）
     */
    protected byte[] mMessageHead = new byte[11];
    /**
     * 消息头_SN
     */
    private byte mSN;
    /**
     * 消息头_以太网加密及协议版本标识（1个BYTE）
     */
    private byte mEthernetEncryptVersion;
    /**
     * 消息头_以太网加密标识头部信息（4个BYTE）
     */
    protected final byte[] mEthernetEncryptHead = new byte[4];
    /**
     * 消息头_消息流水号
     */
    private byte mMessageSerialNumber;
    /**
     * 消息头_消息ID
     */
    protected byte[] mMessageId = new byte[2];
    /**
     * 消息头_消息体长度
     */
    protected byte[] mMessageBodyLength = new byte[2];

    /**
     * 消息体
     */
    protected byte[] mMessageBody = new byte[]{};

    /**
     * 校验数据：
     * 以太网数据完整性校验（32个BYTE）：为兼容自动驾驶域间通讯协议保留该字段，可填充0
     * ETH_CRC（2个BYTE）：ETH_CRC：从 SN 到以太网完整性校验字段每个字节进行 CRC-16 运算
     */
    protected byte[] mMessageCheck = new byte[34];
    /**
     * 校验数据_以太网数据完整性校验（32个BYTE）
     */
    protected byte[] mEthernetDataIntegrity = new byte[32];
    /**
     * 校验数据_ETH_CRC（2个BYTE）
     */
    protected byte[] mMessageCRC = new byte[2];

    public byte[] getMessageHead() {
        return mMessageHead;
    }

    public byte[] getMessageCheck() {
        return mMessageCheck;
    }

    public byte[] getMessageBody() {
        return mMessageBody;
    }
}
