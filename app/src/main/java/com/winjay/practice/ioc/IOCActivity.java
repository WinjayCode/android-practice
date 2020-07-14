package com.winjay.practice.ioc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.ioclibrary.BindView;
import com.winjay.ioclibrary.BindViewUtils;
import com.winjay.ioclibrary.CheckNet;
import com.winjay.ioclibrary.NoDoubleClick;
import com.winjay.ioclibrary.OnClick;
import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

/**
 * 注解测试类
 *
 * @author Winjay
 * @date 2020/7/14
 */
public class IOCActivity extends AppCompatActivity {
    private static final String TAG = IOCActivity.class.getSimpleName();

    @BindView(R.id.ioc_tv)
    TextView ioc_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ioc_activity);
        BindViewUtils.bind(this);
    }

    @OnClick(R.id.ioc_tv)
    @NoDoubleClick(1000)
    @CheckNet
    private void OnClick(View view) {
        LogUtil.d(TAG, "111");
    }
}
