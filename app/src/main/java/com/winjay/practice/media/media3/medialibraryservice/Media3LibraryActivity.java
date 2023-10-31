package com.winjay.practice.media.media3.medialibraryservice;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.MainActivityBinding;

/**
 * Media3服务端使用MediaLibraryService
 *
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3LibraryActivity extends BaseActivity {
    private static final String TAG = Media3LibraryActivity.class.getSimpleName();
    private MainActivityBinding binding;


    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = MainActivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.mainRv.setLayoutManager(new LinearLayoutManager(this));

    }



    @Override
    protected void onDestroy() {


        super.onDestroy();
    }
}
