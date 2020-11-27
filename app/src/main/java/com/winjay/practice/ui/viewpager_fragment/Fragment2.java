package com.winjay.practice.ui.viewpager_fragment;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseFragment;
import com.winjay.practice.utils.LogUtil;

/**
 * Fragment2
 *
 * @author Winjay
 * @date 2020/4/23
 */
public class Fragment2 extends BaseFragment {
    private static final String TAG = Fragment2.class.getSimpleName();

    @Override
    protected int setContentView() {
        return R.layout.two_fragment;
    }

    @Override
    protected void lazyLoad() {
        LogUtil.d(TAG, "lazyLoad()");
    }
}
