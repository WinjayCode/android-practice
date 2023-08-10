package com.winjay.practice.hardware_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.hardware_test.mic.MicTestActivity;
import com.winjay.practice.hardware_test.multi_touch.MultiTouchActivity;
import com.winjay.practice.hardware_test.touch_panel.TouchPanelCheckActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * Hardware Test
 *
 * @author Winjay
 * @date 2022-07-27
 */
public class HardwareTestListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("MultiTouch", MultiTouchActivity.class);
            put("Touch Panel Check", TouchPanelCheckActivity.class);
            put("Mic Test", MicTestActivity.class);
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
                Intent intent = new Intent(HardwareTestListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }
}
