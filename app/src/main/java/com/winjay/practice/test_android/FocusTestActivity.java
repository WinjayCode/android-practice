package com.winjay.practice.test_android;

import android.view.View;

import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityFocusTestBinding;

/**
 * Focus test
 *
 * @author Winjay
 * @date 2026/1/13
 */
public class FocusTestActivity extends BaseActivity {
    private ActivityFocusTestBinding binding;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityFocusTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
