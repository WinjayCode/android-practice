package com.winjay.practice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.winjay.practice.websocket.WebsocketTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Websocket
     */
    public void websocketTest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebsocketTest websocketTest = new WebsocketTest();
                websocketTest.startServer();
                websocketTest.startClient();
            }
        }).start();
    }
}
