package com.winjay.practice.net.socket.udp.message;

import android.util.Log;

import com.winjay.practice.utils.ByteUtil;


/**
 * 域控制器向子设备下发资源更新指令
 */
public class UdpUpdateResourceMessage extends UdpReceiveMessage {
    private static final String TAG = UdpUpdateResourceMessage.class.getSimpleName();
    /**
     * 消息Id
     */
    public static final int MESSAGE_ID = 0x8FA7;
    /**
     * 操作类型
     * 0x01: 终端资源文件清空
     * 0x02: 资源文件更新推送
     */
    private int mOperationType;
    /**
     * 资源文件类型
     * 0x06: 语音主机音视频文件
     */
    private int mResourceType;
    /**
     * 资源文件获取方式:
     * - 0x01: ftp方式
     * - 0x02: Http方式
     * - 0x03: sftp方式
     * - 0x04: Https方式
     */
    private int mResourceObtainType;
    /**
     * 资源文件URL地址
     */
    private String mResourceUrl;
    /**
     * 资源文件名
     */
    private String mResourceName;
    /**
     * 资源文件MD5校验码
     */
    private String mResourceMD5;
    /**
     * 资源服务器登录用户名
     */
    private String mResourceServerUserName;
    /**
     * 资源服务器登录密码
     */
    private String mResourceServerPassword;
    /**
     * 服务器ip或域名
     */
    private String mResourceServerHost;
    /**
     * 端口号
     */
    private int mResourceServerPort;

    @Override
    public void setMessage(byte[] receivedData) {
        super.setMessage(receivedData);
        parseMessageBody();
    }

    private void parseMessageBody() {
        mOperationType = mMessageBody[0] & 0xFF;
        Log.d(TAG, "mOperationType=" + mOperationType);
        int allParamsLength = 0;
        while (allParamsLength < mMessageBody.length - 1) {
            int paramIdPos = allParamsLength + 1;
            int paramId = mMessageBody[allParamsLength + 1] & 0xFF;
            int paramLength = mMessageBody[paramIdPos + 1] & 0xFF;
            switch (paramId) {
                case 0x00:
                    if (paramLength == 1) {
                        mResourceType = mMessageBody[paramIdPos + 1 + 1] & 0xFF;
                        Log.d(TAG, "mResourceType=" + mResourceType);
                    }
                    break;
                case 0x02:
                    if (paramLength == 1) {
                        mResourceObtainType = mMessageBody[paramIdPos + 1 + 1] & 0xFF;
                        Log.d(TAG, "mResourceObtainType=" + mResourceObtainType);
                    }
                    break;
                case 0x03:
                    byte[] url = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, url, 0, url.length);
                    mResourceUrl = new String(url);
                    Log.d(TAG, "mResourceUrl=" + mResourceUrl);
                    break;
                case 0x04:
                    byte[] name = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, name, 0, name.length);
                    mResourceName = new String(name);
                    Log.d(TAG, "mResourceName=" + mResourceName);
                    break;
                case 0x06:
                    byte[] md5 = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, md5, 0, md5.length);
                    mResourceMD5 = new String(md5);
                    Log.d(TAG, "mResourceMD5=" + mResourceMD5);
                    break;
                case 0x07:
                    byte[] serverUserName = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, serverUserName, 0, serverUserName.length);
                    mResourceServerUserName = new String(serverUserName);
                    Log.d(TAG, "mResourceServerUserName=" + mResourceServerUserName);
                    break;
                case 0x08:
                    byte[] serverPassword = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, serverPassword, 0, serverPassword.length);
                    mResourceServerPassword = new String(serverPassword);
                    Log.d(TAG, "mResourceServerPassword=" + mResourceServerPassword);
                    break;
                case 0x09:
                    byte[] serverHost = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, serverHost, 0, serverHost.length);
                    mResourceServerHost = new String(serverHost);
                    Log.d(TAG, "mResourceServerHost=" + mResourceServerHost);
                    break;
                case 0x0A:
                    byte[] serverPort = new byte[paramLength];
                    System.arraycopy(mMessageBody, paramIdPos + 1 + 1, serverPort, 0, serverPort.length);
                    mResourceServerPort = ByteUtil.bytesToInt(serverPort);
                    Log.d(TAG, "mResourceServerPort=" + mResourceServerPort);
                    break;
            }
            allParamsLength += 1 + 1 + paramLength;
        }
    }

    public int getOperationType() {
        return mOperationType;
    }

    public int getResourceType() {
        return mResourceType;
    }

    public int getResourceObtainType() {
        return mResourceObtainType;
    }

    public String getResourceUrl() {
        return mResourceUrl;
    }

    public String getResourceName() {
        return mResourceName;
    }

    public String getResourceMD5() {
        return mResourceMD5;
    }

    public String getResourceServerUserName() {
        return mResourceServerUserName;
    }

    public String getResourceServerPassword() {
        return mResourceServerPassword;
    }

    public String getResourceServerHost() {
        return mResourceServerHost;
    }

    public int getResourceServerPort() {
        return mResourceServerPort;
    }
}
