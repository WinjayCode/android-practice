package com.winjay.practice.so;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.winjay.practice.R;

/**
 * so库调用学习
 *
 * 注意几个地方，一、包名要和so库中的包名一样；二、类名也要一致。
 * 函数名为Java_包名类名函数名。所以我们创建的Android工程的包名也要一致。
 * 同时，负责加载并且提供native方法的类的类名也要相同，SOActivity，否则调用不成功，提示找不到方法。
 *
 * @author Winjay
 * @date 2019/4/17
 */
public class SOActivity extends AppCompatActivity {
    public static final String TAG = "SOActivity";

    /**
     * 加载so库
     */
    static{
        Log.d(TAG, "load so library: libJNITest.so");
        System.loadLibrary("JNITest");
    }

    /**
     * so库中的方法
     * @return
     */
    public static native int num();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so);

        Toast.makeText(this, "" + SOActivity.num(), Toast.LENGTH_SHORT).show();
    }
}