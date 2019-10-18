package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hailong.biometricprompt.fingerprint.FingerprintCallback;
import com.hailong.biometricprompt.fingerprint.FingerprintVerifyManager;
import com.winjay.practice.utils.LogUtil;

/**
 * 指纹验证
 *
 * @author winjay
 * @date 2019-07-25
 */
public class LauncherActivity extends AppCompatActivity {
    private final static String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FingerprintVerifyManager.Builder builder = new FingerprintVerifyManager.Builder(LauncherActivity.this);
        builder.callback(new FingerprintCallback() {
            @Override
            public void onHwUnavailable() {
                LogUtil.d(TAG, "onHwUnavailable()");
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNoneEnrolled() {
                LogUtil.d(TAG, "onNoneEnrolled()");
            }

            @Override
            public void onSucceeded() {
                LogUtil.d(TAG, "onSucceeded()");
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed() {
                LogUtil.d(TAG, "onFailed()");
                Toast.makeText(LauncherActivity.this, "你是谁？", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUsepwd() {
                LogUtil.d(TAG, "onUsepwd()");
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "onCancel()");
                finish();
            }
        }).build();
    }
}
