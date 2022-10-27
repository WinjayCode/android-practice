package com.winjay.dlna;

import com.winjay.dlna.util.LogUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * receive GC ip
 *
 * @author F2848777
 * @date 2022-09-14
 */
public class ServerThread extends Thread {
    private static final String TAG = "ServerThread";
    private boolean running = true;

    public ServerThread() {
    }

    @Override
    public void run() {
        super.run();
        try {
            while (running) {
                ServerSocket serverSocket = new ServerSocket(Constants.IP_PORT);
                Socket client = serverSocket.accept();
                LogUtil.d(TAG, "GC ip=" + client.getInetAddress().getHostAddress());
                LogUtil.d(TAG, "GC host name=" + client.getInetAddress().getHostName());

                List<String> addressList = new ArrayList<>();
                if (MainActivity.mGCIPAddressList.getValue() != null) {
                    addressList.addAll(MainActivity.mGCIPAddressList.getValue());
                }
                addressList.add(client.getInetAddress().getHostAddress());
                MainActivity.mGCIPAddressList.postValue(addressList);
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.running = false;
    }
}
