package com.winjay.practice.media.image;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityImagePlayBinding;
import com.winjay.practice.media.MediaCollectionHelper.ImageBean;

/**
 * 图片展示
 *
 * @author Winjay
 * @date 2022-10-26
 */
public class ImagePlayActivity extends BaseActivity {

    private ActivityImagePlayBinding binding;
    private RequestOptions mGlideOptions;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityImagePlayBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("image")) {
            ImageBean imageBean = getIntent().getParcelableExtra("image");
            if (imageBean != null) {
                mGlideOptions = new RequestOptions()
                        .placeholder(R.mipmap.icon);

                Glide.with(this)
                        .load(imageBean.getUri())
                        .apply(mGlideOptions)
                        .into(binding.imagePlayIv);
            }
        }
    }
}
