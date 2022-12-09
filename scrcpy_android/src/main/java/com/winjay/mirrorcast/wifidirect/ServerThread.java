package com.winjay.mirrorcast.wifidirect;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.app_mirror.AppSocketClientManager;
import com.winjay.mirrorcast.util.LogUtil;

import java.io.InputStream;
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
                LogUtil.d(TAG, "wait client");
                Socket client = serverSocket.accept();
                LogUtil.d(TAG, "GC ip=" + client.getInetAddress().getHostAddress());
//                LogUtil.d(TAG, "GC host name=" + client.getInetAddress().getHostName());
                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[1];
                while (inputStream.read(bytes) != -1) {
                    if (bytes[0] == 1) {
                        LogUtil.d(TAG, "start AppSocketClientManager");
//                        AppSocketServerManager.getInstance().startServer();
                        AppSocketClientManager.getInstance().connect(client.getInetAddress().getHostAddress());
                        serverSocket.close();
                        return;
                    }
                }

                LogUtil.d(TAG, "get GC IP");
                List<String> addressList = new ArrayList<>();
                if (WIFIDirectActivity.mGCIPAddressList.getValue() != null) {
                    addressList.addAll(WIFIDirectActivity.mGCIPAddressList.getValue());
                }
                addressList.add(client.getInetAddress().getHostAddress());
                WIFIDirectActivity.mGCIPAddressList.postValue(addressList);
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
//        AppMirrorSocketServerManager.getInstance().stopServer();
        this.running = false;
    }
}
