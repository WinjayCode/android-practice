package com.winjay.practice.material_design;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Material Design
 *
 * @author Winjay
 * @date 2020/9/22
 */
public class MaterialDesignActivity extends Activity {
    private static final String TAG = MaterialDesignActivity.class.getSimpleName();

    @BindView(R.id.tv_rect)
    TextView tv_rect;

    @BindView(R.id.tv_circle)
    TextView tv_circle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_design_activity);
        ButterKnife.bind(this);
        paletteTest();
        clippingTest();
    }

    /**
     * Palette来提取颜色
     */
    private void paletteTest() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onGenerated(@Nullable Palette palette) {
                if (palette != null) {
                    LogUtil.d(TAG, "111");
                    // 通过Paleete来获取对应的色调
                    Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                    if (vibrant != null) {
                        LogUtil.d(TAG, "222");
                        getActionBar().setBackgroundDrawable(new ColorDrawable(vibrant.getRgb()));
                        Window window = getWindow();
                        window.setStatusBarColor(vibrant.getRgb());
                    }
                }
            }
        });
    }

    /**
     * Clipping裁剪
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
