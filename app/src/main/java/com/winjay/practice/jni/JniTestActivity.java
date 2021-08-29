package com.winjay.practice.jni;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import butterknife.BindView;

/**
 * JNI和NDK学习
 *
 * @author Winjay
 * @date 2021-08-29
 */
public class JniTestActivity extends BaseActivity {
    private static final String TAG = "JniTestActivity";

    @BindView(R.id.jni_test_tv)
    TextView jni_test_tv;

    static {
        LogUtil.d(TAG, "load so library: jni-test.so");
        System.loadLibrary("jni-test");
    }

    public native String get();

    public native void set(String str);

    @Override
    protected int getLayoutId() {
        return R.layout.jni_test_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jni_test_tv.setText(get());
        set("jni ndk");
    }
}
