package com.winjay.practice.utils;

import android.view.View;

import java.util.Calendar;

/**
 * 非暴力点击
 *
 * @author winjay
 * @date 2019/6/24
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    private long delayTime = 1000;
    private long lastClickTime = 0;

    public NoDoubleClickListener(long delayTime) {
        this.delayTime = delayTime;
    }

    public NoDoubleClickListener() {
    }

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > delayTime) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    /**
     * 非暴力点击
     *
     * @param v
     */
    protected abstract void onNoDoubleClick(View v);
}
