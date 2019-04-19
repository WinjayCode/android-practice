package com.winjay.practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.winjay.practice.so.SOActivity;
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

    /**
     * so库的使用
     */
    public void soTest(View view) {
        Intent intent = new Intent(this, SOActivity.class);
        startActivity(intent);
    }
}
