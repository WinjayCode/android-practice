package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.winjay.practice.utils.LogUtil;

import java.util.concurrent.Executor;

/**
 * 指纹验证
 *
 * @author winjay
 * @date 2019-07-25
 */
public class LauncherActivity extends AppCompatActivity {
    private final static String TAG = LauncherActivity.class.getSimpleName();
    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            // No error detected.
            case BiometricManager.BIOMETRIC_SUCCESS:
                LogUtil.d(TAG, "BIOMETRIC_SUCCESS");
                showBiometricPrompt();
                break;
            // The hardware is unavailable. Try again later.
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                LogUtil.d(TAG, "BIOMETRIC_ERROR_HW_UNAVAILABLE");
                gotoMainActivity();
                break;
            // There is no biometric hardware.
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                LogUtil.d(TAG, "BIOMETRIC_ERROR_NO_HARDWARE");
                gotoMainActivity();
                break;
            // The user does not have any biometrics enrolled.
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                LogUtil.d(TAG, "BIOMETRIC_ERROR_NONE_ENROLLED");
                gotoMainActivity();
                break;
        }
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("身份验证")
//                .setSubtitle("SubTitle")
                .setNegativeButtonText("取消")
                .build();
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                LogUtil.d(TAG, "errorCode=" + errorCode + ", errString=" + errString);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                LogUtil.d(TAG);
                gotoMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                LogUtil.d(TAG);
                finish();
            }
        });
        biometricPrompt.authenticate(promptInfo);
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
