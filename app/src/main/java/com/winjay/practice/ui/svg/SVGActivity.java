package com.winjay.practice.ui.svg;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

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

    AppCompatImageView svg_iv;

    AppCompatImageView planet_svg_iv;

    AppCompatImageView search_svg_iv;

    @Override
    protected int getLayoutId() {
        return R.layout.svg_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        svg_iv = findViewById(R.id.svg_iv);
        svg_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate();
            }
        });
        planet_svg_iv = findViewById(R.id.planet_svg_iv);
        planet_svg_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planet();
            }
        });
        search_svg_iv = findViewById(R.id.search_svg_iv);
        search_svg_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    void animate() {
        LogUtil.d(TAG, "animate()");
        Drawable drawable = svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        planet();
        search();
    }

    void planet() {
        LogUtil.d(TAG, "planet()");
        Drawable drawable = planet_svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    void search() {
        LogUtil.d(TAG, "search()");
        Drawable drawable = search_svg_iv.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
}
