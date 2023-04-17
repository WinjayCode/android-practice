package com.winjay.practice.net.socket.websocket;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.Constants;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityWebsocketBinding;
import com.winjay.practice.utils.LogUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocket学习
 *
 * @author Winjay
 * @date 2023-04-17
 */
public class WebSocketActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = WebSocketActivity.class.getSimpleName();

    private ActivityWebsocketBinding binding;

    private MyWebSocketServer myWebSocketServer;
    private MyWebSocketClient myWebSocketClient;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityWebsocketBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.websocketTestBtn.setOnClickListener(this);
        binding.startWebsocketServerBtn.setOnClickListener(this);
        binding.startWebsocketClientBtn.setOnClickListener(this);
        binding.websocketServerSendBtn.setOnClickListener(this);
        binding.websocketClientSendBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
        }
        if (myWebSocketServer != null) {
            myWebSocketServer.close();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.websocketTestBtn) {
            WebsocketTest websocketTest = new WebsocketTest();
            websocketTest.startServer();
            websocketTest.startClient();
        }

        if (v == binding.startWebsocketServerBtn) {
            myWebSocketServer = new MyWebSocketServer(new InetSocketAddress(Constants.SOCKET_PORT));
            myWebSocketServer.start();
            myWebSocketServer.setOnWebSocketServerListener(mOnWebSocketServerListener);
        }

        if (v == binding.startWebsocketClientBtn) {
            try {
                myWebSocketClient = new MyWebSocketClient(new URI("ws://localhost:8080"));
                myWebSocketClient.connect();
                myWebSocketClient.setOnWebSocketClientListener(mOnWebSocketClientListener);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (v == binding.websocketServerSendBtn) {
            if (TextUtils.isEmpty(binding.websocketServerEt.getText().toString())) {
                toast("内容不能为空！");
                return;
            }
            myWebSocketServer.sendMessage(binding.websocketServerEt.getText().toString());
        }

        if (v == binding.websocketClientSendBtn) {
            if (TextUtils.isEmpty(binding.websocketClientEt.getText().toString())) {
                toast("内容不能为空！");
                return;
            }
            myWebSocketClient.send(binding.websocketClientEt.getText().toString());
        }
    }

    private final MyWebSocketServer.OnWebSocketServerListener mOnWebSocketServerListener = new MyWebSocketServer.OnWebSocketServerListener() {
        @Override
        public void onOpen() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.websocketServerMsgTv.setText(binding.websocketServerMsgTv.getText() + "\nWebSocketServer open successfully!");
                }
            });
        }

        @Override
        public void onStart() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.websocketServerMsgTv.setText("WebSocketServer start successfully!");
                }
            });
        }

        @Override
        public void onMessage(String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "server: " + message);
                    binding.websocketServerMsgTv.setText(binding.websocketServerMsgTv.getText() + "\nFrom WebSocketClient:" + message);
                }
            });
        }

        @Override
        public void onClose(String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.websocketServerMsgTv.setText("WebSocketServer had been closed, because " + reason);
                }
            });
        }

        @Override
        public void onError(String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "server: " + errorMessage);
                    binding.websocketServerMsgTv.setText(binding.websocketServerMsgTv.getText() + "\nError: " + errorMessage);
                }
            });
        }
    };

    private final MyWebSocketClient.OnWebSocketClientListener mOnWebSocketClientListener = new MyWebSocketClient.OnWebSocketClientListener() {
        @Override
        public void onOpen() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.websocketClientMsgTv.setText("WebSocketClient start successfully!");
                }
            });
        }

        @Override
        public void onMessage(String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "client: " + message);
                    binding.websocketClientMsgTv.setText(binding.websocketClientMsgTv.getText() + "\nFrom WebSocketServer:" + message);
                }
            });
        }

        @Override
        public void onClose(String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.websocketClientMsgTv.setText("WebSocketClient had been closed, because " + reason);
                }
            });
        }

        @Override
        public void onError(String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "client: " + errorMessage);
                    binding.websocketClientMsgTv.setText(binding.websocketClientMsgTv.getText() + "\nError: " + errorMessage);
                }
            });
        }
    };
}
