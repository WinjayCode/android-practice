package com.winjay.practice.svg;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * SVG学习
 *
 * @author Winjay
 * @date 2020/10/13
 */
public class SVGActivity extends BaseActivity {
    private static final String TAG = SVGActivity.class.getSimpleName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.svg_iv)
    AppCompatImageView svg_iv;

    @BindView(R.id.planet_svg_iv)
    AppCompatImageView planet_svg_iv;

    @BindView(R.id.search_svg_iv)
    AppCompatImageView search_svg_iv;

    @Override
    protected int getLayoutId() {
        return R.layout.svg_activity;
    }

    @OnClick(R.id.svg_iv)
    void animate() {
        LogUtil.d(TAG, "animate()");
        Drawable drawable = svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        planet();
        search();
    }

    @OnClick(R.id.planet_svg_iv)
    void planet() {
        LogUtil.d(TAG, "planet()");
        Drawable drawable = planet_svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    @OnClick(R.id.search_svg_iv)
    void search() {
        LogUtil.d(TAG, "search()");
        Drawable drawable = search_svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
}
