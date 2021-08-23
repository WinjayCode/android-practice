package com.winjay.practice.net.socket.udp;

import android.util.Log;


import com.winjay.practice.utils.ByteUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP发送线程
 */
public class UdpSendThread implements Runnable {
    private static final String TAG = UdpSendThread.class.getSimpleName();
    private volatile Thread thread = null;
    private byte[] sendData;
    private String address;
    private int port;
    private DatagramSocket udpSocket;

    @Override
    public void run() {
        try {
            Log.d(TAG, "send data.size=" + sendData.length);
            // 目的IP
            InetAddress ip = InetAddress.getByName(address);
            // 目的端口
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
            udpSocket.send(sendPacket);
//            udpSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(DatagramSocket udpSocket, byte[] data, String address, int port) {
        Log.d(TAG, "address=" + address + ", port=" + port + ", data=" + ByteUtil.bytesToHex(data));
        this.udpSocket = udpSocket;
        this.sendData = data;
        this.address = address;
        this.port = port;
        thread = new Thread(this, TAG);
        thread.start();
    }
}
