package com.winjay.practice.net.socket;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.net.socket.tcp.TCPTestActivity;
import com.winjay.practice.net.socket.udp.UdpMainActivity;
import com.winjay.practice.net.socket.websocket.WebSocketActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * Socket学习集合
 *
 * 网络套接字（socket）是一种在计算机网络上实现进程间通信或主机间通信的机制。它是由统一标准的 API（应用程序编程接口）组成，可以让开发者在不同操作系统上使用相同的代码。通过创建套接字并对其属性进行配置，应用程序可以发送和接收数据来进行网络通信。
 *
 * 套接字是一个端点，它在计算机网络上唯一标识一个特定的进程或主机。每个套接字有一个 IP 地址和一个端口号，它们一起标识了套接字的位置。当两个套接字建立连接时，它们之间的通信就可以开始了。
 *
 * 套接字 API 通常包括以下函数：socket()、bind()、listen()、accept()、connect()、send()、recv() 等。这些函数可以让开发者完成套接字的创建、绑定、监听、接受连接、发消息、收消息等操作。
 *
 * 套接字可以基于不同的传输层协议实现，其中最常见的是 TCP 和 UDP 协议，这两种协议都使用 IP 协议进行数据传输。
 *
 * @author Winjay
 * @date 2021-07-21
 */
public class SocketListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("TCP", TCPTestActivity.class);
            put("UDP", UdpMainActivity.class);
            put("WebSocket", WebSocketActivity.class);
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
                startActivity(mainMap.get(key));
            }
        });
    }
}
