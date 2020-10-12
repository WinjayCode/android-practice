package com.winjay.practice.custom_view;

import android.animation.Animator;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自定义view展示
 *
 * @author Winjay
 * @date 2020/8/25
 */
public class CustomViewActivity extends BaseActivity {
    @BindView(R.id.oval)
    ImageView ovalIV;

    @BindView(R.id.rect)
    ImageView rectIV;

    @Override
    protected int getLayoutId() {
        return R.layout.custom_view_activity;
    }

    @OnClick(R.id.oval)
    void ovalClick() {
        // 以圆的形式展示、揭示开来
        Animator animator = ViewAnimationUtils.createCircularReveal(
                ovalIV,
                ovalIV.getWidth() / 2,
                ovalIV.getHeight() / 2,
                ovalIV.getWidth(),
                0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2000);
        animator.start();
    }

    @OnClick(R.id.rect)
    void rectClick() {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                rectIV,
                0,
                0,
                0,
                // Math.hypot（计算一直角三角形的斜边长度）
                (float) Math.hypot(rectIV.getWidth(), rectIV.getHeight()));
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(2000);
        animator.start();
    }
}
