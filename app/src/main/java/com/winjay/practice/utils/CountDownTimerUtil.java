package com.winjay.practice.utils;

import android.os.CountDownTimer;

/**
 * 倒计时工具类
 *
 * @author Winjay
 * @date 2019/11/4
 */
public class CountDownTimerUtil {
    private static final String TAG = CountDownTimerUtil.class.getSimpleName();

    private CountDownTimer mCountDownTimer;
    private OnCountDownListener mOnCountDownListener;
    private long millisInFuture = 10 * 1000;
    private long countDownInterval = 1000;


//    public static CountDownTimerUtil getCountDownTimerUtil() {
//        return new CountDownTimerUtil();
//    }

    public CountDownTimerUtil setMillisInFuture(long millisInFuture) {
        this.millisInFuture = millisInFuture;
        return this;
    }

    public CountDownTimerUtil setCountDownInterval(long countDownInterval) {
        this.countDownInterval = countDownInterval;
        return this;
    }

    /**
     * 创建对象
     */
    private void create() {
        LogUtil.d(TAG, "create()");
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mOnCountDownListener != null) {
                    mOnCountDownListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (mOnCountDownListener != null) {
                    mOnCountDownListener.onFinish();
                }
            }
        };
    }

    /**
     * 取消倒计时
     */
    public void cancel() {
        LogUtil.d(TAG, "cancel()");
        if (mCountDownTimer == null) {
            create();
        }
        mCountDownTimer.cancel();
    }

    /**
     * 开始倒计时
     */
    public void start() {
        LogUtil.d(TAG, "start()");
        if (mCountDownTimer == null) {
            create();
        }
        mCountDownTimer.start();
    }

    /**
     * 销毁对象
     */
    public void destroy() {
        if (mCountDownTimer != null) {
            LogUtil.d(TAG, "destroy()");
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mOnCountDownListener = null;
    }

    public interface OnCountDownListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    public void setOnCountDownListener(OnCountDownListener mOnCountDownListener) {
        this.mOnCountDownListener = mOnCountDownListener;
    }
}
