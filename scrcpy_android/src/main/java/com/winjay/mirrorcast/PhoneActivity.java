package com.winjay.mirrorcast;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.aoa.PhoneAOAActivity;
import com.winjay.mirrorcast.app_socket.AppSocketServerManager;
import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityPhoneBinding;
import com.winjay.mirrorcast.util.LogUtil;
import com.winjay.mirrorcast.wifidirect.WIFIDirectActivity;

/**
 * @author Winjay
 * @date 2023-03-31
 */
public class PhoneActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = PhoneActivity.class.getSimpleName();

    private ActivityPhoneBinding binding;

    @Override
    protected View viewBinding() {
        binding = ActivityPhoneBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        initView();

        AppSocketServerManager.getInstance().startServer();
    }

    private void initView() {
        binding.btnWifiP2p.setOnClickListener(this);
        binding.btnAoa.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // wifi p2p
        if (view == binding.btnWifiP2p) {
            startActivity(WIFIDirectActivity.class);
        }
        // aoa
        if (view == binding.btnAoa) {
            startActivity(PhoneAOAActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        AppSocketServerManager.getInstance().stopServer();
    }
}
