package com.winjay.practice.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;

/**
 * Websocket学习
 *
 * @author Winjay
 * @date 2019/3/21
 */
public class WebsocketTest {
    private static final String TAG = WebsocketTest.class.getSimpleName();

    MockWebServer mockWebServer = new MockWebServer();
    ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    /**
     * 启动服务端
     */
    public void startServer() {
        mockWebServer.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "server onOpen");
                Log.d(TAG, "server request header:" + response.request().headers());
                Log.d(TAG, "server response header:" + response.headers());
                Log.d(TAG, "server response:" + response);
            }

            @Override
            public void onMessage(final WebSocket webSocket, String text) {
                Log.d(TAG, "server onMessage");
                Log.d(TAG, "message:" + text);
                if ("command 1".equals(text)) {
                    writeExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            webSocket.send("replay command 1");
                        }
                    });
                } else if ("command 2".equals(text)) {
                    writeExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            webSocket.send("ping from server...");
                        }
                    });
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "server onClosing");
                Log.d(TAG, "code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "server onClosed");
                Log.d(TAG, "code:" + code + " reason:" + reason);
                webSocket.close(1001, "server over!");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.d(TAG, "server onFailure");
                Log.d(TAG, "throwable:" + t);
                Log.d(TAG, "response:" + response);
            }
        }));
    }

    /**
     * 启动客户端
     */
    public void startClient() {
        String wsUrl = "ws://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
        Log.d(TAG, "wsUrl=" + wsUrl);
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(wsUrl).build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(final WebSocket webSocket, Response response) {
                Log.d(TAG, "client onOpen");
                Log.d(TAG, "client request header:" + response.request().headers());
                Log.d(TAG, "client response header:" + response.headers());
                Log.d(TAG, "client response:" + response);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webSocket.send("command 1");
                    }
                }, 5000);
            }

            @Override
            public void onMessage(final WebSocket webSocket, String text) {
                Log.d(TAG, "client onMessage");
                Log.d(TAG, "message:" + text);
                if ("replay command 1".equals(text)) {
                    writeExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            webSocket.send("command 2");
                        }
                    });
                } else if ("ping from server...".equals(text)) {
                    writeExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            webSocket.close(1000, "client over!");
                        }
                    });
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "client onClosing");
                Log.d(TAG, "code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "client onClosed");
                Log.d(TAG, "code:" + code + " reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.d(TAG, "client onFailure");
                Log.d(TAG, "throwable:" + t);
                Log.d(TAG, "response:" + response);
            }
        });
    }
}
