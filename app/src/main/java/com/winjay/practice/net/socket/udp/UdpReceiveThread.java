package com.winjay.practice.net.socket.udp;

import android.util.Log;


import com.winjay.practice.utils.ByteUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * UDP接收线程
 */
public class UdpReceiveThread implements Runnable {
    private static final String TAG = UdpReceiveThread.class.getSimpleName();
    private volatile Thread thread = null;
    //线程开始标志
    private boolean keepRunning = true;
    private DatagramSocket udpSocket;
    private UpdReceiveListener mUpdReceiveListener;

    @Override
    public void run() {
        byte[] data = new byte[0x10000];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        while (keepRunning) {
            try {
                //等待客户机连接
//                packet.setData(data);
                udpSocket.receive(packet);
                Log.d(TAG, "data.size=" + packet.getLength());
                byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());
                Log.d(TAG, "receivedData=" + ByteUtil.bytesToHex(receivedData));
                if (mUpdReceiveListener != null && receivedData.length > 0) {
                    mUpdReceiveListener.onDatagramPacketReceived(receivedData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
        thread = new Thread(this, TAG);
        thread.start();
    }

    public void stop() {
        keepRunning = false;

        if (udpSocket != null) {
            udpSocket.close();
            udpSocket = null;
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread = null;
    }

    public interface UpdReceiveListener {
        void onDatagramPacketReceived(byte[] receivedData);
    }

    public void setUpdReceiveListener(UpdReceiveListener mUpdReceiveListener) {
        this.mUpdReceiveListener = mUpdReceiveListener;
    }
}