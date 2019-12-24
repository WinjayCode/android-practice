package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.winjay.practice.utils.LogUtil;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.winjay.practice.cardview.CardViewActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.constrain_layout.ConstrainLayoutActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.websocket.WebsocketTest;
import com.youdao.sdk.app.YouDaoApplication;
import com.youdao.zhiyun.sdk.fingerdetect.FingerDetectManager;
import com.youdao.zhiyun.sdk.fingerdetect.FingerDetectParams;
import com.youdao.zhiyun.sdk.fingerdetect.OCRListener;
import com.youdao.zhiyun.sdk.fingerdetect.OcrErrorCode;
import com.youdao.zhiyun.sdk.fingerdetect.QuestionOCRDetailResult;
import com.youdao.zhiyun.sdk.fingerdetect.WordOCRDetailResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public void test(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);

//        YouDaoApplication.init(this, "appkey");
//        FingerDetectParams params = new FingerDetectParams.Builder()
//                .fingerType("word")
//                .timeout(10000)
//                .build();
//        FingerDetectManager.getIntance(params).startRecognize("", new OCRListener() {
//            @Override
//            public void onError(OcrErrorCode ocrErrorCode) {
//                LogUtil.d(TAG, "code=" + ocrErrorCode.getCode() + ",msg=" + ocrErrorCode.toString());
//            }
//
//            @Override
//            public void onResult(String result, WordOCRDetailResult wordOCRDetailResult, QuestionOCRDetailResult questionOCRDetailResult) {
//                LogUtil.d(TAG, "result=" + result);
//            }
//        });
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
