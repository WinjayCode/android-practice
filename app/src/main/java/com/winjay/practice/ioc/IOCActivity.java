package com.winjay.practice.ioc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.bind.BindHelper;
import com.winjay.bind.Unbinder;
import com.winjay.practice.R;

/**
 * 注解测试类
 *
 * @author Winjay
 * @date 2020/7/14
 */
public class IOCActivity extends AppCompatActivity {
    private static final String TAG = IOCActivity.class.getSimpleName();

    TextView mIOCTV;

    ImageView mIOCIV;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ioc_activity);
        mUnbinder = BindHelper.bind(this);

        mIOCTV = findViewById(R.id.ioc_tv);
        mIOCTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hahaha(v);
            }
        });
        mIOCIV = findViewById(R.id.ioc_iv);
        mIOCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hahaha(v);
            }
        });
        mIOCTV.setText("注解测试");
    }

    void hahaha(View view) {
        if (view.getId()  == R.id.ioc_tv) {
            Toast.makeText(this, "文本点击事件", Toast.LENGTH_SHORT).show();
        } else if (view.getId()  == R.id.ioc_iv) {
            Toast.makeText(this, "图片点击事件", Toast.LENGTH_SHORT).show();
        }
    }

//    @OnClick(R.id.ioc_tv)
//    void haha(View view) {
//        Toast.makeText(this, "文本点击事件", Toast.LENGTH_SHORT).show();
//    }
//
//    @OnClick(R.id.ioc_iv)
//    void hehe(View view) {
//        Toast.makeText(this, "图片点击事件", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
