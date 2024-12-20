package com.winjay.practice.ui.viewpager_fragment;

import android.view.View;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseFragment;
import com.winjay.practice.utils.LogUtil;

/**
 * Fragment1
 *
 * @author Winjay
 * @date 2020/4/23
 */
public class Fragment1 extends BaseFragment {
    private static final String TAG = Fragment1.class.getSimpleName();

    @Override
    protected int setContentView() {
        return R.layout.one_fragment;
    }

    @Override
    protected void lazyLoad() {
        LogUtil.d(TAG, "lazyLoad()");
        View tv = findViewById(R.id.fragment_1_tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG, "onClick");
            }
        });
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtil.d(TAG, "onLongClick");
                return true;
            }
        });
    }
}
