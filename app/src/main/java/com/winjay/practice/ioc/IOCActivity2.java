package com.winjay.practice.ioc;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.annotations.BindView;
import com.winjay.bind.BindHelper;
import com.winjay.bind.Unbinder;
import com.winjay.practice.R;

/**
 * 注解测试类2
 *
 * @author Winjay
 * @date 2020/7/14
 */
public class IOCActivity2 extends AppCompatActivity {
    private static final String TAG = IOCActivity2.class.getSimpleName();

//    @BindView(R.id.ioc2_tv)
//    TextView mIOCTV;
//
//    @BindView(R.id.ioc2_iv)
//    ImageView mIOCIV;

    private Unbinder mUnbinder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ioc_activity2);
        mUnbinder = BindHelper.bind(this);

//        mIOCTV.setText("注解测试");
    }

//    @OnClick(R.id.ioc_tv)
//    @NoDoubleClick(1000)
//    @CheckNet
//    private void OnClick(View view) {
//        Toast.makeText(this, "点击事件", Toast.LENGTH_SHORT).show();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
