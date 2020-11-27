package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;

public class CActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c);
    }

    public void start(View view) {
        startActivity(new Intent(this, DActivity.class));
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
