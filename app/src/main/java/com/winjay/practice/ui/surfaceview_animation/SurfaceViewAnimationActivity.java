package com.winjay.practice.ui.surfaceview_animation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SurfaceViewAnimationActivity extends BaseActivity {
    public static final String TAG = SurfaceViewAnimationActivity.class.getSimpleName();

    FrameSurfaceView mFrameSurfaceView;

    FrameSurfaceView mFrameSurfaceView2;

    Button pauseBtn;

    Button resumeBtn;

    ConstraintLayout testCL;

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
        mFrameSurfaceView = findViewById(R.id.fsv);
        mFrameSurfaceView2 = findViewById(R.id.fsv_2);
        pauseBtn = findViewById(R.id.pause);
        resumeBtn = findViewById(R.id.resume);
        testCL = findViewById(R.id.test);
        root_rl = findViewById(R.id.root_rl);
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

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAnim();
            }
        });
        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeAnim();
            }
        });
        findViewById(R.id.replace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceAnim();
            }
        });
        findViewById(R.id.show_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHide();
            }
        });
    }

    void pauseAnim() {
        LogUtil.d(TAG, "pause()");
        mFrameSurfaceView.pause();
    }

    void resumeAnim() {
        LogUtil.d(TAG, "resume()");
        mFrameSurfaceView.resume();
    }

    private boolean mSwitch = false;

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

    void showOrHide() {
        if (mFrameSurfaceView.getVisibility() == View.VISIBLE) {
            mFrameSurfaceView.setVisibility(View.GONE);
        } else if (mFrameSurfaceView.getVisibility() == View.GONE) {
            mFrameSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        // view销毁放到super.onDestroy();之前，因为super.onDestroy();中调用了unbinder.unbind();销毁了view
        mFrameSurfaceView.destroy();
        mFrameSurfaceView2.destroy();
        super.onDestroy();
    }
}
