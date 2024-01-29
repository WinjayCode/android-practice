package com.winjay.mirrorcast;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.car.server.TipsActivity;
import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityMainBinding;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

/**
 * @author Winjay
 * @date 2022-11-09
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    @Override
    protected View viewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected String[] permissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET};
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);

        initView();
    }

    private void initView() {
        binding.vehicleBtn.setOnClickListener(this);
        binding.phoneBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.vehicleBtn) {
            startActivity(VehicleActivity.class);

//            int virtualDisplay = DisplayUtil.createVirtualDisplay(0);
//            LogUtil.d(TAG, "id: " + virtualDisplay);
        }
        if (v == binding.phoneBtn) {
            startActivity(PhoneActivity.class);

//            startActivity(TipsActivity.class);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
