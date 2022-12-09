package com.winjay.mirrorcast.car.server;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityTipsBinding;
import com.winjay.mirrorcast.util.LogUtil;

/**
 * @author F2848777
 * @date 2022-11-30
 */
public class TipsActivity extends BaseActivity {
    private static final String TAG = TipsActivity.class.getSimpleName();
    private ActivityTipsBinding binding;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected View viewBinding() {
        binding = ActivityTipsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);
        binding.backgroundLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return phone launcher
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // mask back button
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
