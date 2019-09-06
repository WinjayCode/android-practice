package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.winjay.practice.cardview.CardViewActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.constrain_layout.ConstrainLayoutActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.websocket.WebsocketTest;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Winjay
 * @date 2019/3/21
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
    }

    /**
     * Websocket
     */
    @OnClick(R.id.websocket)
    void websocketTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebsocketTest websocketTest = new WebsocketTest();
                websocketTest.startServer();
                websocketTest.startClient();
            }
        }).start();
    }

    /**
     * so库的使用
     */
    @OnClick(R.id.so_use)
    void soTest() {
        Intent intent = new Intent(this, SOActivity.class);
        startActivity(intent);
    }

    /**
     * Kotlin
     *
     * @param view
     */
    public void kotlinTest(View view) {
        Intent intent = new Intent(this, KotlinTestActivity.class);
        startActivity(intent);
    }

    /**
     * ContentProvider
     *
     * @param view
     */
    public void provider(View view) {
        Intent intent = new Intent(this, ProviderActivity.class);
        startActivity(intent);
    }

    /**
     * ConstrainLayout
     *
     * @param view
     */
    public void constrainLayout(View view) {
        Intent intent = new Intent(this, ConstrainLayoutActivity.class);
        startActivity(intent);
    }

    /**
     * Location
     *
     * @param view
     */
    public void location(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void intentFilter(View view) {
        Intent intent = new Intent();
        intent.setAction("com.winjay.practice.action_1");
        intent.addCategory("com.winjay.practice.category_1");
        intent.setDataAndType(Uri.parse("file://abc"), "text/plain");
        startActivity(intent);
    }

    /**
     * CardView
     *
     * @param view
     */
    public void cardview(View view) {
        Intent intent = new Intent(this, CardViewActivity.class);
        startActivity(intent);
    }

    public void test(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }
}
