package com.winjay.mirrorcast.server;

import android.view.MotionEvent;

import com.winjay.mirrorcast.util.LogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ScreenSocketServer extends WebSocketServer {
    private final String TAG = ScreenSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;

    public ScreenSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogUtil.d(TAG);
        mWebSocket = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtil.d(TAG, "onClose:" + reason);
    }

    private boolean isAppMirror = false;

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, "message=" + message);
        // 应用流转
        if (message.equals("app_mirror")) {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new SocketClient("am stack list", new SocketClient.onServiceSend() {
                            @Override
                            public void getSend(String result) {
                                LogUtil.d(TAG, "result=" + result);
                                int start = result.indexOf("taskId=");
                                LogUtil.d(TAG, "start=" + start);

                                if (start == -1) {
                                    return;
                                }

                                int end = result.indexOf(":");
                                LogUtil.d(TAG, "end=" + end);
                                int taskId = Integer.parseInt(result.substring(start + 7, end));
                                LogUtil.d(TAG, "top task id=" + taskId);

                                String command = "am display move-stack " + taskId + " " + ScreenEncoder.VIRTUAL_DISPLAY_ID;
                                LogUtil.d(TAG, "move-stack command=" + command);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SocketClient(command, new SocketClient.onServiceSend() {
                                            @Override
                                            public void getSend(String result) {
                                                LogUtil.d(TAG, "move-stack result=" + result);
                                                if (result.equals("###ShellOK#")) {
                                                    isAppMirror = true;
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 反控
        else {
            String[] split = message.split("/");

            String motionevent = "";
            int action = Integer.parseInt(split[0]);
            if (action == MotionEvent.ACTION_DOWN) {
                motionevent = "DOWN";
            } else if (action == MotionEvent.ACTION_UP) {
                motionevent = "UP";
            } else if (action == MotionEvent.ACTION_MOVE) {
                motionevent = "MOVE";
            } else if (action == MotionEvent.ACTION_CANCEL) {
                motionevent = "CANCEL";
            }

            LogUtil.d(TAG, "x=" + split[1]);
            LogUtil.d(TAG, "y=" + split[2]);

            String command;
            if (isAppMirror) {
                command = "input -d " + ScreenEncoder.VIRTUAL_DISPLAY_ID + " motionevent " + motionevent + " " + Integer.parseInt(split[1]) + " " + Integer.parseInt(split[2]);
            } else {
                command = "input motionevent " + motionevent + " " + Integer.parseInt(split[1]) + " " + Integer.parseInt(split[2]);
            }
            LogUtil.d(TAG, "motionevent command=" + command);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new SocketClient(command, new SocketClient.onServiceSend() {
                        @Override
                        public void getSend(String result) {
                            LogUtil.d(TAG, "motionevent result=" + result);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtil.d(TAG, "onError:" + ex.toString());
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG);
    }

    public void sendData(byte[] bytes) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            // 通过WebSocket 发送数据
//      Log.d(TAG, "sendData:");
            mWebSocket.send(bytes);
        }
    }

    public void close() {
        if (mWebSocket != null) {
            LogUtil.d(TAG);
            mWebSocket.close();
        }
    }
}
