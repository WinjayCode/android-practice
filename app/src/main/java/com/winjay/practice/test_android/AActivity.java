package com.winjay.practice.test_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

public class AActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a);
    }

    public void start(View view) {
//        Intent intent = new Intent(this, BActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(new Intent(this, BActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("dwj", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("dwj", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("dwj", "onDestroy()");
    }
}
