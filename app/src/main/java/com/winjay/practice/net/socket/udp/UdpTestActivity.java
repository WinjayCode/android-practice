package com.winjay.practice.net.socket.udp;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.net.socket.udp.message.UdpCommonResponseMessage;
import com.winjay.practice.net.socket.udp.message.UdpMessage;
import com.winjay.practice.net.socket.udp.message.UdpQueryParamsMessage;
import com.winjay.practice.net.socket.udp.message.UdpSetParamsMessage;
import com.winjay.practice.net.socket.udp.message.UdpUpdateResourceMessage;
import com.winjay.practice.net.socket.udp.message.UdpUpdateStatusMessage;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.ByteUtil;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.FtpUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.MediaUtil;
import com.winjay.practice.utils.NetUtil;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Arrays;

import butterknife.BindView;

/**
 * Socket使用UDP协议的示例
 *
 * 通过创建DatagramSocket类实例，并指定UDP的端口号来实现数据报文的接收和发送
 *
 * @author Winjay
 * @date 2021-08-17
 */
public class UdpTestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = UdpTestActivity.class.getSimpleName();
    //目的主机IP
    private String SERVER_IP = "192.168.1.1";
    //目的主机端口
    private int SERVER_PORT = 10000;
    //本机监听端口
    private static final int LOCAL_PORT = 10001;
    //本机IP
    private String LOCAL_IP;
    // 使用UPD协议的Socket
    private DatagramSocket udpSocket;
    private UdpReceiveThread udpReceiveThread;
    private UdpSendThread udpSendThread;

    @BindView(R.id.info_tv)
    TextView info_tv;

    @BindView(R.id.query_vin_btn)
    Button query_vin_btn;

    @BindView(R.id.response_btn)
    Button response_btn;

    @BindView(R.id.update_status_btn)
    Button update_status_btn;

    @BindView(R.id.send_data_tv)
    TextView send_data_tv;

    @BindView(R.id.receive_data_tv)
    TextView receive_data_tv;

    private int messageSerialNumber;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.udp_test_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SERVER_IP = getIntent().getStringExtra("ip");
        SERVER_PORT = Integer.parseInt(getIntent().getStringExtra("port"));

        info_tv = findViewById(R.id.info_tv);
        info_tv.append("\n目的IP：" + SERVER_IP + "\n" + "目的端口：" + SERVER_PORT);
        info_tv.append("\n本地IP：" + NetUtil.getIPAddress(UdpTestActivity.this) + "\n" + "本地端口：" + LOCAL_PORT);

        query_vin_btn = findViewById(R.id.query_vin_btn);
        query_vin_btn.setOnClickListener(this);
        response_btn = findViewById(R.id.response_btn);
        response_btn.setOnClickListener(this);
        update_status_btn = findViewById(R.id.update_status_btn);
        update_status_btn.setOnClickListener(this);

        send_data_tv = findViewById(R.id.send_data_tv);
        receive_data_tv = findViewById(R.id.receive_data_tv);

        createUdpSocket();
        mHandler = new Handler(Looper.getMainLooper());
