package com.winjay.dlna;

import com.winjay.dlna.util.LogUtil;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * send GC ip to GO
 * @author F2848777
 * @date 2022-09-14
 */
public class ClientThread extends Thread {
    private static final String TAG = "ClientThread";
    private Socket socket;
    private String host;

    public ClientThread(String host) {
        this.host = host;
        socket = new Socket();
    }

    @Override
    public void run() {
        super.run();
        try {
            LogUtil.d(TAG, "Send GC ip to GO!");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, Constants.IP_PORT)), 500);
            OutputStream os = socket.getOutputStream();
            os.write(0);
            os.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "Send GC ip to GO! Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
