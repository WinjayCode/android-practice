package com.winjay.mirrorcast.car.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.winjay.mirrorcast.BaseActivity;
import com.winjay.mirrorcast.R;
import com.winjay.mirrorcast.databinding.ActivityCarHomeBinding;
import com.winjay.mirrorcast.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author F2848777
 * @date 2022-11-24
 */
public class CarLauncherActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CarLauncherActivity.class.getSimpleName();

    private ActivityCarHomeBinding binding;

    private TimeReceiver mTimeReceiver;
    private SimpleDateFormat mSimpleDateFormat;
    private Date mDate;

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected View viewBinding() {
        binding = ActivityCarHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);

        handleShortcut();

        handleTime();

        initHomeViewPager();
    }

    private void initHomeViewPager() {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(new CarHomeOneFragment());
        fragments.add(new CarHomeTwoFragment());
        binding.homeVp.setOffscreenPageLimit(1);
        binding.homeVp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        binding.homeVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                LogUtil.d(TAG, "onPageSelected()_position=" + position);
                if (position == 0) {
                    binding.dotOneIv.setImageResource(R.mipmap.dot_selected);
                    binding.dotTwoIv.setImageResource(R.mipmap.dot);
                    binding.homeIv.setImageResource(R.mipmap.app_list);
                } else if (position == 1) {
                    binding.dotOneIv.setImageResource(R.mipmap.dot);
                    binding.dotTwoIv.setImageResource(R.mipmap.dot_selected);
                    binding.homeIv.setImageResource(R.mipmap.home);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void handleShortcut() {
        binding.voiceIv.setOnClickListener(this);
        binding.navigationIv.setOnClickListener(this);
        binding.tiktokIv.setOnClickListener(this);
        binding.phoneIv.setOnClickListener(this);
        binding.homeIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.voiceIv) {
            toast("语音助手");
        }
        if (v == binding.navigationIv) {
            toast("导航");
        }
        if (v == binding.tiktokIv) {
            toast("在线音乐");
        }
        if (v == binding.phoneIv) {
            toast("通话");
        }
        if (v == binding.homeIv) {
            if (binding.homeVp.getCurrentItem() == 0) {
                binding.homeVp.setCurrentItem(1);
            } else {
                binding.homeVp.setCurrentItem(0);
            }
        }
    }

    private void handleTime() {
        registerTimeReceiver();

        mSimpleDateFormat = new SimpleDateFormat("HH:mm");
        mDate = new Date(System.currentTimeMillis());
        binding.timeTv.setText(mSimpleDateFormat.format(mDate));
    }

    private void registerTimeReceiver() {
        if (mTimeReceiver == null) {
            mTimeReceiver = new TimeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(mTimeReceiver, filter);
        }
    }

    private void unregisterTimeReceiver() {
        if (mTimeReceiver != null) {
            unregisterReceiver(mTimeReceiver);
        }
    }

    private class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                mDate = new Date(System.currentTimeMillis());
                binding.timeTv.setText(mSimpleDateFormat.format(mDate));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        unregisterTimeReceiver();
    }
}
