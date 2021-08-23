package com.winjay.practice.net.socket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.net.socket.websocket.WebsocketTest;
import com.winjay.practice.net.socket.udp.UdpMainActivity;
import com.winjay.practice.thread.HandlerManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * Socket学习集合
 *
 * @author Winjay
 * @date 2021-07-21
 */
public class SocketListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("WebSocket", null);
            put("UDP", UdpMainActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                switch (key) {
                    case "WebSocket":
                        HandlerManager.getInstance().postOnSubThread(new Runnable() {
                            @Override
                            public void run() {
                                WebsocketTest websocketTest = new WebsocketTest();
                                websocketTest.startServer();
                                websocketTest.startClient();
                            }
                        });
                        break;
                    default:
                        Intent intent = new Intent(SocketListActivity.this, mainMap.get(key));
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}
