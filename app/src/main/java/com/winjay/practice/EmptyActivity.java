package com.winjay.practice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;

/**
 * 空页面
 *
 * @author Winjay
 * @date 2020/4/9
 */
public class EmptyActivity extends AppCompatActivity {
    private static final String TAG = EmptyActivity.class.getSimpleName();
    private String content = "今天天气怎么样苹果英文怎么说";
    private int i = 0;
    private int j = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate()111");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        LogUtil.d(TAG, "onCreate()222");

        // 唐诗体测试
//        MyVerticalTextView a = findViewById(R.id.aaa);
//        a.setMaxLine(1);
//        a.setLineMaxCharNum(7);
//        a.setText("今天天气怎么样");
//        MyVerticalTextView vt = findViewById(R.id.vt);
//        vt.setLineMaxCharNum(7);
//        vt.setMaxLine(2);

//        AssetManager mgr = getAssets();
//        Typeface tf = Typeface.createFromAsset(mgr, "fonts/new.ttf");
//        a.setTypeface(tf);
//        vt.setTypeface(tf);

//        a.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (i = 0; i < 14; i++) {
//                    vt.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            vt.setText(content.substring(0, j + 1));
////                            vt.setTextContent(content.substring(0, j + 1));
////                            vt.requestLayout();
////                            vt.invalidate();
//                            ++j;
//                        }
//                    }, i * 1000);
//                }
//            }
//        }, 2000);
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume()111");
        super.onResume();
        LogUtil.d(TAG, "onResume()222");
        // 统计布局渲染时间
        final long start = System.currentTimeMillis();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                LogUtil.d(TAG, "onRender cost:" + (System.currentTimeMillis() - start));
                return false;
            }
        });
    }

    private long costTime() {
        long time = 0;
        Intent intent = getIntent();
        if (intent.hasExtra("time")) {
            long startTime = intent.getLongExtra("time", 0);
            time = System.currentTimeMillis() - startTime;
        }
        return time;
    }
}
