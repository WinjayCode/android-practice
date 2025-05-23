package com.winjay.practice.net.socket.udp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * @author Winjay
 * @date 2021-08-17
 */
public class UdpMainActivity extends BaseActivity {
    private static final String TAG = UdpMainActivity.class.getSimpleName();

    EditText ipEt;

    EditText portEt;

    @Override
    protected int getLayoutId() {
        return R.layout.udp_main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ipEt = findViewById(R.id.ip_et);
        portEt = findViewById(R.id.port_et);
        findViewById(R.id.bind_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind();
            }
        });
    }

    void bind() {
        if (TextUtils.isEmpty(ipEt.getText().toString())) {
            toast("输入目的IP！");
            return;
        }
        if (TextUtils.isEmpty(portEt.getText().toString())) {
            toast("输入目的端口！");
            return;
        }
        Intent intent = new Intent(UdpMainActivity.this, UdpTestActivity.class);
        intent.putExtra("ip", ipEt.getText().toString());
        intent.putExtra("port", portEt.getText().toString());
        startActivity(intent);
    }
}
