package com.winjay.scrcpy;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author F2848777
 * @date 2022-12-02
 */
public class DroidSocketClientManager {
    private static DroidSocketClientManager mInstance;

    private DroidSocketClient mDroidSocketClient;

    private DroidSocketClientManager() {
    }

    public static DroidSocketClientManager getInstance() {
        synchronized (DroidSocketClientManager.class) {
            if (mInstance == null) {
                mInstance = new DroidSocketClientManager();
            }
        }
        return mInstance;
    }

    public void connect(String serverIp, String port, Device device, DroidSocketClient.OnSocketClientListener listener) {
        try {
            URI uri = new URI("ws://" + serverIp + ":" + port);
            mDroidSocketClient = new DroidSocketClient(uri, device, listener);
            mDroidSocketClient.connect();
        } catch (URISyntaxException e) {
            Ln.e("DroidSocketClient connect error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setController(DroidController controller) {
        if (mDroidSocketClient != null) {
            mDroidSocketClient.setController(controller);
        }
    }

    public void sendData(byte[] bytes) {
        if (mDroidSocketClient != null) {
            mDroidSocketClient.send(bytes);
        }
    }

    public void close() {
        if (mDroidSocketClient != null) {
            mDroidSocketClient.close();
        }
    }
}
