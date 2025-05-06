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
import com.winjay.practice.ipc.binder.BinderStudyClientActivity;
import com.winjay.practice.ipc.content_provider.ProviderActivity;
import com.winjay.practice.ipc.messenger.MessengerActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * IPC
 *
 * @author Winjay
 * @date 2022-04-11
 */
public class IPCListActivity extends BaseActivity {
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("AIDL", BookManagerClientActivity.class);
            put("Binder_Study", BinderStudyClientActivity.class);
            put("Messenger", MessengerActivity.class);
            put("ContentProvider", ProviderActivity.class);
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
                Intent intent = new Intent(IPCListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }
}
