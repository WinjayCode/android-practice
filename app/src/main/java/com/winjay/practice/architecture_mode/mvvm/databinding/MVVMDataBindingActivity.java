package com.winjay.practice.architecture_mode.mvvm.databinding;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.winjay.practice.R;
import com.winjay.practice.databinding.MvvmDataBindActivityBinding;


/**
 * MVVM(DataBinding)
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVVMDataBindingActivity extends AppCompatActivity {
    MvvmDataBindActivityBinding mvvmDataBindActivityBinding;
    private MVVMBean mMVVMBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 每个数据绑定布局文件都会生成一个绑定类，ViewDataBinding的实例名是根据布局文件名来生成，将之改为首字母大写的驼峰命名法来命名，并省略布局文件名包含的下划线
        mvvmDataBindActivityBinding = DataBindingUtil.setContentView(this, R.layout.mvvm_data_bind_activity);
        mMVVMBean = new MVVMBean("这是MVVM架构DataBinging的数据!");
        // 使用findViewById会报空指针错误
        mvvmDataBindActivityBinding.mvvmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvvmDataBindActivityBinding.setBean(mMVVMBean);
            }
        });
    }
}
