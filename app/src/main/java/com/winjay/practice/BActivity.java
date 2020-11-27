package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;

public class BActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b);
    }

    public void start(View view) {
        Intent intent = new Intent(this, CActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
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
