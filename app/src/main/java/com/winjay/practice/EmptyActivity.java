package com.winjay.practice;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.view.MyVerticalTextView;
import com.winjay.practice.view.VerticalTextView;

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

//        ScrollView scrollView = findViewById(R.id.sv);
//        LinearLayout linearLayout = findViewById(R.id.content_list_ll);
//        linearLayout.removeAllViews();
//        scrollView.setVisibility(View.GONE);
//
//        for (int i = 0; i < 50; i++) {
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 40);
//            linearLayout.addView(generateHistoricalEventsTextView("哈哈哈哈哈"), lp);
//        }
//        scrollView.setVisibility(View.VISIBLE);

        MyVerticalTextView a = findViewById(R.id.aaa);
        a.setMaxLine(1);
        a.setLineMaxCharNum(7);
        a.setText("今天天气怎么样");
        MyVerticalTextView vt = findViewById(R.id.vt);
        vt.setLineMaxCharNum(7);
        vt.setMaxLine(2);

//        AssetManager mgr = getAssets();
//        Typeface tf = Typeface.createFromAsset(mgr, "fonts/new.ttf");
//        a.setTypeface(tf);
//        vt.setTypeface(tf);

        a.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (i = 0; i < 14; i++) {
                    vt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            vt.setText(content.substring(0, j + 1));
//                            vt.setTextContent(content.substring(0, j + 1));
//                            vt.requestLayout();
//                            vt.invalidate();
                            ++j;
                        }
                    }, i * 1000);
                }
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "11111111111111111");
                startActivity(new Intent(EmptyActivity.this, MainActivity.class));
            }
        }, 10000);
    }

    private TextView generateHistoricalEventsTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 64);
        textView.setLineSpacing(0, 1.2f);
        return textView;
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
