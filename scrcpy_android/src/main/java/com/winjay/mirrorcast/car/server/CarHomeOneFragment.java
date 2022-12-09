package com.winjay.mirrorcast.car.server;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.mirrorcast.BaseFragment;
import com.winjay.mirrorcast.databinding.FragmentCarHomeOneBinding;
import com.winjay.mirrorcast.util.LogUtil;

public class CarHomeOneFragment extends BaseFragment<FragmentCarHomeOneBinding> {
    private static final String TAG = CarHomeOneFragment.class.getSimpleName();

    @Override
    protected FragmentCarHomeOneBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentCarHomeOneBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void lazyLoad() {
    }
}
