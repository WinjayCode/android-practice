package com.winjay.practice.app_compat_text;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * AppCompatTextView使用
 * <p>
 * AppCompatTextView使用属性
 * app:autoSizeMaxTextSize 设置字体最大值
 * app:autoSizeMinTextSize 设置字体最小值
 * app:autoSizeTextType="uniform"
 * <p>
 * 注:android:layout_width和android:layout_height必须设置值
 * <p>
 *
 * @author Winjay
 * @date 2019-12-24
 */
public class AppCompatTextActivity extends BaseActivity {
    private final String TAG = AppCompatTextActivity.class.getSimpleName();

    @BindView(R.id.app_compat_tv)
    AppCompatTextView appCompatTextView;

    int i = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.app_compat_text_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
    }

    @OnClick(R.id.hehe)
    void hehe(View view) {
        Toast.makeText(this, "hehe", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.haha)
    void haha(View view) {
        Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
    }

    private void test() {
        for (i = 0; i < 10; i++) {
            int delayTime = (i + 1) * 1000;
            LogUtil.d(TAG, "delayTime=" + delayTime);
            appCompatTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String content = appCompatTextView.getText().toString() + "你好";
                    LogUtil.d(TAG, "content=" + content);
                    appCompatTextView.setText(content);
                }
            }, delayTime);
        }

        appCompatTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (i = 0; i < 9; i++) {
                    int delayTime = (i + 1) * 1000;
                    LogUtil.d(TAG, "delayTime=" + delayTime);
                    appCompatTextView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String content = appCompatTextView.getText().toString();
                            content = content.substring(0, content.length() - 2);
                            appCompatTextView.setText(content);
                        }
                    }, delayTime);
                }
            }
        }, 12000);
    }
}
