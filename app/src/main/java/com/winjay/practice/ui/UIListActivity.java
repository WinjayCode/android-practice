package com.winjay.practice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.EmptyActivity;
import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.ui.app_compat_text.AppCompatTextActivity;
import com.winjay.practice.ui.cardview.CardViewActivity;
import com.winjay.practice.ui.constraint_layout.ConstraintLayoutActivity;
import com.winjay.practice.ui.custom_view.CustomViewActivity;
import com.winjay.practice.ui.drawable.DrawableActivity;
import com.winjay.practice.ui.imageloader.ImageLoaderActivity;
import com.winjay.practice.ui.material_design.MaterialDesignActivity;
import com.winjay.practice.ui.remote_views.RemoteViewsNotificationActivity;
import com.winjay.practice.ui.surfaceview_animation.SurfaceViewAnimationActivity;
import com.winjay.practice.ui.svg.SVGActivity;
import com.winjay.practice.ui.toolbar.ToolbarActivity;
import com.winjay.practice.ui.viewpager_fragment.ViewPagerActivity;
import com.winjay.practice.ui.window.WindowActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * UI集合
 *
 * @author Winjay
 * @date 2021-04-29
 */
public class UIListActivity extends BaseActivity {
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("ConstrainLayout", ConstraintLayoutActivity.class);
            put("Drawable", DrawableActivity.class);
            put("CardView", CardViewActivity.class);
            put("AppCompatTextView", AppCompatTextActivity.class);
            put("SurfaceViewAnimation", SurfaceViewAnimationActivity.class);
            put("ViewPager+Fragment", ViewPagerActivity.class);
            put("MaterialDesign", MaterialDesignActivity.class);
            put("Toolbar", ToolbarActivity.class);
            put("SVG", SVGActivity.class);
            put("RemoteViews", RemoteViewsNotificationActivity.class);
            put("CustomView", CustomViewActivity.class);
            put("ImageLoader", ImageLoaderActivity.class);
            put("Window", WindowActivity.class);
            put("EmptyPage", EmptyActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_rv = findViewById(R.id.main_rv);
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                Intent intent = new Intent(UIListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }

    /**
     * 根据文件名来加载view
     */
    private void getViewByLayoutName() {
        int layoutId = getResources().getIdentifier("main_activity", "layout", getPackageName());
        View view = getLayoutInflater().inflate(layoutId, main_rv, false);
    }
}
