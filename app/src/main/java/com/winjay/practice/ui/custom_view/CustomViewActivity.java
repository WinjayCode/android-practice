package com.winjay.practice.ui.custom_view;

import android.animation.Animator;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // View已经初始化完毕，宽高已经准备好了，此时可以获取view的宽高。
        if (hasFocus) {
            int width = ovalIV.getMeasuredWidth();
            int height = ovalIV.getMeasuredHeight();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 将一个runnable投递到消息队列的尾部，然后等待Looper调用此runnable的时候，View已经初始化好了
        ovalIV.post(new Runnable() {
            @Override
            public void run() {
                int width = ovalIV.getMeasuredWidth();
                int height = ovalIV.getMeasuredHeight();
            }
        });

        // View树的状态发生改变时或者View树内部的View的可见性发生改变时，会多次回调
        ViewTreeObserver viewTreeObserver = ovalIV.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ovalIV.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = ovalIV.getMeasuredWidth();
                int height = ovalIV.getMeasuredHeight();
            }
        });
    }
}
