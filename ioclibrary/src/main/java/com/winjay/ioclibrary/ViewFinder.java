package com.winjay.ioclibrary;

import android.app.Activity;
import android.view.View;

/**
 * 复制类
 *
 * @author Winjay
 * @date 2020/7/12
 */
public class ViewFinder {
    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }

    public ViewFinder(View view) {
        this.mView = view;
    }

    public View findViewById(int viewId) {
        return mActivity != null ? mActivity.findViewById(viewId) : mView.findViewById(viewId);
    }
}
