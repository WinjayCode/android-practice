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

    private static TextView jni_call_java_tv;

    @BindView(R.id.java_call_jni_tv)
    TextView java_call_jni_tv;

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
        jni_call_java_tv = findViewById(R.id.jni_call_java_tv);
        java_call_jni_tv.setText(get());
        set("jni ndk");
    }

    public static void methodCalledByJni(String msgFromJni) {
        LogUtil.d(TAG, "msg=" + msgFromJni);
        jni_call_java_tv.setText(msgFromJni);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jni_call_java_tv = null;
    }
}
