package com.winjay.practice.net.socket.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Winjay
 * @date 2023-04-27
 */
public class SendThread implements Runnable {
    private Socket socket;
    private String message;

    public SendThread(Socket socket, String msg) {
        this.socket = socket;
        message = msg;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

