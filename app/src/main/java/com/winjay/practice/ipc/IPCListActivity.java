package com.winjay.practice.ipc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.ipc.aidl.BookManagerClientActivity;
import com.winjay.practice.ipc.messenger.MessengerActivity;
import com.winjay.practice.jetpack.lifecycle.LifecycleTestActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * IPC
 *
 * @author Winjay
 * @date 2022-04-11
 */
public class IPCListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("AIDL", BookManagerClientActivity.class);
            put("Messenger", MessengerActivity.class);
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
                Intent intent = new Intent(IPCListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }
}
