package com.winjay.mirrorcast.car.server;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.winjay.mirrorcast.common.BaseActivity;
import com.winjay.mirrorcast.databinding.ActivityTipsBinding;
import com.winjay.mirrorcast.util.HandlerManager;
import com.winjay.mirrorcast.util.LogUtil;

/**
 * @author Winjay
 * @date 2022-11-30
 */
public class TipsActivity extends BaseActivity {
    private static final String TAG = TipsActivity.class.getSimpleName();
    private ActivityTipsBinding binding;

    private final float startBrightness = 0.2f;

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
                setBrightness(startBrightness);

                HandlerManager.getInstance().postDelayedOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        goHome();
                    }
                }, 150);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);
        changeBrightness();
    }

//    @Override
//    public void onBackPressed() {
//        // mask back button
//    }

    private void changeBrightness() {
        setBrightness(startBrightness);
        ValueAnimator animator = ValueAnimator.ofFloat(startBrightness, 0f);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float value = (float) animator.getAnimatedValue();
                setBrightness(value);
            }
        });
        animator.start();
    }

    public void setBrightness(float paramFloat) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = paramFloat;
        getWindow().setAttributes(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeBrightness();
        }
        return super.onTouchEvent(event);
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
