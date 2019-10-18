package com.winjay.practice.cardview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.MainActivity;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import butterknife.OnClick;

/**
 * CardView使用
 * <p>
 * cardView属性
 * app:cardCornerRadius 设置圆角的半径
 * app:cardElevation 设置阴影的半径
 * app:cardBackgroundColor=""设置背景色
 * app:cardMaxElevation=""设置Z轴最大高度值
 * app:cardUseCompatPadding=""是否使用CompatPadding
 * app:cardPreventCornerOverlap=""是否使用PreventCornerOverlap
 * app:contentPadding=""内容的Padding
 * app:contentPaddingTop=""内容的上Padding
 * app:contentPaddingLeft=""内容的左Padding
 * app:contentPaddingRight=""内容的右Padding
 * app:contentPaddingBottom=""内容的下Padding
 * <p>
 *
 * @author Winjay
 * @date 2019-09-03
 */
public class CardViewActivity extends BaseActivity {
    private final String TAG = CardViewActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.card_view_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
    }

    @OnClick(R.id.card_view_tv)
    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
    }
}
