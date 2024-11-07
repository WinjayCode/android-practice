package com.winjay.practice;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.practice.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * @author Winjay
 * @date 2024-07-13
 */
public class KeepLiveActivity extends AppCompatActivity {
    private final String TAG = KeepLiveActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.d(TAG, "开启KeepLiveActivity");
//        setContentView(R.layout.test);
        //左上角显示
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        //设置为1像素大小
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
//        params.width = 1;
//        params.height = 1;
        window.setContentView(R.layout.test);
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        window.setAttributes(params);
        KeepLiveManager.getInstance().setKeepLiveActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "关闭KeepLiveActivity");
    }

    public static class KeepLiveManager {
        private static final KeepLiveManager ourInstance = new KeepLiveManager();

        public static KeepLiveManager getInstance() {
            return ourInstance;
        }

        private KeepLiveManager() {
        }

        //弱引用，防止内存泄漏
        private WeakReference reference;

        public void setKeepLiveActivity(KeepLiveActivity activity) {
            reference = new WeakReference<>(activity);
        }
    }
}
