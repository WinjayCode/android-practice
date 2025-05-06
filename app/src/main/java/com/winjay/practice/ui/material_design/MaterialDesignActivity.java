package com.winjay.practice.ui.material_design;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.palette.graphics.Palette;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

/**
 * Material Design
 *
 * @author Winjay
 * @date 2020/9/22
 */
public class MaterialDesignActivity extends Activity {
    private static final String TAG = MaterialDesignActivity.class.getSimpleName();

    AppCompatTextView tv_rect;

    AppCompatTextView tv_circle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_design_activity);
        tv_rect = findViewById(R.id.tv_rect);
        tv_circle = findViewById(R.id.tv_circle);
        paletteTest();
        clippingTest();
    }

    /**
     * Palette来提取颜色
     */
    private void paletteTest() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        Palette.from(bitmap).maximumColorCount(16).generate(palette -> {
            if (palette != null) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    LogUtil.d(TAG, "getDominantSwatch->getRgb");
                    getActionBar().setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
                    Window window = getWindow();
                    window.setStatusBarColor(swatch.getRgb());
                }
            }
        });
    }

    /**
     * Clipping裁剪
     */
    private void clippingTest() {
        ViewOutlineProvider viewOutlineProvider1 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 修改outline为特定形状
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        };

        ViewOutlineProvider viewOutlineProvider2 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 修改outline为特定形状
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        };

        tv_rect.setOutlineProvider(viewOutlineProvider1);
        tv_circle.setOutlineProvider(viewOutlineProvider2);
    }
}
