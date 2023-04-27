package com.winjay.practice.net.socket.tcp;

import com.winjay.practice.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author Winjay
 * @date 2023-04-27
 */
public class ReceiveThread implements Runnable {
    private static final String TAG = ReceiveThread.class.getSimpleName();
    private ReceiveThreadListener receiveThreadListener;
    private Socket socket;
    private boolean isRunning = true;
    private InputStream inputStream;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            byte[] buf = new byte[1024];
            int len;

            while (isRunning  && !socket.isClosed() && (len = inputStream.read(buf)) != -1) {
                String data = new String(buf, 0, len);
                LogUtil.d(TAG, "data=" + data);
                if (receiveThreadListener != null) {
                    receiveThreadListener.onMessage(data);
                }
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        isRunning = false;
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ReceiveThreadListener {
        void onMessage(String message);
    }

    public void setReceiveThreadListener(ReceiveThreadListener listener) {
        receiveThreadListener = listener;
    }
}
