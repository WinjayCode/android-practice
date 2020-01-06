package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.winjay.practice.app_compat_text.AppCompatTextActivity;
import com.winjay.practice.cardview.CardViewActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.constrain_layout.ConstrainLayoutActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.directory_structure.DirectoryStructureActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.websocket.WebsocketTest;

import butterknife.OnClick;

/**
 * @author Winjay
 * @date 2019/3/21
 */
public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume()");

//        Intent intent = new Intent(this, TestService.class);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
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

    /**
     * AppCompatTextView
     *
     * @param view
     */
    public void textview(View view) {
        Intent intent = new Intent(this, AppCompatTextActivity.class);
        startActivity(intent);
    }

    public void download(View view) {
        Intent intent = new Intent(this, DownloadManagerActivity.class);
        startActivity(intent);
    }

    public void directory(View view) {
        Intent intent = new Intent(this, DirectoryStructureActivity.class);
        startActivity(intent);
    }

    public void test(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }
}
