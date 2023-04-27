package com.winjay.practice.net.socket.tcp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityTcpTestBinding;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket使用TCP示例
 * <p>
 * Socket socket = new Socket("127.0.0.1", 8080); // 创建TCP套接字，连接本地IP地址和端口号为8080的服务端
 *
 * @author Winjay
 * @date 2023-04-26
 */
public class TCPTestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = TCPTestActivity.class.getSimpleName();
    private ActivityTcpTestBinding binding;

    private static final int MSG_SERVER_SOCKET = 1;
    private static final int MSG_SOCKET = 2;

    private ServerSocket serverSocket;
    private Socket connectedServerSocket;
    private ReceiveThread serverSocketReceiveThread;

    private Socket socketClient;
    private ReceiveThread socketClientReceiveThread;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityTcpTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.startSocketServerBtn.setOnClickListener(this);
        binding.serverSocketSendBtn.setOnClickListener(this);


        binding.startSocketClientBtn.setOnClickListener(this);
        binding.socketClientSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.startSocketServerBtn) {
            if (serverSocket == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 创建ServerSocket对象，监听端口
                            serverSocket = new ServerSocket(8888);
                            Message msg = Message.obtain(handler, MSG_SERVER_SOCKET);
                            msg.obj = "服务器已启动，等待客户端连接..." + "\n";
                            handler.sendMessage(msg);

                            while (true) {
                                // 接受客户端请求
                                connectedServerSocket = serverSocket.accept();
                                Message msg2 = Message.obtain(handler, MSG_SERVER_SOCKET);
                                msg2.obj = "客户端已连接：" + connectedServerSocket.getInetAddress() + "\n";
                                handler.sendMessage(msg2);

                                // 创建新线程来接收客户端消息
                                serverSocketReceiveThread = new ReceiveThread(connectedServerSocket);
                                serverSocketReceiveThread.setReceiveThreadListener(new ReceiveThread.ReceiveThreadListener() {
                                    @Override
                                    public void onMessage(String message) {
                                        Message msg = Message.obtain(handler, MSG_SERVER_SOCKET);
                                        msg.obj = "From Client:" + message;
                                        handler.sendMessage(msg);
                                    }
                                });
                                Thread receiveThread = new Thread(serverSocketReceiveThread);
                                receiveThread.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (v == binding.serverSocketSendBtn) {
            if (serverSocket != null) {
                String msg = binding.serverSocketEt.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    toast("内容不能为空！");
                    return;
                }
                // 创建新线程来发送消息给客户端
                Thread sendThread = new Thread(new SendThread(connectedServerSocket, msg));
                sendThread.start();
            } else {
                toast("请先启动ServerSocket服务端！");
            }
        }

        if (v == binding.startSocketClientBtn) {
            if (socketClient == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 创建Socket对象，连接服务器
                            socketClient = new Socket("localhost", 8888);
                            Message msg = Message.obtain(handler, MSG_SOCKET);
                            msg.obj = "已连接服务器：" + socketClient.getInetAddress() + "\n";
                            handler.sendMessage(msg);

                            // 创建新线程来接收服务器消息
                            socketClientReceiveThread = new ReceiveThread(socketClient);
                            socketClientReceiveThread.setReceiveThreadListener(new ReceiveThread.ReceiveThreadListener() {
                                @Override
                                public void onMessage(String message) {
                                    Message msg = Message.obtain(handler, MSG_SOCKET);
                                    msg.obj = "From Server:" + message;
                                    handler.sendMessage(msg);
                                }
                            });
                            Thread receiveThread = new Thread(socketClientReceiveThread);
                            receiveThread.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (v == binding.socketClientSendBtn) {
            if (socketClient != null) {
                String msg = binding.socketClientEt.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    toast("内容不能为空！");
                    return;
                }
                // 创建新线程来发送消息给服务器
                Thread sendThread = new Thread(new SendThread(socketClient, msg));
                sendThread.start();
            } else {
                toast("请先启动Socket客户端！");
            }
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_SERVER_SOCKET:
                    binding.serverSocketMsgTv.setText(binding.serverSocketMsgTv.getText().toString() + msg.obj + "\n");
                    break;
                case MSG_SOCKET:
                    binding.socketClientMsgTv.setText(binding.socketClientMsgTv.getText().toString() + msg.obj + "\n");
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO how to close socket safely?
        try {
            serverSocketReceiveThread.stopThread();
            socketClientReceiveThread.stopThread();

            if (socketClient != null && !socketClient.isClosed()) {
                socketClient.close();
            }
            if (connectedServerSocket != null && !connectedServerSocket.isClosed()) {
                connectedServerSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
