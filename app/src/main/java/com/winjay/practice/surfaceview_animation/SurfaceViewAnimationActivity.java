package com.winjay.practice.surfaceview_animation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SurfaceViewAnimationActivity extends BaseActivity {
    public static final String TAG = SurfaceViewAnimationActivity.class.getSimpleName();

    @BindView(R.id.fsv)
    FrameSurfaceView mFrameSurfaceView;

    @BindView(R.id.fsv_2)
    FrameSurfaceView mFrameSurfaceView2;

    @BindView(R.id.pause)
    Button pauseBtn;

    @BindView(R.id.resume)
    Button resumeBtn;

    @BindView(R.id.test)
    ConstraintLayout testCL;

    @BindView(R.id.root_rl)
    ConstraintLayout root_rl;

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

    private List<Integer> mBitmapList2 = Arrays.asList(
//            R.raw._8_hanbao00,
//            R.raw._8_hanbao01,
//            R.raw._8_hanbao02,
//            R.raw._8_hanbao03,
//            R.raw._8_hanbao04,
//            R.raw._8_hanbao05,
//            R.raw._8_hanbao06,
//            R.raw._8_hanbao07,
//            R.raw._8_hanbao08,
//            R.raw._8_hanbao09,
//            R.raw._8_hanbao10
            R.raw._8_hanbao11,
            R.raw._8_hanbao12,
            R.raw._8_hanbao13,
            R.raw._8_hanbao14,
            R.raw._8_hanbao15,
            R.raw._8_hanbao16,
            R.raw._8_hanbao17,
            R.raw._8_hanbao18,
            R.raw._8_hanbao19,
            R.raw._8_hanbao20
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

    private List<Integer> mBitmapList3 = Collections.singletonList(
            R.raw._8_hanbao20
    );

    @Override
    protected int getLayoutId() {
        return R.layout.surfaceview_animation_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrameSurfaceView.setBitmapIds(mBitmapList);
        mFrameSurfaceView.setRepeatCount(FrameSurfaceView.INFINITE);

        mFrameSurfaceView.start();

        List<FrameSurfaceView.FrameAnimation> frameAnimationList = new ArrayList<>();
        FrameSurfaceView.FrameAnimation frameAnimation1 = new FrameSurfaceView.FrameAnimation(AnimationResUtil.getOneStar(), 2);
        FrameSurfaceView.FrameAnimation frameAnimation2 = new FrameSurfaceView.FrameAnimation(AnimationResUtil.getTwoStar(), 1);
        FrameSurfaceView.FrameAnimation frameAnimation3 = new FrameSurfaceView.FrameAnimation(AnimationResUtil.getThreeStar(), FrameSurfaceView.INFINITE);
        frameAnimationList.add(frameAnimation1);
        frameAnimationList.add(frameAnimation2);
        frameAnimationList.add(frameAnimation3);
        mFrameSurfaceView2.playSequentially(frameAnimationList);
        mFrameSurfaceView2.start();


//        testCL.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ConstraintSet set = new ConstraintSet();
//                set.clone(root_rl);
//                set.setMargin(testCL.getId(), ConstraintSet.TOP, 100);
//                set.applyTo(root_rl);
//
//                testCL.setVisibility(View.VISIBLE);
//            }
//        }, 5000);
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

    private boolean mSwitch = false;

    @OnClick(R.id.replace)
    void replaceAnim() {
        LogUtil.d(TAG, "replaceAnim()");
        if (mSwitch) {
            mSwitch = false;
            mFrameSurfaceView.reset();
            mFrameSurfaceView.setBitmapIds(mBitmapList);
            mFrameSurfaceView.start();
        } else {
            mSwitch = true;
            mFrameSurfaceView.reset();
            mFrameSurfaceView.setBitmapIds(mBitmapList3);
            mFrameSurfaceView.start();
        }
    }

    @OnClick(R.id.show_hide)
    void showOrHide() {
        if (mFrameSurfaceView.getVisibility() == View.VISIBLE) {
            mFrameSurfaceView.setVisibility(View.GONE);
        } else if (mFrameSurfaceView.getVisibility() == View.GONE) {
            mFrameSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFrameSurfaceView.destroy();
        mFrameSurfaceView2.destroy();
    }
}
