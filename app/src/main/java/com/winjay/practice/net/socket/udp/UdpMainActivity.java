package com.winjay.practice.net.socket.udp;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Winjay
 * @date 2021-08-17
 */
public class UdpMainActivity extends BaseActivity {
    private static final String TAG = UdpMainActivity.class.getSimpleName();

    @BindView(R.id.ip_et)
    EditText ipEt;

    @BindView(R.id.port_et)
    EditText portEt;

    @Override
    protected int getLayoutId() {
        return R.layout.udp_main_activity;
    }

    @OnClick(R.id.bind_btn)
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
