package com.winjay.practice.ui.viewpager_fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager + Fragment
 *
 * @author Winjay
 * @date 2020/4/23
 */
public class ViewPagerActivity extends BaseActivity {
    private static final String TAG = ViewPagerActivity.class.getSimpleName();

    ViewPager mVP;

    @Override
    protected int getLayoutId() {
        return R.layout.viewpager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVP = findViewById(R.id.vp);
        initData();
    }

    private void initData() {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(new Fragment1());
        fragments.add(new Fragment2());
        mVP.setOffscreenPageLimit(0);
        mVP.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        mVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.d(TAG, "onPageSelected()_position=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
