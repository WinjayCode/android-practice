package com.winjay.practice.net;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
//import com.winjay.practice.net.ftp.FtpTestActivity;
import com.winjay.practice.net.socket.SocketListActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 网络相关
 *
 * @author Winjay
 * @date 2021-07-21
 */
public class NetListActivity extends BaseActivity {
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
//            put("FTP", FtpTestActivity.class);
            put("Socket", SocketListActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_rv = findViewById(R.id.main_rv);
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
