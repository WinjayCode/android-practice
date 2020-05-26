package com.winjay.practice.surfaceview_animation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.winjay.practice.MainActivity;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SurfaceViewAnimationActivity extends BaseActivity {
    public static final String TAG = SurfaceViewAnimationActivity.class.getSimpleName();

    @BindView(R.id.fsv)
    FrameSurfaceView mFrameSurfaceView;

    @BindView(R.id.pause)
    Button pauseBtn;

    @BindView(R.id.resume)
    Button resumeBtn;

    private List<Integer> mBitmapList = Arrays.asList(
            R.raw._8_hanbao00,
            R.raw._8_hanbao01,
            R.raw._8_hanbao02,
            R.raw._8_hanbao03,
            R.raw._8_hanbao04,
            R.raw._8_hanbao05,
            R.raw._8_hanbao06,
            R.raw._8_hanbao07,
            R.raw._8_hanbao08,
            R.raw._8_hanbao09,
            R.raw._8_hanbao10
//            R.raw._8_hanbao11,
//            R.raw._8_hanbao12,
//            R.raw._8_hanbao13,
//            R.raw._8_hanbao14,
//            R.raw._8_hanbao15,
//            R.raw._8_hanbao16,
//            R.raw._8_hanbao17,
//            R.raw._8_hanbao18,
//            R.raw._8_hanbao19,
//            R.raw._8_hanbao20,
//            R.raw._8_hanbao21,
//            R.raw._8_hanbao22,
//            R.raw._8_hanbao23,
//            R.raw._8_hanbao24,
//            R.raw._8_hanbao25,
//            R.raw._8_hanbao26,
//            R.raw._8_hanbao27,
//            R.raw._8_hanbao28,
//            R.raw._8_hanbao29,
//            R.raw._8_hanbao30,
//            R.raw._8_hanbao31
    );

    @Override
    protected int getLayoutId() {
        return R.layout.surfaceview_animation_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrameSurfaceView.setBitmapIds(mBitmapList);
        mFrameSurfaceView.setDuration(125 * mBitmapList.size());
//        mFrameSurfaceView.setRepeatTimes(FrameSurfaceView.INFINITE);

        mFrameSurfaceView.start();mFrameSurfaceView.pauseDrawThread();
    }

    @OnClick(R.id.pause)
    void pauseAnim() {
        LogUtil.d(TAG, "pause()");
        mFrameSurfaceView.pause();
    }

    @OnClick(R.id.resume)
    void resumeAnim() {
        LogUtil.d(TAG, "resume()");
        mFrameSurfaceView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFrameSurfaceView.destroy();
    }
}