//        getVin();
    }

    private void createUdpSocket() {
        try {
            // 监听本地端口
            udpSocket = new DatagramSocket(LOCAL_PORT);
            udpSocket.setBroadcast(true);
//          udpSocket.setSoTimeout(200);
        } catch (Exception e) {
            LogUtil.e(TAG, "error=" + e.toString());
            e.printStackTrace();
        }
        udpReceiveThread = new UdpReceiveThread();
        udpReceiveThread.setUpdReceiveListener(new UdpReceiveThread.UpdReceiveListener() {
            @Override
            public void onDatagramPacketReceived(byte[] receivedData) {
                parseReceiveData(receivedData);
            }
        });
        udpReceiveThread.start(udpSocket);
    }

    private void parseReceiveData(byte[] receivedData) {
        receive_data_tv.post(new Runnable() {
            @Override
            public void run() {
                receive_data_tv.setText(ByteUtil.bytesToHex(receivedData));
            }
        });
        // 数据起始符校验
        byte[] messageStart = new byte[3];
        System.arraycopy(receivedData, 0, messageStart, 0, messageStart.length);
        if (!Arrays.equals(UdpMessage.MESSAGE_START, messageStart)) {
            Log.e(TAG, "message start is wrong!");
            return;
        }
        // 数据结束符校验
        byte[] messageEnd = new byte[3];
        System.arraycopy(receivedData, receivedData.length - messageEnd.length, messageEnd, 0, messageEnd.length);
        if (!Arrays.equals(UdpMessage.MESSAGE_END, messageEnd)) {
            Log.e(TAG, "message end is wrong!");
            return;
        }
        // CRC16校验
//        byte[] messageCheck = new byte[2];
//        byte[] messageCheckCalculate = new byte[receivedData.length - (messageStart.length + messageEnd.length + messageCheck.length)];
//        System.arraycopy(receivedData, receivedData.length - messageEnd.length - messageCheck.length, messageCheck, 0, messageCheck.length);
//        System.arraycopy(receivedData, messageStart.length, messageCheckCalculate, 0, messageCheckCalculate.length);
//        byte[] crc16 = ByteUtil.intToByte2(CRC16Util.calculateCRC16(messageCheckCalculate));
//        Log.d(TAG, "messageCheck=" + ByteUtil.bytesToHex(messageCheck));
//        Log.d(TAG, "calculateCRC16=" + ByteUtil.bytesToHex(crc16));
//        if (!Arrays.equals(crc16, messageCheck)) {
//            Log.e(TAG, "CRC16 check is wrong!");
//            return;
//        }
        // 消息ID
        byte[] messageId = new byte[2];
        System.arraycopy(receivedData, 10, messageId, 0, messageId.length);
        if (Arrays.equals(ByteUtil.intToByte2(UdpSetParamsMessage.MESSAGE_ID), messageId)) {
            UdpSetParamsMessage udpSetParamsMessage = new UdpSetParamsMessage();
            udpSetParamsMessage.setMessage(receivedData);
            String vin = udpSetParamsMessage.getVIN();
            Log.d(TAG, "vin=" + vin);
            if (!TextUtils.isEmpty(vin)) {
//                Utils.setProperty(SYSTEM_PROP_VIN, vin);
//                Log.d(TAG, "prop vin=" + Utils.getProperty(SYSTEM_PROP_VIN));

                // 子设备通用应答
                UdpCommonResponseMessage udpCommonResponseMessage = new UdpCommonResponseMessage();
                udpCommonResponseMessage.setSN(getSN());
                udpCommonResponseMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
                udpCommonResponseMessage.setResponseSerialNumber(udpSetParamsMessage.getMessageSerialNumber());
                udpCommonResponseMessage.setResponseId(udpSetParamsMessage.getMessageId());
                udpCommonResponseMessage.setResult(ByteUtil.hexToByte(Integer.toHexString(0)));
                sendUpdMessage(udpCommonResponseMessage.getMessage());

                receive_data_tv.post(new Runnable() {
                    @Override
                    public void run() {
                        receive_data_tv.append("\nVIN=" + vin);
                    }
                });
            }
        }
        if (Arrays.equals(ByteUtil.intToByte2(UdpUpdateResourceMessage.MESSAGE_ID), messageId)) {
            Log.d(TAG, "synchronize resource");
            UdpUpdateResourceMessage udpUpdateResourceMessage = new UdpUpdateResourceMessage();
            udpUpdateResourceMessage.setMessage(receivedData);
            // 子设备通用应答
            UdpCommonResponseMessage udpCommonResponseMessage = new UdpCommonResponseMessage();
            udpCommonResponseMessage.setSN(getSN());
            udpCommonResponseMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
            udpCommonResponseMessage.setResponseSerialNumber(udpUpdateResourceMessage.getMessageSerialNumber());
            udpCommonResponseMessage.setResponseId(udpUpdateResourceMessage.getMessageId());
            udpCommonResponseMessage.setResult(ByteUtil.hexToByte(Integer.toHexString(0)));
            sendUpdMessage(udpCommonResponseMessage.getMessage());

            send_data_tv.append("\n数据起始符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_START));
            send_data_tv.append("\n消息头：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageHead()));
            send_data_tv.append("\n消息体：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageBody()));
            send_data_tv.append("\n校验数据：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageCheck()));
            send_data_tv.append("\n数据结束符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_END));

            if (udpUpdateResourceMessage.getOperationType() == 0x01) {
                Log.d(TAG, "clear resource!");
                // 清空终端资源文件
                clearAllLocalResource();
            } else if (udpUpdateResourceMessage.getOperationType() == 0x02
                    && udpUpdateResourceMessage.getResourceType() == 0x06
                    && udpUpdateResourceMessage.getResourceObtainType() == 0x01) {
                Log.d(TAG, "download resource!");
                downloadResource(udpUpdateResourceMessage);
            }
        }
    }

    private void clearAllLocalResource() {
        // 更新媒体库
//        MediaUtil.removeFile(getApplicationContext(), MusicConstant.VOICE_CONTROL_VIDEO_PATH);
//        MediaUtil.removeFile(getApplicationContext(), MusicConstant.SIGNAL_CONTROL_VIDEO_PATH);

        // 子设备资源更新状态汇报
        UdpUpdateStatusMessage udpUpdateStatusMessage = new UdpUpdateStatusMessage();
        udpUpdateStatusMessage.setSN(getSN());
        udpUpdateStatusMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
        sendUpdMessage(udpUpdateStatusMessage.getMessage());
    }

    private void downloadResource(UdpUpdateResourceMessage udpUpdateResourceMessage) {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                FtpUtil ftpUtils = new FtpUtil();
                FTPClient ftpClient = ftpUtils.getFTPClient(udpUpdateResourceMessage.getResourceServerHost(),
                        udpUpdateResourceMessage.getResourceServerPort(),
                        udpUpdateResourceMessage.getResourceServerUserName(),
                        udpUpdateResourceMessage.getResourceServerPassword());
                String savedPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.d(TAG, "savedPath:" + savedPath);

                // 子设备资源更新状态汇报
                UdpUpdateStatusMessage udpUpdateStatusMessage = new UdpUpdateStatusMessage();
                udpUpdateStatusMessage.setSN(getSN());
                udpUpdateStatusMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));

                if (ftpClient != null) {
                    String filePath = udpUpdateResourceMessage.getResourceUrl();
//                    if (filePath.startsWith("/")) {
//                        filePath = filePath.substring(1);
//                    }
                    Log.d(TAG, "filePath=" + filePath);
                    boolean result = ftpUtils.downLoadFTP(ftpClient, filePath, udpUpdateResourceMessage.getResourceName(), savedPath);
                    Log.d(TAG, "result=" + result);
                    ftpUtils.closeFTP(ftpClient);

                    String md5 = FileUtil.getFileMD5(new File(savedPath + "/" + udpUpdateResourceMessage.getResourceName()));
                    Log.d(TAG, "md5=" + md5);

                    if (result) {
                        try {
                            FileUtil.upZipFile(savedPath + "/" + udpUpdateResourceMessage.getResourceName(), savedPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String zipDirName = "";
                        if (udpUpdateResourceMessage.getResourceName().endsWith(".zip")) {
                            zipDirName = udpUpdateResourceMessage.getResourceName().replace(".zip", "");
                        }
                        Log.d(TAG, "zipDirName=" + zipDirName);
                        if (!TextUtils.isEmpty(zipDirName)) {
                            File test = new File(savedPath + File.separator + zipDirName);
                            if (test.exists()) {
                                for (File file : test.listFiles()) {
                                    Log.d(TAG, "dirName=" + file.getName());
                                    if (new File(savedPath + File.separator + file.getName()).exists()) {
//                                        FileUtil.deleteFolder(savedPath + File.separator + file.getName());
                                    }
                                    FileUtil.copyFolder(savedPath + File.separator + zipDirName + File.separator + file.getName(),
                                            savedPath + File.separator + file.getName());
                                }
                            }
                        }
//                        FileUtil.deleteFolder(savedPath + File.separator + zipDirName);
//                        FileUtil.deleteFolder(savedPath + File.separator + udpUpdateResourceMessage.getResourceName());
                        // 更新媒体库
//                        MediaUtil.scanFile(getApplicationContext(), MusicConstant.VOICE_CONTROL_VIDEO_PATH);
//                        MediaUtil.scanFile(getApplicationContext(), MusicConstant.SIGNAL_CONTROL_VIDEO_PATH);
                    } else {
                        udpUpdateStatusMessage.setDownloadState(3);
                    }
                } else {
                    Log.d(TAG, "ftpClient == null!");
                    udpUpdateStatusMessage.setDownloadState(3);
                }
                sendUpdMessage(udpUpdateStatusMessage.getMessage());
                send_data_tv.append("\n数据起始符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_START));
                send_data_tv.append("\n消息头：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageHead()));
                send_data_tv.append("\n消息体：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageBody()));
                send_data_tv.append("\n校验数据：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageCheck()));
                send_data_tv.append("\n数据结束符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_END));
            }
        });
    }

    private void sendUpdMessage(byte[] data) {
        send_data_tv.setText(ByteUtil.bytesToHex(data));
        udpSendThread = new UdpSendThread();
        udpSendThread.sendMsg(udpSocket, data, SERVER_IP, SERVER_PORT);
    }

    private byte getSN() {
//        String ip = NetUtils.getIPAddress(getApplicationContext());
//        if (!TextUtils.isEmpty(ip)) {
//            String[] strings = ip.split("\\.");
//            String last = strings[strings.length - 1];
//            Log.d(TAG, "sn=" + last);
//            return ByteUtil.hexToByte(Integer.toHexString(Integer.parseInt(last)));
//        }
        return ByteUtil.hexToByte(Integer.toHexString(47));
    }

    private void getVin() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "getVin()");
//                if (TextUtils.isEmpty(Utils.getProperty(SYSTEM_PROP_VIN))) {
//                    Log.d(TAG, "send udpQueryParamsMessage!");
//                    // 请求VIN
//                    UdpQueryParamsMessage udpQueryParamsMessage = new UdpQueryParamsMessage();
//                    udpQueryParamsMessage.setSN(getSN());
//                    udpQueryParamsMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
//                    udpQueryParamsMessage.setParamsIdList(UdpQueryParamsMessage.VIN);
//                    sendUpdMessage(udpQueryParamsMessage.getMessage());
//
//                    mHandler.postDelayed(this, 5000);
//                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_vin_btn:
                UdpQueryParamsMessage udpQueryParamsMessage = new UdpQueryParamsMessage();
                udpQueryParamsMessage.setSN(getSN());
                udpQueryParamsMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
                udpQueryParamsMessage.setParamsIdList(UdpQueryParamsMessage.VIN, UdpQueryParamsMessage.CHE_GONG_NUM);
                sendUpdMessage(udpQueryParamsMessage.getMessage());
                send_data_tv.append("\n数据起始符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_START));
                send_data_tv.append("\n消息头：\n" + ByteUtil.bytesToHex(udpQueryParamsMessage.getMessageHead()));
                send_data_tv.append("\n消息体：\n" + ByteUtil.bytesToHex(udpQueryParamsMessage.getMessageBody()));
                send_data_tv.append("\n校验数据：\n" + ByteUtil.bytesToHex(udpQueryParamsMessage.getMessageCheck()));
                send_data_tv.append("\n数据结束符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_END));
                break;
            case R.id.response_btn:
                UdpCommonResponseMessage udpCommonResponseMessage = new UdpCommonResponseMessage();
                udpCommonResponseMessage.setSN(getSN());
                udpCommonResponseMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
                udpCommonResponseMessage.setResponseSerialNumber(ByteUtil.hexToByte(Integer.toHexString(1)));
                udpCommonResponseMessage.setResponseId(ByteUtil.intToByte2(1));
                udpCommonResponseMessage.setResult(ByteUtil.hexToByte(Integer.toHexString(0)));
                sendUpdMessage(udpCommonResponseMessage.getMessage());
                send_data_tv.append("\n数据起始符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_START));
                send_data_tv.append("\n消息头：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageHead()));
                send_data_tv.append("\n消息体：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageBody()));
                send_data_tv.append("\n校验数据：\n" + ByteUtil.bytesToHex(udpCommonResponseMessage.getMessageCheck()));
                send_data_tv.append("\n数据结束符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_END));
                break;
            case R.id.update_status_btn:
                UdpUpdateStatusMessage udpUpdateStatusMessage = new UdpUpdateStatusMessage();
                udpUpdateStatusMessage.setSN(getSN());
                udpUpdateStatusMessage.setMessageSerialNumber(ByteUtil.hexToByte(Integer.toHexString(messageSerialNumber++)));
                sendUpdMessage(udpUpdateStatusMessage.getMessage());
                send_data_tv.append("\n数据起始符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_START));
                send_data_tv.append("\n消息头：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageHead()));
                send_data_tv.append("\n消息体：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageBody()));
                send_data_tv.append("\n校验数据：\n" + ByteUtil.bytesToHex(udpUpdateStatusMessage.getMessageCheck()));
                send_data_tv.append("\n数据结束符：\n" + ByteUtil.bytesToHex(UdpMessage.MESSAGE_END));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        udpReceiveThread.stop();
    }
}
