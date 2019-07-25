package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hailong.biometricprompt.fingerprint.FingerprintCallback;
import com.hailong.biometricprompt.fingerprint.FingerprintVerifyManager;

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
                Log.d(TAG, "onHwUnavailable()");
            }

            @Override
            public void onNoneEnrolled() {
                Log.d(TAG, "onNoneEnrolled()");
            }

            @Override
            public void onSucceeded() {
                Log.d(TAG, "onSucceeded()");
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed()");
                Toast.makeText(LauncherActivity.this, "你是谁？", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUsepwd() {
                Log.d(TAG, "onUsepwd()");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel()");
                finish();
            }
        }).build();
    }
}
